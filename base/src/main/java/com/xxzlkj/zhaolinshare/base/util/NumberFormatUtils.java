package com.xxzlkj.zhaolinshare.base.util;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * 字符串转换格式工具
 *
 * @author zhangrq
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NumberFormatUtils {

    private static String TAG = NumberFormatUtils.class.getSimpleName();

    /**
     * 字符串在转换成int值
     */
    public static int toInt(String value) {
        return tryCatchValueOfMethod(Integer.class, value, "0");
    }

    /**
     * 字符串在转换成int值
     */
    public static int toInt(String value, int errorValue) {
        return tryCatchValueOfMethod(Integer.class, value, errorValue + "");
    }

    /**
     * 字符串在转换成double值
     */
    public static double toDouble(String value) {
        return tryCatchValueOfMethod(Double.class, value, "0");
    }

    /**
     * 字符串在转换成double值
     */
    public static double toDouble(String value, double errorValue) {
        return tryCatchValueOfMethod(Double.class, value, errorValue + "");
    }

    /**
     * 字符串在转换成long值
     */
    public static long toLong(String value) {
        return tryCatchValueOfMethod(Long.class, value, "0");
    }

    /**
     * 字符串在转换成long值
     */
    public static long toLong(String value, long errorValue) {
        return tryCatchValueOfMethod(Long.class, value, errorValue + "");
    }

    /**
     * 字符串在转换成float值
     */
    public static float toFloat(String value) {
        return tryCatchValueOfMethod(Float.class, value, "0");
    }

    /**
     * 字符串在转换成float值
     */
    public static float toFloat(String value, float errorValue) {
        return tryCatchValueOfMethod(Float.class, value, errorValue + "");
    }

    /**
     * 字符串在转换成byte值
     */
    public static byte toByte(String value) {
        return tryCatchValueOfMethod(Byte.class, value, "0");
    }

    /**
     * 字符串在转换成byte值
     */
    public static byte toByte(String value, byte errorValue) {
        return tryCatchValueOfMethod(Byte.class, value, errorValue + "");
    }

    /**
     * 字符串在转换成short值
     */
    public static short toShort(String value) {
        return tryCatchValueOfMethod(Short.class, value, "0");
    }

    /**
     * 字符串在转换成short值
     */
    public static short toShort(String value, short errorValue) {
        return tryCatchValueOfMethod(Short.class, value, errorValue + "");
    }

    @SuppressWarnings("unchecked")
    private static <T> T tryCatchValueOfMethod(Class<T> clazz, String value, String errorValueStr) {
        T errorValue = null;
        try {
            Method valueOfMethod = clazz.getDeclaredMethod("valueOf",
                    String.class);
            errorValue = (T) valueOfMethod.invoke(clazz, errorValueStr);
            return (T) valueOfMethod.invoke(clazz, value);
        } catch (Exception e) {
            Log.e(TAG, "格式转换错误,要转换的值为" + value + ",错误信息如下:", e);
            return errorValue;
        }
    }
}
