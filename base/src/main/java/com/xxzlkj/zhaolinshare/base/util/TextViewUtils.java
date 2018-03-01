package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 描述:
 *
 * @author zhangrq
 *         2016/11/1 15:56
 */
public class TextViewUtils {

    public static void setText(TextView textView, CharSequence... text) {
        if (textView == null || text == null || text.length == 0)
            return;
        StringBuilder stringBuilder = new StringBuilder();
        for (CharSequence charSequence : text) {
            if (charSequence == null || charSequence.length() == 0)
                charSequence = " ";
            stringBuilder.append(charSequence);
        }
        textView.setText(stringBuilder);
    }

    public static void setTextHasValue(TextView textView, CharSequence... text) {
        if (textView == null || text == null || text.length == 0)
            return;
        StringBuilder stringBuilder = new StringBuilder();
        for (CharSequence charSequence : text) {
            if (charSequence == null || charSequence.length() == 0)
                charSequence = "- -";
            stringBuilder.append(charSequence);
        }
        textView.setText(stringBuilder);
    }

    /**
     * @param context 上下文
     * @param resId   资源id
     * @param gravity 图片位置
     */
    public static void setImageResources(Context context, int resId, int gravity, TextView textView) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        switch (gravity) {
            case Gravity.LEFT:// 左
                textView.setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
                break;
            case Gravity.TOP:// 上
                textView.setCompoundDrawables(compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3]);
                break;
            case Gravity.RIGHT:// 右
                textView.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3]);
                break;
            case Gravity.BOTTOM:// 下
                textView.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], drawable);
                break;
        }

    }
}
