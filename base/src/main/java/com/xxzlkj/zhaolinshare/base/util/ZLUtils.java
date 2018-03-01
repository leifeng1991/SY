package com.xxzlkj.zhaolinshare.base.util;


import android.os.Build;

import com.xxzlkj.zhaolinshare.base.BuildConfig;
import com.xxzlkj.zhaolinshare.base.app.BaseApplication;
import com.xxzlkj.zhaolinshare.base.listener.OnLoginUserListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/5/2 16:35
 */
public class ZLUtils {

    /**
     * 兆邻的请求头
     */
    public static Map<String, String> getZhaoLinRequestHeader() {
//        App-Key	zhaolin
//        Timestamp	当前时间戳
//        Signature	签名 签名算法 sha1(App-Key连接Nonce连接Timestamp)
//        Phone-Model	手机型号 苹果传iPhone  安卓传android
//        Phone-Version	App版本号

        Map<String, String> map = new HashMap<>();
        String appKeyStr = "zhaolin";
        map.put("App-Key", appKeyStr);
        String currentTimeStr = String.valueOf(System.currentTimeMillis() / 1000);
        map.put("Timestamp", currentTimeStr);
        map.put("Signature", DecriptUtil.encryptSHA1(appKeyStr + "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCpeLgK0vk5POU5PQ" + currentTimeStr));
        map.put("Phone-Model", "android");
        map.put("Phone-Version", BuildConfig.VERSION_NAME);// 当前app版本号
        map.put("Phone-System", Build.VERSION.RELEASE); // 手机系统版本
        map.put("Phone-Brand", Build.BRAND);// 手机品牌类型
        map.put("Phone-Series", Build.MODEL);// 手机品牌型号
        BaseApplication baseApplication = BaseApplication.getBaseApplication();
        map.put("Uid", baseApplication instanceof OnLoginUserListener ? ((OnLoginUserListener) baseApplication).getLoginUserId() : "0");
        return map;
    }
}
