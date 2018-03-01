package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 手机显示屏相关的工具类
 *
 * @author zhangrq
 */
@SuppressWarnings("WeakerAccess")
public class PixelUtil {

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * getDensity(context) + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        return (int) (pxValue / getScaledDensity(context) + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * getScaledDensity(context) + 0.5f);
    }

    /**
     * 获取按比例缩放的屏幕密度
     */
    public static float getScaledDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 获取屏幕密度
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取显示信息
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 给金额显示添加千分位","
     *
     * @param val 金额
     */
    public static String parseMoney(Object val) {
        String pattern = "##,###,##0.00";
        if (val == null || val.equals(""))
            return "";
        String valStr = val + "";
        DecimalFormat df = new DecimalFormat(pattern);
        valStr = df.format(new BigDecimal(valStr));
        return subZeroAndDot(valStr);
    }

    /**
     * 字符串数字的加法
     *
     * @param num1 数字1
     * @param num2 数字2
     */
    public static String stringAdd(String num1, String num2) {
        BigDecimal vNum1 = new BigDecimal(num1);
        BigDecimal vNum2 = new BigDecimal(num2);
        return (vNum1.add(vNum2)).toString();
    }

    /**
     * 字符串数字的减法
     *
     * @param num1 数字1
     * @param num2 数字2
     */
    public static String stringSubstract(String num1, String num2) {
        BigDecimal vNum1 = new BigDecimal(num1);
        BigDecimal vNum2 = new BigDecimal(num2);
        return (vNum1.subtract(vNum2)).toString();
    }

    /**
     * 字符串数字的乘法
     *
     * @param num1 数字1
     * @param num2 数字2
     */
    public static String stringMultiple(String num1, String num2) {
        BigDecimal vNum1 = new BigDecimal(num1);
        BigDecimal vNum2 = new BigDecimal(num2);
        return (vNum1.multiply(vNum2)).toString();
    }

    /**
     * 小数的 取小数点和零 如 12.30 -> 12.3 | 23.00 -> 23
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }

    public static double getWidthScale(Context context) {
        return 1.0 * getScreenWidth(context) / 750;
    }

    public static double getWidthScale(Context context, int width) {
        return 1.0 *  width / getScreenWidth(context);
    }

    public static double getHeightScale(Context context) {
        return 1.0 * getScreenHeight(context) / 1334;
    }
}
