package com.xxzlkj.zhaolinshouyin.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseFragment;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.event.CheckDiscountEvent;
import com.xxzlkj.zhaolinshouyin.listener.OnPayListener;
import com.xxzlkj.zhaolinshouyin.model.CheckDiscountBean;
import com.xxzlkj.zhaolinshouyin.utils.CashBoxUtils;
import com.xxzlkj.zhaolinshouyin.utils.NumberKeyboardUtil;
import com.zrq.spanbuilder.Spans;
import com.zrq.spanbuilder.TextStyle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;

/**
 * 描述:现金
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class CashPayFragment extends BaseFragment {
    public static final String PAY_MONEY = "pay_money";
    public static final String PAY_STATE = "payState";
    private OnPayListener onPayListener;
    private EditText mEditText;
    private TextView mAmountReceivableTextView, mTotalTextView, mChangeTipTextView, mAmountReceivableTipTextView, mDiscountTextView, mChangeTextView;
    private Button mReceivableButton;
    private double payMoney;
    private double mPayMoney;
    private int payState;
    private double discount_price;

    /**
     * @param payState      必传，支付状态   1：支付 2：退款
     * @param payMoney      应收金额（必传）
     * @param onPayListener 点击确认回调
     */
    public static CashPayFragment newInstance(int payState, double payMoney, OnPayListener onPayListener) {
        CashPayFragment inputCodeFragment = new CashPayFragment();
        inputCodeFragment.onPayListener = onPayListener;
        Bundle bundle = new Bundle();
        bundle.putInt(PAY_STATE, payState);
        bundle.putDouble(PAY_MONEY, payMoney);
        inputCodeFragment.setArguments(bundle);
        return inputCodeFragment;
    }

    @Override
    public View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_cash_apy, container, false);
    }

    @Override
    protected void findViewById() {
        mAmountReceivableTextView = getView(R.id.id_amount_receivable);// 应收金额
        mTotalTextView = getView(R.id.id_total);// 合计
        mDiscountTextView = getView(R.id.id_discount);// 优惠
        mAmountReceivableTipTextView = getView(R.id.id_amount_receivable_tip);// 实收金额提示
        mEditText = getView(R.id.id_money_received);// 实收金额
        mChangeTipTextView = getView(R.id.id_change_tip);// 找零提示
        mChangeTextView = getView(R.id.id_change);// 找零
        mReceivableButton = getView(R.id.btn_num_clear);// 应收款

    }

    @Override
    public void setListener() {
        EventBus.getDefault().register(this);

        NumberKeyboardUtil.setClick(rootView, mEditText, 2, (inputCode, clickButton) -> {
            double change = NumberFormatUtils.toDouble(mChangeTextView.getText().toString().trim());
            String trim = mEditText.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                ToastManager.showShortToast(mContext, "请输入收款金额！");
                return;
            }
            // 实收金额
            double mMoneyReceived = NumberFormatUtils.toDouble(trim);
            if (mMoneyReceived < mPayMoney) {
                ToastManager.showShortToast(mContext, "实收金额小于应收金额，请重新输入");
                return;
            }
            // 找零不能大于等于100
            if (change >= 100) {
                ToastManager.showShortToast(mContext, "找零金额须小于100！");
                return;
            }
            // 抹零金额
            double molingMoney = payMoney - mPayMoney - discount_price;// 原始金额-应收金额-优惠金额=抹零金额
            // 支付
            submitPay(mMoneyReceived, NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(change)), NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(molingMoney)));
        }, null);
        // 输入框监听事件
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 实收金额
                double mMoneyReceived = NumberFormatUtils.toDouble(mEditText.getText().toString().trim());
                // 实收金额大于等于应收金额计算找零
                if (mMoneyReceived >= mPayMoney) {
                    // 实际找零金额
                    double realityChangeMoney = mMoneyReceived - mPayMoney;

                    mChangeTextView.setText(String.format("%s", StringUtil.saveTwoDecimal(realityChangeMoney)));
                } else {
                    mChangeTextView.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 应收金额
        mReceivableButton.setOnClickListener(v -> {
            mEditText.setText("");
            int index = mEditText.getSelectionStart();
            Editable editable = mEditText.getText();
            editable.insert(index, String.format("%s", mPayMoney));
        });
    }

    @Override
    public void processLogic() {
        // 获取值
        Bundle arguments = getArguments();
        payMoney = Math.abs(arguments.getDouble(PAY_MONEY, 0));// 支付金额：为正数，通过 payState 来判断是支付还是退款
        payState = arguments.getInt(PAY_STATE, C.I.PAY_STATE_PAY);// 支付状态   1：支付 2：退款
        // 抹零后的实付金额值
        BigDecimal payMoneyBigDecimal = new BigDecimal(StringUtil.saveTwoDecimal(payMoney)).setScale(1, BigDecimal.ROUND_DOWN);
        mPayMoney = Double.valueOf(payMoneyBigDecimal.toString());

        // 设置值
        if (payState == C.I.PAY_STATE_PAY) {
            // 支付
            mAmountReceivableTextView.setText(Spans.builder().text("应收金额：").text(StringUtil.saveTwoDecimal(payMoney) + "").size(26).style(TextStyle.BOLD).build());
            mTotalTextView.setText(String.format("合计：%s", StringUtil.saveTwoDecimal(payMoney)));
            mDiscountTextView.setText(String.format("优惠：%s", "0.00"));
            mAmountReceivableTipTextView.setText("实收金额：");
            mChangeTipTextView.setText("找零：");
            mReceivableButton.setText("应收款");
        } else {
            // 退款
            mAmountReceivableTextView.setText(Spans.builder().text("应退金额：").text(StringUtil.saveTwoDecimal(payMoney) + "").size(26).style(TextStyle.BOLD).build());
            mTotalTextView.setVisibility(View.GONE);
            mDiscountTextView.setVisibility(View.GONE);
            mAmountReceivableTipTextView.setText("实退金额：");
            mChangeTipTextView.setText("差额：");
            mReceivableButton.setText("应退款");
        }

    }

    /**
     * 提交支付
     *
     * @param paidInMoney   实付金额
     * @param zhaolingMoney 找零金额
     * @param molingMoney   抹零金额
     */
    private void submitPay(double paidInMoney, double zhaolingMoney, double molingMoney) {
        if (onPayListener != null) {
            LogUtil.e("实收金额：" + paidInMoney + "找零金额：" + zhaolingMoney + "抹零金额：" + molingMoney);
//             payState      支付状态：1：支付 2：退款
//             payType       支付类型: 1：支付宝 2：微信 6：现金
//             paidInMoney   实收金额
//             zhaolingMoney 找零金额
//             muolingMoney  抹零金额
            // 打开钱箱
            CashBoxUtils.openDrawer();

            onPayListener.onPayFinish(mActivity, payState, C.I.PAY_TYPE_CASH, paidInMoney, zhaolingMoney, molingMoney, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收到优惠金额
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveDiscount(CheckDiscountEvent event) {
        if (event != null && event.getCheckDiscountBean() != null && event.getCheckDiscountBean().getData() != null) {
            CheckDiscountBean.DataBean data = event.getCheckDiscountBean().getData();
            // 保存值
            mPayMoney = data.getPrice();
            discount_price = data.getDiscount_price();
            // 设置值
            mAmountReceivableTextView.setText(Spans.builder().text("应收金额：").text(StringUtil.saveTwoDecimal(mPayMoney + "")).size(26).style(TextStyle.BOLD).build());
            mDiscountTextView.setText(String.valueOf("优惠：" + StringUtil.saveTwoDecimal(discount_price)));
//            mEditText.setText(String.valueOf(mPayMoney));
        }
    }
}
