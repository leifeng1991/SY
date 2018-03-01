package com.xxzlkj.zhaolinshouyin.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.HintRefreshFragment;
import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.event.CheckDiscountEvent;
import com.xxzlkj.zhaolinshouyin.listener.OnPayListener;
import com.xxzlkj.zhaolinshouyin.model.CheckDiscountBean;
import com.xxzlkj.zhaolinshouyin.utils.NumberKeyboardUtil;
import com.zrq.spanbuilder.Spans;
import com.zrq.spanbuilder.TextStyle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 描述:三方
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class TriplicitiesPayFragment extends HintRefreshFragment {
    public static final String PAY_MONEY = "pay_money";
    public static final String PAY_TYPE = "payType";
    private TextView mAmountReceivableTextView, mTotalTextView, mDiscountTextView;
    private EditText mEditText;
    private RelativeLayout mNoNetLinearLayout, mHasNetLinearLayout;
    private double payMoney;
    private OnPayListener onPayListener;
    private int payType;

    /**
     * @param payMoney      应收金额（必传）
     * @param payType       支付类型: 1：支付宝 2：微信 6：现金
     * @param onPayListener 支付完成回调
     */
    public static TriplicitiesPayFragment newInstance(double payMoney, int payType, OnPayListener onPayListener) {
        TriplicitiesPayFragment inputCodeFragment = new TriplicitiesPayFragment();
        inputCodeFragment.onPayListener = onPayListener;
        Bundle bundle = new Bundle();
        bundle.putDouble(PAY_MONEY, payMoney);
        bundle.putInt(PAY_TYPE, payType);
        inputCodeFragment.setArguments(bundle);
        return inputCodeFragment;
    }

    @Override
    public View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_triplicities_apy, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void findViewById() {
        mAmountReceivableTextView = getView(R.id.id_amount_receivable);// 应收金额
        mTotalTextView = getView(R.id.id_total);// 合计
        mDiscountTextView = getView(R.id.id_discount);// 优惠
        mEditText = getView(R.id.id_edit_text);
        mHasNetLinearLayout = getView(R.id.id_has_net_layout);// 有网络
        mNoNetLinearLayout = getView(R.id.id_no_net_layout);// 无网络

        TextView mNoNetTextView = getView(R.id.id_no_net);
        mNoNetTextView.setText("网络连接失败，无法使用在线支付！\n" +
                "            请检查网络状态");
    }

    @Override
    public void setListener() {
        EventBus.getDefault().register(this);
        // 按钮确认，点击完提交支付
        NumberKeyboardUtil.setClick(rootView, mEditText, 0, (inputCode, clickButton) -> {
            // 提交支付
            submitPay(inputCode);
        }, null);
    }


    @Override
    public void processLogic() {
        // 获取传值
        Bundle arguments = getArguments();
        payMoney = arguments.getDouble(PAY_MONEY, 0);
        payType = arguments.getInt(PAY_TYPE, 1);
        // 设置信息
        mAmountReceivableTextView.setText(Spans.builder().text("应收金额：").text(StringUtil.saveTwoDecimal(payMoney) + "").size(26).style(TextStyle.BOLD).build());
        mTotalTextView.setText(String.format("合计：%s", StringUtil.saveTwoDecimal(payMoney)));
        mDiscountTextView.setText(String.valueOf("优惠：0.00"));

    }

    @Override
    public void hintRefreshData() {
        if (NetStateManager.isAvailable(mContext)) {
            mHasNetLinearLayout.setVisibility(View.VISIBLE);
            mNoNetLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            mNoNetLinearLayout.setVisibility(View.VISIBLE);
            mHasNetLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 提交支付
     *
     * @param auth_code 收款码
     */
    private void submitPay(String auth_code) {
        if (TextUtils.isEmpty(auth_code)) {
            // 收款码为空提示
            ToastManager.showShortToast(mContext, "请录入付款付款码");
            return;
        }
        if (onPayListener != null) {

//             payState      支付状态：1：支付 2：退款
//             payType       支付类型: 1：支付宝 2：微信 6：现金
//             paidInMoney   实收金额
//             zhaolingMoney 找零金额
//             muolingMoney  抹零金额
            onPayListener.onPayFinish(mActivity, C.I.PAY_STATE_PAY, payType, payMoney, 0, 0, auth_code);
        }
    }

    /**
     * 设置扫描到的收款码，并支付，支付前要检查网络
     *
     * @param auth_code 收款码
     */
    public void setScanCodeAndRequestPay(String auth_code) {
        if (mHasNetLinearLayout.getVisibility() == View.VISIBLE) {
            // 支付页面显示有网的，可以输入码，进行支付
            if (TextUtils.isEmpty(auth_code)) {
                // 内容为空，不设置内容，内容为EditText获取
                auth_code = mEditText.getText().toString().trim();
            } else {
                // 内容不为空，设置内容，提交内容为auth_code
                // 设置内容
                mEditText.setText(auth_code);
            }
            // 提交支付
            submitPay(auth_code);
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
            payMoney = data.getPrice();
            // 设置值
            mAmountReceivableTextView.setText(Spans.builder().text("应收金额：").text(StringUtil.saveTwoDecimal(data.getPrice()) + "").size(26).style(TextStyle.BOLD).build());
            mDiscountTextView.setText(String.valueOf("优惠：" + StringUtil.saveTwoDecimal(data.getDiscount_price())));
        }
    }
}
