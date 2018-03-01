package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.util.DialogBuilder;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.EntryOrdersLeftAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.EntryOrdersRightAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.OrderRefundDesGoodsAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.SelectCashierAdapter;
import com.xxzlkj.zhaolinshouyin.db.GuaDan;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.model.OrderRefundGoods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * 乐行样式弹框
 *
 * @author zhangrq
 */
public class ZLDialogUtil {


    public static Dialog showRawDialog(Activity activity, String message, final OnClickConfirmListener listener) {
        return new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null)
                            listener.onClickConfirm();
                    }
                })
                .show();
    }

    public static AlertDialog showRawDialogOneButton(Activity activity, String message, final OnClickConfirmListener listener) {
        return new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null)
                            listener.onClickConfirm();
                    }
                })
                .show();
    }

    public static Dialog showRawDialogTwoButton(Activity activity, String message, String buttonOneStr, DialogInterface.OnClickListener oneButtonListener, String buttonTwoStr, DialogInterface.OnClickListener twoButtonListener) {
        return new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton(buttonOneStr, oneButtonListener)
                .setPositiveButton(buttonTwoStr, twoButtonListener)
                .show();
    }

    /**
     * 展示数字键盘dialog
     *
     * @param editType   （必传）编辑类型，1：编辑价格，2：编辑数量 3：打印张数
     * @param isStandard （非必传）是否是标品，editType = 2，必传
     */
    public static Dialog showNumberKeyboard(Activity activity, int editType, boolean isStandard, final OnInputCodeListener listener) {
        ViewGroup loadDataView = (ViewGroup) View.inflate(activity, R.layout.dialog_input_code, null);
        EditText mEditText = loadDataView.findViewById(R.id.id_edit_text);
        ImageView mDeleteImageView = loadDataView.findViewById(R.id.id_delete);
        TextView mMessageTextView = loadDataView.findViewById(R.id.id_message);
        mMessageTextView.setVisibility(View.VISIBLE);
        // 1:价格 2：数量 3：打印张数
        mMessageTextView.setText(editType == 1 ? "价格" : editType == 2 ? "数量" : "打印张数");
        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(loadDataView);
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        dialogBuilder.show();
        // 可输入的小数点长度
        int decimalDigits = 0;
        if (editType == 1) {
            // 1：编辑价格
            decimalDigits = 2;
        } else if (editType == 2) {
            // 2：编辑数量
            decimalDigits = isStandard ? 0 : 3;
        } else if (editType == 3) {
            // 3:打印张数
            mEditText.setHint("默认打印张数最大20");
        }

        // 数字点击监听事件
        NumberKeyboardUtil.setClick(loadDataView, mEditText, decimalDigits,
                (inputCode, clickButton) -> {
                    if (TextUtils.isEmpty(inputCode)) {
                        ToastManager.showShortToast(activity.getApplicationContext(), "输入值不能为空");
                    } else {
                        // 输入完成
                        mEditText.setText("");
                        if (listener != null)
                            listener.onInputCodeFinish(inputCode);
                        dialogBuilder.dismiss();
                    }

                },
                dialogBuilder::dismiss); // 输入取消
        mDeleteImageView.setOnClickListener(v -> dialogBuilder.dismiss());
        return dialogBuilder.getDialog();
    }


    /**
     * 检查更新的Dialog
     *
     * @param isForcedUpdate 是否是强制更新
     */
    public static Dialog showUpdateDialog(Activity activity, boolean isForcedUpdate, String title, String message, final OnClickConfirmListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity, R.layout.dialog_update, null);
        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);// 标题
        TextView tv_des = (TextView) rootView.findViewById(R.id.tv_des);// 更新内容
        View rl_no_forced_update = rootView.findViewById(R.id.rl_no_forced_update);// 不强制更新布局
        TextView tv_cancel = (TextView) rootView.findViewById(R.id.tv_cancel);// 取消
        TextView tv_config = (TextView) rootView.findViewById(R.id.tv_config);// 更新
        TextView tv_forced_update = (TextView) rootView.findViewById(R.id.tv_forced_update);// 强制更新
        final DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView)
                .setWidthScale(0.62)
                .setCancelable(!isForcedUpdate)
                .setCanceledOnTouchOutside(!isForcedUpdate)
                .show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        // 通用设置
        tv_title.setText(title);
        tv_des.setText(message);
        // 设置点击
        // 非强制更新
        tv_cancel.setOnClickListener(v -> dialogBuilder.dismiss());
        tv_config.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickConfirm();
            }
            dialogBuilder.dismiss();
        });
        // 强制更新
        tv_forced_update.setOnClickListener(v -> {
            if (listener != null) {
                tv_forced_update.setEnabled(false);
                listener.onClickConfirm();
            }
        });
        // 设置是否强制更新布局
        rl_no_forced_update.setVisibility(isForcedUpdate ? View.GONE : View.VISIBLE);
        tv_forced_update.setVisibility(isForcedUpdate ? View.VISIBLE : View.GONE);

        return dialogBuilder.getDialog();
    }

    /**
     * 展示没有商品的dialog
     */
    public static Dialog showNoGoodsDialog(Activity activity, String goodsCode, String hintStr) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_no_goods, null);
        TextView tv_goods_code = rootView.findViewById(R.id.tv_goods_code);// 货号
        TextView tv_hint = rootView.findViewById(R.id.tv_hint);// 货号
        TextView tv_config = rootView.findViewById(R.id.tv_config);// 按钮-确定

        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView)
                .show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        // 设置值
        tv_goods_code.setText(goodsCode);
        tv_hint.setText(hintStr);
        // 设置点击
        tv_config.setOnClickListener(v -> dialogBuilder.dismiss());
        return dialogBuilder.getDialog();
    }

    /**
     * 选择退货方式弹框
     */
    public static Dialog showSelectSalesTypeDialog(Activity activity, OnClickSureListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_select_sales_type, null);
        RadioGroup mRadioGroup = rootView.findViewById(R.id.id_radio_group);
        Button mCancelButton = rootView.findViewById(R.id.id_cancel);// 取消
        Button mSureButton = rootView.findViewById(R.id.id_sure);// 确定

        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView)
                .show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        // 确定
        mSureButton.setOnClickListener(v -> {
            switch (mRadioGroup.getCheckedRadioButtonId()) {
                case R.id.id_item_return:// 单品退货
                    if (listener != null)
                        listener.onClickSure(1);
                    break;
                case R.id.id_order_return:// 订单退货
                    if (listener != null)
                        listener.onClickSure(2);
                    break;
            }
            dialogBuilder.dismiss();
        });
        // 取消
        mCancelButton.setOnClickListener(v -> dialogBuilder.dismiss());
        return dialogBuilder.getDialog();
    }

    /**
     * 确认取消退货？
     */
    public static Dialog showConfigReturnGoodsDialog(Activity activity, OnClickConfirmListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_config_return_goods, null);
        Button mCancelButton = rootView.findViewById(R.id.id_cancel);// 取消
        Button mSureButton = rootView.findViewById(R.id.id_sure);// 确定

        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView)
                .show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        // 确定
        mSureButton.setOnClickListener(v -> {
            if (listener != null)
                listener.onClickConfirm();
            dialogBuilder.dismiss();
        });
        // 取消
        mCancelButton.setOnClickListener(v -> dialogBuilder.dismiss());
        return dialogBuilder.getDialog();
    }

    /**
     * 现金退款弹框
     *
     * @param refundableAmount 应退金额
     * @param listener         点击确定按钮回调
     */
    public static Dialog showCashRefundDialog(Activity activity, double refundableAmount, OnClickCashRefundSureListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_cash_refund, null);
        ImageView mDeleteImageView = rootView.findViewById(R.id.id_delete);// 关闭弹框按钮
        TextView mAmountReceivableTextView = rootView.findViewById(R.id.id_amount_receivable);// 应退金额：
        mAmountReceivableTextView.setText(String.format("应退金额：%s", refundableAmount));
        TextView mTotalTextView = rootView.findViewById(R.id.id_total);
        // 没用控件隐藏
        mTotalTextView.setVisibility(View.GONE);
        TextView mDiscountTextView = rootView.findViewById(R.id.id_discount);
        mDiscountTextView.setVisibility(View.GONE);
        TextView mAmountReceivableTipTextView = rootView.findViewById(R.id.id_amount_receivable_tip);//实退金额
        mAmountReceivableTipTextView.setText("实退金额：");
        TextView mChangeTipTextView = rootView.findViewById(R.id.id_change_tip);//差额
        mChangeTipTextView.setText("差额：");
        EditText mEditText = rootView.findViewById(R.id.id_money_received);// 实退金额编辑框
        TextView mChangeTextView = rootView.findViewById(R.id.id_change);// 差额
        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView);
        // 解决dialog宽度问题
        Window window = dialogBuilder.getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawable(null);
        dialogBuilder.show();
        // 键盘输入
        NumberKeyboardUtil.setClick(rootView, mEditText, 2, (inputCode, clickButton) -> {
            if (listener != null) {
                // 实退金额
                double refundAmount = NumberFormatUtils.toDouble(mEditText.getText().toString().trim(), 0);
                // 差额
                double balance = NumberFormatUtils.toDouble(mChangeTextView.getText().toString(), 0);
                // 实退金额>=应退金额
                if (refundAmount >= refundableAmount) {
                    listener.onClickSure(refundAmount, balance);
                    dialogBuilder.dismiss();
                } else {
                    ToastManager.showShortToast(activity.getApplicationContext(), "实退金额需大于等应退金额");
                }

            }
        }, dialogBuilder::dismiss);
        // 实退金额编辑框监听事件
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 实退金额
                double refundAmount = NumberFormatUtils.toDouble(s.toString(), 0);
                // 实退金额>=应退金额
                if (refundAmount >= refundableAmount) {
                    mChangeTextView.setText(String.format("%s", StringUtil.sub(s.toString(), refundableAmount + "")));
                } else {
                    mChangeTextView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 关闭弹框
        mDeleteImageView.setOnClickListener(v -> dialogBuilder.dismiss());
        rootView.setOnClickListener(v -> dialogBuilder.dismiss());
        return dialogBuilder.getDialog();
    }

    /**
     * 交班对账成功 ！
     */
    public static Dialog showShiftSuccessDialog(Activity activity, OnClickConfirmListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_shift_success, null);
        Button mSureButton = rootView.findViewById(R.id.id_sure);// 确定

        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView)
                .show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        // 确定
        mSureButton.setOnClickListener(v -> {
            if (listener != null)
                listener.onClickConfirm();
            dialogBuilder.dismiss();
        });
        return dialogBuilder.getDialog();
    }

    /**
     * 选择收银员对话框
     */
    public static Dialog showSelectCashierDialog(Activity activity, OnSelectCashierConfirmClickListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_select_cashier, null);
        Button mSureButton = rootView.findViewById(R.id.id_sure);// 确定
        RecyclerView mRecyclerView = rootView.findViewById(R.id.id_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        SelectCashierAdapter adapter = new SelectCashierAdapter(activity.getApplicationContext(), R.layout.adapter_select_cashier_list_item);
        mRecyclerView.setAdapter(adapter);
        // 获取用户列表
        DaoUtils.getUserList(activity, new DaoUtils.OnDaoResultListListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                adapter.clearAndAddList(list);
            }

            @Override
            public void onFailed() {
                adapter.clear();
            }
        });
        // item点击事件
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<User>() {
            @Override
            public void onClick(int position, User item) {
                // 如果原来没有选中点击时选中，反之不选中
                Boolean aBoolean = adapter.map.get(position);
                adapter.map.put(position, !aBoolean);
                adapter.notifyDataSetChanged();
            }
        });

        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView)
                .show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        // 确定
        mSureButton.setOnClickListener(v -> {
            if (listener != null) {
                // 用户id集合
                List<String> uids = new ArrayList<>();
                // 用户名多个用英文逗号隔开
                String names = null;
                // 遍历获取用户uid集合
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (adapter.map.get(i)) {
                        User user = adapter.getList().get(i);
                        uids.add(user.getUid());
                        names = TextUtils.isEmpty(names) ? user.getName() : names + "," + user.getName();
                    }


                }
                listener.onConfirmClick(uids, names);
            }

            dialogBuilder.dismiss();
        });
        return dialogBuilder.getDialog();
    }

    /**
     * 挂单弹框
     *
     * @param list     商品列表
     * @param listener 提取监听
     */
    public static void showEntryOrdersDialog(Activity activity, List<InputGoodsBean> list, OnExtractListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_entry_orders, null);
        RelativeLayout mParentRelativeLayout = rootView.findViewById(R.id.id_parent);
        Button mBackButton = rootView.findViewById(R.id.id_back);//返回
        Button mClearButton = rootView.findViewById(R.id.id_clear);//清空
        Button mDeleteButton = rootView.findViewById(R.id.id_delete_btn);//删除
        Button mExtractButton = rootView.findViewById(R.id.id_extract);//提取
        LinearLayout mLeftLinearLayout = rootView.findViewById(R.id.id_left_parent);
        mLeftLinearLayout.setBackgroundResource(R.drawable.shape_rectangle_radius_ebedf2);
        LinearLayout mRightLinearLayout = rootView.findViewById(R.id.id_right_parent);
        mRightLinearLayout.setBackgroundResource(R.drawable.shape_rectangle_radius_ebedf2);
        RecyclerView mLeftRecyclerView = rootView.findViewById(R.id.id_left_recycler_view);//挂单列表
        mLeftRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        EntryOrdersLeftAdapter mLeftAdapter = new EntryOrdersLeftAdapter(activity.getApplicationContext(), R.layout.adapter_entry_orders_left_list_item);
        mLeftRecyclerView.setAdapter(mLeftAdapter);
        RecyclerView mRightRecyclerView = rootView.findViewById(R.id.id_right_recycler_view);//挂单列表
        mRightRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        EntryOrdersRightAdapter mRightAdapter = new EntryOrdersRightAdapter(activity.getApplicationContext(), R.layout.adapter_entry_orders_right_list_item);
        mRightRecyclerView.setAdapter(mRightAdapter);

        // 获取挂单列表和第一个挂单对应的商品列表
        GuaDanUtils.insertAndGetList(activity, list, mLeftAdapter, mRightAdapter);

        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView);
        // 解决dialog宽度问题
        Window window = dialogBuilder.getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawable(null);
        dialogBuilder.show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);

        // 返回
        mBackButton.setOnClickListener(v -> dialogBuilder.dismiss());
        mParentRelativeLayout.setOnClickListener(v -> dialogBuilder.dismiss());
        // 清空
        mClearButton.setOnClickListener(v -> GuaDanUtils.clearGuaDan(activity, mLeftAdapter, mRightAdapter));
        // 删除
        mDeleteButton.setOnClickListener(v -> {
            // 有挂单才可以删除
            GuaDanUtils.delGuaDan(activity, mLeftAdapter, mRightAdapter);

        });
        // 提取
        mExtractButton.setOnClickListener(v -> {
            // 有挂单才可以提取
            if (mLeftAdapter.getItemCount() > 0 && mRightAdapter.getItemCount() > 0) {
                GuaDanUtils.ExtractGuaDan(mLeftAdapter);
                if (listener != null)
                    listener.onExtract(mRightAdapter.getList());
                dialogBuilder.dismiss();
            } else {
                ToastManager.showShortToast(activity.getApplicationContext(), "暂无挂单，无法提取");
            }


        });
        // 左侧item点击事件
        mLeftAdapter.setOnItemClickListener((position, item) -> {
            // 设置选中
            if (mLeftAdapter.selectPosition != position) {
                mLeftAdapter.selectPosition = position;
                mLeftAdapter.notifyDataSetChanged();
            }
            // 右侧列表重置
            GuaDanUtils.getRightGoodsList(activity, item.getId(), mRightAdapter);
        });
    }

    /**
     * 退款详情
     */
    public static Dialog showOrderRefundDesDialog(Activity activity, List<OrderRefundGoods> list, double totalPrice, int payType, OnClickConfirmListener listener) {
        ViewGroup rootView = (ViewGroup) View.inflate(activity.getApplicationContext(), R.layout.dialog_order_refund, null);
        RecyclerView mRecyclerView = rootView.findViewById(R.id.id_recycler_view);// 退款商品列表
        TextView mDataTextView = rootView.findViewById(R.id.id_data);// 相关数据
        Button mCancelButton = rootView.findViewById(R.id.id_cancel);// 取消
        Button mSureButton = rootView.findViewById(R.id.id_sure);// 确定
        LinearLayout mRightLinearLayout = rootView.findViewById(R.id.id_right_parent);
        mRightLinearLayout.setBackgroundResource(R.drawable.shape_rectangle_radius_ebedf2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        OrderRefundDesGoodsAdapter mAdapter = new OrderRefundDesGoodsAdapter(activity, R.layout.adapter_order_refund_des_list_item);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addList(list);
        int height = activity.getResources().getDimensionPixelOffset(R.dimen.item_height) * 4;
        mRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, list.size() >= 4 ? height : LinearLayout.LayoutParams.WRAP_CONTENT));
        // 支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
        String payStr = "";
        switch (payType) {
            case 1:// 支付宝
                payStr = "支付宝";
                break;
            case 2:// 微信
                payStr = "微信";
                break;
            case 6:// 现金
                payStr = "现金";
                break;
        }

        // 商品总数量
        double totalRefundNumber = 0;
        for (OrderRefundGoods orderRefundGoods : list) {
            totalRefundNumber += orderRefundGoods.getRefundNumber();
        }
        mDataTextView.setText(String.format("数量：%s            退款金额：%s            退款方式：%s", StringUtil.saveThreeDecimal(totalRefundNumber), StringUtil.saveTwoDecimal(totalPrice), payStr));

        DialogBuilder dialogBuilder = DialogBuilder
                .create(activity)
                .setView(rootView);
        // 解决dialog宽度问题
        Window window = dialogBuilder.getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawable(null);
        dialogBuilder.show();
        dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.shape_rectangle_transparent);
        // 取消
        mCancelButton.setOnClickListener(v -> dialogBuilder.dismiss());
        // 确定
        mSureButton.setOnClickListener(v -> {
            if (listener != null)
                listener.onClickConfirm();
        });

        return dialogBuilder.getDialog();

    }


    public interface OnClickConfirmListener {
        void onClickConfirm();
    }

    public interface OnSelectCashierConfirmClickListener {
        /**
         * @param uids  用户id集合
         * @param names 用户名多个用英文逗号隔开
         */
        void onConfirmClick(List<String> uids, String names);
    }

    public interface OnInputCodeListener {
        void onInputCodeFinish(String inputCode);
    }


    public interface OnClickSureListener {
        /**
         * @param position 1:单品退货 2:订单退货
         */
        void onClickSure(int position);
    }

    public interface OnClickCashRefundSureListener {
        /**
         * @param refundAmount 实退金额
         * @param balance      差额
         */
        void onClickSure(double refundAmount, double balance);
    }

    public interface OnExtractListener {
        /**
         * 提取
         *
         * @param inputGoodsBeanList 返回商品列表
         */
        void onExtract(List<InputGoodsBean> inputGoodsBeanList);
    }

    public interface OnInsertSuccessListener {
        /**
         * 成功
         */
        void onSuccess();
    }
}
