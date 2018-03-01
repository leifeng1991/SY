package com.xxzlkj.zhaolinshare.base.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 小数点位数的过滤器(默认保存两位小数点)
 *
 * @author zhangrq
 */
public class LengthInputFilter implements InputFilter {

    /**
     * 输入框小数的位数
     */
    private int decimalDigits = 2;

    public LengthInputFilter() {
    }

    /**
     * @param decimalDigits 小数位数
     */
    public LengthInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    /**
     * 此方法对单个字符进行控制
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        // 删除等特殊字符，直接返回
        if ("".equals(source.toString())) {// source 新输入的单个内容
            return null;
        }
        String dValue = dest.toString();// dest原来输入的内容（比如：输入123，dest为12，source为3）
        // 开头不能输入.
        if ("".equals(dValue) && ".".equals(source)) {
            return "";
        }
        // 开头不能输入00.
        if ("0".equals(dValue) && "0".equals(source)) {
            return "";
        }


        String[] splitArray = dValue.split("\\.");
        if (splitArray.length > 1) {
            //输入的指针在小数点前面，可以继续输入，返回null，代表不控制
            if (dstart < dest.length() - splitArray[1].length()) {
                return null;
            }
            // 原小数点的值得长度+1=新小数点的长度
            int dotLength = splitArray[1].length() + 1;
            if (dotLength > decimalDigits) {
                // 小数点超过规定的长度
                return "";
            }
        }
        return null;// 返回null，代表不控制
    }

}
