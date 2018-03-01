package com.xxzlkj.zhaolinshouyin.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xxzlkj.zhaolinshouyin.R;

import java.lang.reflect.Method;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/7 14:08
 */


public class NumberKeyboardUtil {
    /**
     * @param mEditText      输入的文本
     * @param decimalDigits  输入的小数点位数
     * @param listener       输入完成
     * @param cancelListener 取消/清空回调
     */
    public static void setClick(View rootView, EditText mEditText, int decimalDigits, OnInputCodeListener listener, OnCancelListener cancelListener) {
        if (decimalDigits == 0) {
            // 只能输入整数
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            // 可以输入小数
            mEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mEditText.setFilters(new InputFilter[]{new LengthInputFilter(decimalDigits)});
        }
        // 设置点击编辑框不弹软键盘并且编辑框获焦点
        disableShowInput(mEditText);
        // 数字键
        View[] buttons = new View[15];
        buttons[0] = rootView.findViewById(R.id.btn_num_00);
        buttons[1] = rootView.findViewById(R.id.btn_num_0);
        buttons[2] = rootView.findViewById(R.id.btn_num_1);
        buttons[3] = rootView.findViewById(R.id.btn_num_2);
        buttons[4] = rootView.findViewById(R.id.btn_num_3);
        buttons[5] = rootView.findViewById(R.id.btn_num_4);
        buttons[6] = rootView.findViewById(R.id.btn_num_5);
        buttons[7] = rootView.findViewById(R.id.btn_num_6);
        buttons[8] = rootView.findViewById(R.id.btn_num_7);
        buttons[9] = rootView.findViewById(R.id.btn_num_8);
        buttons[10] = rootView.findViewById(R.id.btn_num_9);
        buttons[11] = rootView.findViewById(R.id.btn_num_delete);
        buttons[12] = rootView.findViewById(R.id.btn_num_clear);
        buttons[13] = rootView.findViewById(R.id.btn_num_config);
        buttons[14] = rootView.findViewById(R.id.btn_num_dot);
        // 点击事件
        for (View button : buttons) {
            button.setOnClickListener(v -> {
                int index = mEditText.getSelectionStart();
                Editable editable = mEditText.getText();
                switch (v.getId()) {
                    case R.id.btn_num_00:// 数字键00
                        editable.insert(index, "00");
                        break;
                    case R.id.btn_num_0:// 数字键0
                        editable.insert(index, "0");
                        break;
                    case R.id.btn_num_1:// 数字键1
                        editable.insert(index, "1");
                        break;
                    case R.id.btn_num_2:// 数字键2
                        editable.insert(index, "2");
                        break;
                    case R.id.btn_num_3:// 数字键3
                        editable.insert(index, "3");
                        break;
                    case R.id.btn_num_4:// 数字键4
                        editable.insert(index, "4");
                        break;
                    case R.id.btn_num_5:// 数字键5
                        editable.insert(index, "5");
                        break;
                    case R.id.btn_num_6:// 数字键6
                        editable.insert(index, "6");
                        break;
                    case R.id.btn_num_7:// 数字键7
                        editable.insert(index, "7");
                        break;
                    case R.id.btn_num_8:// 数字键8
                        editable.insert(index, "8");
                        break;
                    case R.id.btn_num_9:// 数字键9
                        editable.insert(index, "9");
                        break;
                    case R.id.btn_num_delete:// 删除
                        if (index >= 1)
                            editable.delete(index - 1, index);
                        break;
                    case R.id.btn_num_clear:
                        Button btn = (Button) buttons[12];
                        if ("清空".equals(btn.getText().toString())) {
                            // 清空
                            mEditText.setText("");
                        } else {
                            // 取消
                            if (cancelListener != null)
                                cancelListener.cancel();
                        }
                        break;
                    case R.id.btn_num_dot:// 小数点
                        editable.insert(index, ".");
                        break;
                    case R.id.btn_num_config:// 确定
                        if (listener != null)
                            listener.onInputCodeFinish(mEditText.getText().toString().trim(), (TextView) button);
                        break;
                }
            });
        }
    }

    /**
     * 12键 数字键盘
     *
     * @param mEditText     文本输入框
     * @param decimalDigits 输入的小数点位数
     */
    public static void setClick12(View rootView, EditText mEditText, int decimalDigits) {
        if (decimalDigits == 0) {
            // 只能输入整数
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            // 可以输入小数
            mEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mEditText.setFilters(new InputFilter[]{new LengthInputFilter(decimalDigits)});
        }
        // 设置点击编辑框不弹软键盘并且编辑框获焦点
        disableShowInput(mEditText);
        // 数字键
        View[] buttons = new View[12];
        buttons[0] = rootView.findViewById(R.id.btn_num_0);
        buttons[1] = rootView.findViewById(R.id.btn_num_1);
        buttons[2] = rootView.findViewById(R.id.btn_num_2);
        buttons[3] = rootView.findViewById(R.id.btn_num_3);
        buttons[4] = rootView.findViewById(R.id.btn_num_4);
        buttons[5] = rootView.findViewById(R.id.btn_num_5);
        buttons[6] = rootView.findViewById(R.id.btn_num_6);
        buttons[7] = rootView.findViewById(R.id.btn_num_7);
        buttons[8] = rootView.findViewById(R.id.btn_num_8);
        buttons[9] = rootView.findViewById(R.id.btn_num_9);
        buttons[10] = rootView.findViewById(R.id.btn_num_dot);
        buttons[11] = rootView.findViewById(R.id.btn_num_delete);
        // 点击事件
        for (View button : buttons) {
            button.setOnClickListener(v -> {
                int index = mEditText.getSelectionStart();
                Editable editable = mEditText.getText();
                switch (v.getId()) {
                    case R.id.btn_num_0:// 数字键0
                        editable.insert(index, "0");
                        break;
                    case R.id.btn_num_1:// 数字键1
                        editable.insert(index, "1");
                        break;
                    case R.id.btn_num_2:// 数字键2
                        editable.insert(index, "2");
                        break;
                    case R.id.btn_num_3:// 数字键3
                        editable.insert(index, "3");
                        break;
                    case R.id.btn_num_4:// 数字键4
                        editable.insert(index, "4");
                        break;
                    case R.id.btn_num_5:// 数字键5
                        editable.insert(index, "5");
                        break;
                    case R.id.btn_num_6:// 数字键6
                        editable.insert(index, "6");
                        break;
                    case R.id.btn_num_7:// 数字键7
                        editable.insert(index, "7");
                        break;
                    case R.id.btn_num_8:// 数字键8
                        editable.insert(index, "8");
                        break;
                    case R.id.btn_num_9:// 数字键9
                        editable.insert(index, "9");
                        break;
                    case R.id.btn_num_delete:// 删除
                        if (index >= 1)
                            editable.delete(index - 1, index);
                        break;
                    case R.id.btn_num_dot:// 小数点
                        editable.insert(index, ".");
                        break;

                }
            });
        }
    }

    /**
     * 点击编辑框不出软键盘并且有焦点
     */
    public static void disableShowInput(EditText mEditText) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(mEditText, false);
        } catch (Exception e) {
        }
        try {
            method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(mEditText, false);
        } catch (Exception e) {
        }
    }

    public interface OnInputCodeListener {
        /**
         * @param inputCode   输入框输入的内容
         * @param clickButton 按钮
         */
        void onInputCodeFinish(String inputCode, TextView clickButton);
    }

    public interface OnCancelListener {
        void cancel();
    }
}
