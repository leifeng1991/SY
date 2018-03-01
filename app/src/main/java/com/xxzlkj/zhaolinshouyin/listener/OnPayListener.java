package com.xxzlkj.zhaolinshouyin.listener;

import android.app.Activity;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/13 11:58
 */
public interface OnPayListener {
    /**
     * @param payState      （必传）支付状态   1：支付 2：退款
     * @param payType       （必传）支付类型: 1：支付宝 2：微信 6：现金
     * @param paidInMoney   （必传）实收金额
     * @param zhaolingMoney （选传）找零金额，现金支付必传
     * @param muolingMoney  （选传）抹零金额，现金支付必传
     * @param auth_code     （选传）微信、支付宝支付时的码,，支付宝、微信支付必传
     */
    void onPayFinish(Activity activity, int payState, int payType, double paidInMoney, double zhaolingMoney, double muolingMoney, String auth_code);

    /**
     * 检查优惠
     *
     * @param isNetAvailable 网络是否可用
     * @param coupon         优惠券
     * @param payment        支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
     */
    void onCheckDiscount(boolean isNetAvailable, String coupon, int payment);
}
