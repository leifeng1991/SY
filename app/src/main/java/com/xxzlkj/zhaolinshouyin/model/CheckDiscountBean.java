package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

/**
 * 描述: 检测优惠
 *
 * @author zhangrq
 *         2017/12/15 11:02
 */
public class CheckDiscountBean extends BaseBean {
    private DataBean data = new DataBean();

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private double price;// 订单实际支付金额，是抹零之后的金额
        private double discount_price;// 优惠金额
        private String coupon;// 优惠券
        private String members_code;// 会员码

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(double discount_price) {
            this.discount_price = discount_price;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        public String getMembers_code() {
            return members_code;
        }

        public void setMembers_code(String members_code) {
            this.members_code = members_code;
        }
    }
}
