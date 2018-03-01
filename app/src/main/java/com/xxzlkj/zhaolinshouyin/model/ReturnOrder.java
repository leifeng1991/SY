package com.xxzlkj.zhaolinshouyin.model;

/**
 * 描述: 订单表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
public class ReturnOrder {
    private String order_id;// 订单id
    private int return_type;// 退款类型：1：现金退款，2：原路返回
    private double price;// 订单实际支付金额，是抹零之后的金额
    private double muoling_price;// 抹零金额
    private String store_uid;// 操作人员id
    private String store_id;// 店铺id

    public ReturnOrder(String order_id, int return_type, double price, double muoling_price, String store_uid, String store_id) {
        this.order_id = order_id;
        this.return_type = return_type;
        this.price = price;
        this.muoling_price = muoling_price;
        this.store_uid = store_uid;
        this.store_id = store_id;
    }

    public double getMuoling_price() {
        return muoling_price;
    }

    public void setMuoling_price(double muoling_price) {
        this.muoling_price = muoling_price;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getReturn_type() {
        return return_type;
    }

    public void setReturn_type(int return_type) {
        this.return_type = return_type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStore_uid() {
        return store_uid;
    }

    public void setStore_uid(String store_uid) {
        this.store_uid = store_uid;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
}
