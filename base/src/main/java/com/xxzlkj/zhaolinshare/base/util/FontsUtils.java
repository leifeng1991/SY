package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 设置字体类型工具类
 *
 * @author zhangrq
 */
public class FontsUtils {
    private static Map<String, Typeface> typefaceMap = new HashMap<>();

    /**
     * 获取钱罐项目的字体类型
     *
     * @param context 上下文
     */
    public static Typeface getQGTypeface(Context context) {
//        return getTypeface(context, "fonts/Aileron-Light.otf");
        return getTypeface(context, "fonts/phone.ttf");
    }

    /**
     * 获取字体类型
     *
     * @param context      上下文
     * @param typefacePath Assets目录下的字型路径
     * @return 字体类型
     */
    public static Typeface getTypeface(Context context, String typefacePath) {
        Typeface typeface = typefaceMap.get(typefacePath);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), typefacePath);
            typefaceMap.put(typefacePath, typeface);
        }
        return typeface;
    }

    /**
     * 设置钱罐项目的全局字体样式
     */
    public static void setMyAppTypeface(Context context) {
        setAppTypeface("SERIF", getQGTypeface(context));
        LogUtil.e("=======4",System.currentTimeMillis()+"");
    }

    /**
     * 设置全局字体样式
     *
     * @param oldTypeface 原来设置的字体类型名：DEFAULT、DEFAULT_BOLD、SANS_SERIF、SERIF、MONOSPACE
     * @param newTypeface 要设置的字体类型
     */
    public static void setAppTypeface(String oldTypeface, Typeface newTypeface) {
        try {
            Field field = Typeface.class.getDeclaredField(oldTypeface);
            field.setAccessible(true);
            field.set(null, newTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
