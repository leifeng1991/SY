package com.xxzlkj.zhaolinshouyin.model;

/**
 * 描述: 清单表，通过 order_id 查找当时购买的商品
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
public class ReturnOrderDetail {

    private double price;// 商品卖价
    private String code;// 商品货号
    private long goods_id;// 商品id
    private double num;// 退货数量

    public ReturnOrderDetail(double price, String code, long goods_id, double num) {
        this.price = price;
        this.code = code;
        this.goods_id = goods_id;
        this.num = num;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(long goods_id) {
        this.goods_id = goods_id;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }
}
