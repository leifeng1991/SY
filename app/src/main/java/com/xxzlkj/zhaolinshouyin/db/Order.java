package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述: 订单表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class Order {
    @Id
    private String order_id;// 订单id
    private int state;// 流水类型： 1：消费 2：退款
    private int payment;// 支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
    private int order_type;// 订单类型： 1：线上 2：线下
    private double prices;// 原价，是未抹零、未优惠的价格
    private double price;// 订单实际支付金额，是抹零之后的金额
    private double pay_price;// 付款金额
    private double zhaoling_price;// 找零金额
    private double muoling_price;// 抹零金额
    private long addtime;// 订单增加时间
    private long syn_time;// 同步时间
    private String store_uid;// 操作人员id
    private String store_id;// 店铺id
    private int upload_state = 0;// 上传状态，0：未上传，1：已上传，2：无库存
    private String auth_code;// 支付宝、微信支付的码
    private double discount_pre_price;// 未优惠（优惠前）金额
    private String coupon;// 优惠券
    private String members_code;// 会员码
    private String source_order_id;// 原始订单id，退款订单关联原始订单表

    @Generated(hash = 1105174599)
    public Order() {
    }
    @Generated(hash = 333174912)
    public Order(String order_id, int state, int payment, int order_type,
            double prices, double price, double pay_price, double zhaoling_price,
            double muoling_price, long addtime, long syn_time, String store_uid,
            String store_id, int upload_state, String auth_code,
            double discount_pre_price, String coupon, String members_code,
            String source_order_id) {
        this.order_id = order_id;
        this.state = state;
        this.payment = payment;
        this.order_type = order_type;
        this.prices = prices;
        this.price = price;
        this.pay_price = pay_price;
        this.zhaoling_price = zhaoling_price;
        this.muoling_price = muoling_price;
        this.addtime = addtime;
        this.syn_time = syn_time;
        this.store_uid = store_uid;
        this.store_id = store_id;
        this.upload_state = upload_state;
        this.auth_code = auth_code;
        this.discount_pre_price = discount_pre_price;
        this.coupon = coupon;
        this.members_code = members_code;
        this.source_order_id = source_order_id;
    }
    public String getOrder_id() {
        return this.order_id;
    }
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public int getPayment() {
        return this.payment;
    }
    public void setPayment(int payment) {
        this.payment = payment;
    }
    public int getOrder_type() {
        return this.order_type;
    }
    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }
    public double getPrices() {
        return this.prices;
    }
    public void setPrices(double prices) {
        this.prices = prices;
    }
    public double getPrice() {
        return this.price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getPay_price() {
        return this.pay_price;
    }
    public void setPay_price(double pay_price) {
        this.pay_price = pay_price;
    }
    public double getZhaoling_price() {
        return this.zhaoling_price;
    }
    public void setZhaoling_price(double zhaoling_price) {
        this.zhaoling_price = zhaoling_price;
    }
    public double getMuoling_price() {
        return this.muoling_price;
    }
    public void setMuoling_price(double muoling_price) {
        this.muoling_price = muoling_price;
    }
    public long getAddtime() {
        return this.addtime;
    }
    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }
    public long getSyn_time() {
        return this.syn_time;
    }
    public void setSyn_time(long syn_time) {
        this.syn_time = syn_time;
    }
    public String getStore_uid() {
        return this.store_uid;
    }
    public void setStore_uid(String store_uid) {
        this.store_uid = store_uid;
    }
    public String getStore_id() {
        return this.store_id;
    }
    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
    public int getUpload_state() {
        return this.upload_state;
    }
    public void setUpload_state(int upload_state) {
        this.upload_state = upload_state;
    }
    public String getAuth_code() {
        return this.auth_code;
    }
    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }
    public String getCoupon() {
        return this.coupon;
    }
    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
    public String getMembers_code() {
        return this.members_code;
    }
    public void setMembers_code(String members_code) {
        this.members_code = members_code;
    }
    public double getDiscount_pre_price() {
        return this.discount_pre_price;
    }
    public void setDiscount_pre_price(double discount_pre_price) {
        this.discount_pre_price = discount_pre_price;
    }
    public String getSource_order_id() {
        return this.source_order_id;
    }
    public void setSource_order_id(String source_order_id) {
        this.source_order_id = source_order_id;
    }

  

}
