package com.xxzlkj.zhaolinshare.base.util;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 基于OkHttp封住的工具类
 *
 * @author zhangrq
 */
public class OkHttpUtils {
    private static final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).build();

    /**
     * 在当前线程访问网络。（判断返回信息）
     *
     * @param request 请求体
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request          请求体
     * @param responseCallback 成功失败的回调接口
     */
    public static void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 获取GET请求方式的请求体
     *
     * @param url 地址
     */
    public static Request getRequestGet(String url) {
        return getRequestBuilder(url, null).build();
    }


    /**
     * 获取GET请求方式的请求体
     *
     * @param url       地址
     * @param headerMap 请求头，可以为空
     */
    public static Request getRequestGet(String url, Map<String, String> headerMap) {
        return getRequestBuilder(url, headerMap).build();
    }

    /**
     * 获取Post请求方式的请求体
     *
     * @param url         地址
     * @param headerMap   请求头，可以为空
     * @param requestBody 请求内容
     */
    public static Request getRequestPost(String url, Map<String, String> headerMap, RequestBody requestBody) {
        return getRequestBuilder(url, headerMap).post(requestBody).build();
    }

    /**
     * 获取Post请求方式的请求体
     *
     * @param url         地址
     * @param requestBody 请求内容
     */
    public static Request getRequestPost(String url, RequestBody requestBody) {
        return getRequestBuilder(url, null).post(requestBody).build();
    }

    /**
     * 获取请求的builder
     *
     * @param url       地址
     * @param headerMap 请求头，可以为空
     */
    public static Request.Builder getRequestBuilder(String url, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder().url(url);
        if (headerMap != null) {
            for (Map.Entry<String, String> stringStringEntry : headerMap.entrySet()) {
                if (stringStringEntry != null) {
                    builder.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }
        }
        return builder;
    }

    /**
     * okHttp post请求表单提交
     *
     * @param url       接口地址
     * @param headerMap 请求头，可以为空
     * @param paramsMap 请求参数
     */
    public static Request getRequestPostByForm(String url, Map<String, String> headerMap, Map<String, String> paramsMap) {
        //创建一个FormBody.Builder
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            //追加表单信息
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(paramsMap.get(key))) {
                continue;
            }
            builder.add(key, paramsMap.get(key));
        }
        //生成表单实体对象
        RequestBody formBody = builder.build();
        return getRequestPost(url, headerMap, formBody);
    }

    public static void okHttpUploadImage(String uploadUrl, String partName, File file, final Callback callback) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        // 初始化请求体对象，设置Content-Type以及文件数据流
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)   // multipart/form-data
                .addFormDataPart(partName, file.getName(), requestFile)
                .build();
        // 封装OkHttp请求对象，初始化请求参数
        Request request = new Request.Builder()
                .url(uploadUrl)    // 上传url地址
                .post(requestBody)    // post请求体
                .build();
        final OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                .connectTimeout(100, TimeUnit.SECONDS)   // 设置请求超时时间
                .writeTimeout(150, TimeUnit.SECONDS)
                .build();
        // 发起异步网络请求
        okHttpClient.newCall(request).enqueue(callback);
    }

}
