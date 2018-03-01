package com.xxzlkj.zhaolinshare.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.xxzlkj.zhaolinshare.base.config.BaseGlobalParams;


/**
 * 单例的吐司
 *
 * @author zhangrq
 */
public class ToastManager {
    private static Toast toast;
    private static boolean isDebug = BaseGlobalParams.isDebug;
    private static TextView tv;

    public ToastManager() {
    }

    public static void showShortToast(Context context, String message) {
        toast = getToast(context);
        setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void showShortToast(Context context, int resId) {
        toast = getToast(context);
        setText(resId);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void showLongToast(Context context, String message) {
        toast = getToast(context);
        setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLongToast(Context context, int resId) {
        toast = getToast(context);
        setText(resId);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showMsgToast(Context context, String message) {
        toast = getToast(context);
        setText(TextUtils.isEmpty(message) ? "服务器返回数据错误，请稍后再试" : message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 展示错误提示
     */
    public static void showErrorToast(Context context, int errorCode, String errorStr) {

        toast = getToast(context);
        String errorMessage = "对不起，获取数据错误！请检查网络或稍后再试！";
        if (isDebug) {
            // 测试的版本
            errorMessage = errorStr;
        }
        setText(errorMessage);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }

    @SuppressLint("ShowToast")
    private static Toast getToast(Context context) {
        if (toast == null) {
            synchronized (ToastManager.class) {
                if (toast == null) {
                    toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    // 设置归原
                    tv = new TextView(context);
                    tv.setTextColor(Color.WHITE);
                    tv.setPadding(dp2px(context, 10), dp2px(context, 10), dp2px(context, 10), dp2px(context, 10));
                    tv.setBackground(getBackground(context));
                    toast.setView(tv);
                }
            }
        }
        return toast;
    }

    private static void setText(CharSequence message) {
        if (tv != null) {
            tv.setText(message);
        }
    }

    private static void setText(int resId) {
        if (tv != null) {
            setText(tv.getContext().getText(resId));
        }
    }

    private static Drawable getBackground(Context context) {
        int roundRadius = dp2px(context, 8); // 8dp 圆角半径
        int fillColor = Color.parseColor("#bb000000");//内部填充颜色
        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        return gd;
    }

    private static int dp2px(Context context, float dipValue) {
        return (int) (dipValue * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
