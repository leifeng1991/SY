package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;

/**
 * SharedPreferences的工具类
 *
 * @author zhangrq
 */
public class PreferencesSaver {

    private static final String PREFERENCES_FILE = "com.qiyuan.qianguan.preferences";

    /**
     * 存储String值
     */
    public static void setStringAttr(Context context, String key, String value) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        if (value == null) {
            edit.remove(key);
        } else {
            edit.putString(key, value);
        }
        edit.commit();
    }

    /**
     * 获取String值，没有默认为""
     */
    public static String getStringAttr(Context context, String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    /**
     * 存储boolean值
     */
    public static void setBooleanAttr(Context context, String key, boolean value) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    /**
     * 获取boolean值，没有默认为false
     */
    public static boolean getBooleanAttr(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);

    }

    /**
     * 获取boolean值，没有默认为true
     */
    public static boolean getBooleanAttrDefaultTrue(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, true);
    }

    /**
     * 存储int值
     */
    public static void setIntAttr(Context context, String key, int value) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        edit.putInt(key, value);
        edit.commit();
    }

    /**
     * 获取Int值，没有默认为-1
     */
    public static int getIntAttr(Context context, String key) {
        return getSharedPreferences(context).getInt(key, -1);
    }

    /**
     * 获取Int值，默认值自定义
     */
    public static int getIntAttr(Context context, String key, int defValue) {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    /**
     * 存储long值
     */
    public static void setLongAttr(Context context, String key, long value) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        edit.putLong(key, value);
        edit.commit();
    }

    /**
     * 获取long值，没有默认为-1
     */
    public static long getLongAttr(Context context, String key) {
        return getSharedPreferences(context).getLong(key, -1);
    }

    /**
     * 获取long值，默认值自定义
     */
    public static long getLongAttr(Context context, String key, long defValue) {
        return getSharedPreferences(context).getLong(key, defValue);
    }

    /**
     * 存储集合，以 item,item,item,的形式存储
     */
    public static void putStrList(Context context, String key, ArrayList<String> value) {
        SharedPreferences sp = getSharedPreferences(context);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(value.get(i));
        }

        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, sb.toString());
        edit.commit();
    }

    /**
     * 获取存储的集合
     */
    public static ArrayList<String> getStrList(Context context, String key) {
        String value = getStringAttr(context, key);

        String[] strings;
        if (value.equals("")) {
            strings = new String[0];
        } else {
            strings = value.split(",");
        }

        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, strings);

        return list;
    }

    /**
     * 移除Key
     */
    public static void removeAttr(Context context, String key) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        edit.remove(key);
        edit.commit();
    }

    /**
     * 获取SharedPreferences
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }
}
