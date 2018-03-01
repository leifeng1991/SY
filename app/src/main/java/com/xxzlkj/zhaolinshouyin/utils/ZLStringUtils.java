package com.xxzlkj.zhaolinshouyin.utils;

import android.widget.TextView;

/**
 * 描述:更新
 *
 * @author zhangrq
 *         2017/4/26 16:28
 */
public class ZLStringUtils {
    /**
     * 设置网络状态提示
     */
    public static void setNetStateHint(boolean isAvailable, TextView stateTextView) {
        stateTextView.setText(isAvailable ? "当前状态：网络获取" : "当前状态：本地获取");
    }
}
