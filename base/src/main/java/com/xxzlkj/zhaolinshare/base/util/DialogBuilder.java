package com.xxzlkj.zhaolinshare.base.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 描述:Dialog的常用功能
 *
 *
 * @author zhangrq
 *         2016/12/8 11:20
 */

public class DialogBuilder {

    private final Dialog dialog;
    private Object obj;

    public DialogBuilder(Activity activity) {
        dialog = new AppCompatDialog(activity);
    }

    public DialogBuilder(Activity activity, int themeResId) {
        dialog = new AppCompatDialog(activity, themeResId);
    }

    public static DialogBuilder create(Activity activity) {
        return new DialogBuilder(activity);
    }

    public static DialogBuilder create(Activity activity, int themeResId) {
        return new DialogBuilder(activity, themeResId);
    }

    public DialogBuilder setView(int layoutResID) {
        dialog.setContentView(layoutResID);
        return this;
    }

    public DialogBuilder setView(View view) {
        dialog.setContentView(view);
        return this;
    }

    public DialogBuilder setView(View view, ViewGroup.LayoutParams params) {
        dialog.setContentView(view, params);
        return this;
    }

    /**
     * 设置dialog的动画样式
     *
     * @param styleId 动画样式
     */
    public DialogBuilder setAnimations(int styleId) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(styleId); // 设置窗口弹出动画
        }
        return this;
    }

    /**
     * 设置dialog的重力
     *
     * @param gravity 设置dialog的重力 例如：Gravity.BOTTOM
     */
    public DialogBuilder setGravity(int gravity) {
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.gravity = gravity; // 设置重力
            window.setAttributes(attributes);
        }
        return this;
    }


    /**
     * 设置dialog的宽度
     *
     * @param scaleScreenWidth 相对于屏幕宽度的比例
     */
    public DialogBuilder setWidthScale(double scaleScreenWidth) {
        return setWidthAndHeight((int) (getScreenWidth() * scaleScreenWidth),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 获取屏幕宽度并设置
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) dialog.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }


    /**
     * 设置dialog的宽、高
     *
     * @param width  dialog的宽
     * @param height dialog的高
     */
    public DialogBuilder setWidthAndHeight(int width, int height) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(null);//必须设置dialog背景,因为默认的dialog样式给设置了距离周围有距离的.9图片
            window.setLayout(width, height);
        }
        return this;
    }

    /**
     * 展示dialog
     */
    public DialogBuilder show() {
        dialog.show();
        return this;
    }

    /**
     * 设置dialog是否可取消
     */
    public DialogBuilder setCancelable(boolean flag) {
        dialog.setCancelable(flag);
        return this;
    }

    /**
     * 设置dialog点击外部区域是否可取消
     */
    public DialogBuilder setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**
     * 获取dialog
     */
    public Dialog getDialog() {
        return dialog;
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public DialogBuilder hide() {
        dialog.hide();
        return this;
    }

    public DialogBuilder dismiss() {
        dialog.dismiss();
        return this;
    }

    /**
     * dialog弹出后显示软键盘
     */
    public DialogBuilder showSoftInput() {
        return setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * dialog设置软键盘模式
     */
    public DialogBuilder setSoftInputMode(int mode) {
        getWindow().setSoftInputMode(mode);
        return this;
    }

    public Window getWindow() {
        return dialog.getWindow();
    }

    public DialogBuilder cancel() {
        dialog.cancel();
        return this;
    }

    /**
     * 获取存入的obj
     */
    public Object getObj() {
        return obj;
    }
    /**
     * 存入的obj
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }
}
