package com.xxzlkj.zhaolinshare.base.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;
import com.xxzlkj.zhaolinshare.base.util.OkHttpUtils;
import com.xxzlkj.zhaolinshare.base.util.ZLUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * @author zhangrq
 */
public class RequestRunnable implements Runnable {

    private static final int EXCEPTION = 1;// 异常(正常的超时等)
    private static final int URL_NULL = 2;// URL地址为空
    private static final int PARSING_JSON_ERROR = 3;// 解析json异常
    public static final int EXIT_LOGIN_USER = 4;// 服务器返回-999，退出登录用户
    private RequestHandler handler;
    private String urlString;
    private String returnContent;
    private Map<String, String> requestParams;

    RequestRunnable(String urlString, Map<String, String> requestParams, RequestHandler handler) {
        this.urlString = urlString;
        this.handler = handler;
        if (requestParams == null)
            requestParams = new HashMap<>();
        this.requestParams = requestParams;
    }

    public void run() {
        handler.notifyStart();
        if (!TextUtils.isEmpty(urlString)) {
            LogUtil.i("请求数据：", urlString);
            try {
                Map<String, String> requestHeader = ZLUtils.getZhaoLinRequestHeader();
                LogUtil.i("请求数据头：", requestHeader.toString());
                LogUtil.i("请求数据参数：", requestParams.toString());
                Response execute = OkHttpUtils.execute(OkHttpUtils.getRequestPostByForm(urlString, requestHeader, requestParams));
                returnContent = execute.body().string();
                int resultCode = execute.code();
                if (resultCode == 200) {
                    // 返回成功的数据
                    LogUtil.i("返回数据：", returnContent);//格式化返回数据
                    Gson gson = new Gson();
                    BaseBean cmtNetBean = gson.fromJson(returnContent, BaseBean.class);
                    if (cmtNetBean != null && "-999".equals(cmtNetBean.getCode())) {
                        // 服务器返回-999   //此账号已在其它设备登录，您将退出登录
                        handler.notifyFail(EXIT_LOGIN_USER, cmtNetBean.getMessage());
                        handler.notifyEnd();
                    } else {
                        // 返回成功的数据
                        Type genType = handler.listener.getClass().getGenericSuperclass();
                        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                        Class clazz = (Class) params[0];
                        Object retObject = gson.fromJson(returnContent, clazz);
                        handler.notifySuccess(retObject);
                        handler.notifyEnd();
                    }
                } else {
                    // 响应码错误，404,500等
                    LogUtil.i("返回数据：", "响应码" + resultCode + "错误" + returnContent);
                    handler.notifyFail(resultCode, resultCode + "错误");
                    handler.notifyEnd();
                }
            } catch (JsonSyntaxException e) {
                //json解析错误
                LogUtil.i("返回数据：", "Json解析异常" + returnContent);
                e.printStackTrace();
                handler.notifyFail(PARSING_JSON_ERROR, "Json解析异常");
                handler.notifyEnd();
            } catch (IOException e) {
                //访问网络错误，超时等
                LogUtil.i("返回数据：", "IOException(无网络、超时等)");
                e.printStackTrace();
                handler.notifyFail(EXCEPTION, "访问网络错误，请检查网络");
                handler.notifyEnd();
            }
        } else {
            //地址为空
            LogUtil.i("返回数据：", "请求地址为空");
            handler.notifyFail(URL_NULL, "请求地址为空");
            handler.notifyEnd();
        }
    }
}