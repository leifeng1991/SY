package com.xxzlkj.zhaolinshouyin.utils;

import android.os.Build;

import com.xxzlkj.zhaolinshare.base.util.DecriptUtil;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;

import java.lang.reflect.Method;

/**
 * 获取手机信息
 *
 * @author zhangrq
 */
public class PhoneInfo {
    /**
     * 获取一些设备参数
     */
    private static String getBuildParameter() {
        return Build.BOARD + // 主板
                Build.BRAND + // 系统定制商
                Build.CPU_ABI + // cpu指令集
                Build.DEVICE + // 设备参数
                Build.DISPLAY + // 显示屏参数
                Build.ID + // 修订版本列表
                Build.MANUFACTURER + // 硬件制造商
                Build.MODEL + // 版本即最终用户可见的名称
                Build.PRODUCT; // 整个产品的名称
    }

    /**
     * 移动设备唯一标识 MD5加密了，会返回32位字符串
     */
    public static String getUId() {
        LogUtil.e("序列号：" + getSerialNumber());
        // 为null，生成后保存
        String UId = getBuildParameter() + getSerialNumber();
        // 会生成32位字符串
        for (int i = 0; i < 2; i++) {
            UId = DecriptUtil.encryptMD5(UId);
        }
        return UId;
    }

    /**
     * 序列号
     */
    private static String getSerialNumber() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }


}
