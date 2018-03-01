package com.xxzlkj.zhaolinshare.base.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 输入法的工具类
 *
 * @author zhangrq
 */
public class InputMethodUtils {
    private static InputMethodManager inputMethodManager;

    /**
     * 获取输入法Manager
     */
    private static InputMethodManager getInputMethodManager(Context context) {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        return inputMethodManager;
    }

    /**
     * 隐藏Activity页面的软键盘
     *
     * @param activity Activity
     */
    public static boolean hideInputMethod(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        return view != null && getInputMethodManager(activity).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 让EditText失去焦点并隐藏软键盘
     *
     * @param context 上下文
     * @param edit    EditText
     */
    public static boolean hideInputMethod(Context context, EditText edit) {
        edit.clearFocus();
        return getInputMethodManager(context).hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }


    /**
     * 让EditText获取焦点并显示软键盘
     *
     * @param context 上下文
     * @param edit    EditText
     */
    public static boolean showInputMethod(Context context, EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        return getInputMethodManager(context).showSoftInput(edit, 0);
    }

    /**
     * 让EditText获取焦点并切换软键盘
     *
     * @param context 上下文
     * @param edit    EditText
     */
    public static void toggleSoftInput(Context context, EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        getInputMethodManager(context).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
