package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 描述: 清单表，通过 order_id 查找当时购买的商品
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class OrderDetail {
    @Id(autoincrement = true)
    private Long id;
    private String order_id;// 订单id
    private String title;// 商品名
    private double prices;// 商品原价
    private double price;// 商品卖价
    private String code;// 商品货号
    private long goods_id;// 商品id
    private double num;// 商品购买数量，或者重量
    private double return_num;// 商品可退货总数
    private int standard;// 是否是标品:1：标品；2:非标品
    private double discount_pre_price;// 未优惠（优惠前）金额
    private int return_state;// 退款状态:0:可退；非0不可退

    @Generated(hash = 705925108)
    public OrderDetail(Long id, String order_id, String title, double prices, double price, String code,
            long goods_id, double num, double return_num, int standard, double discount_pre_price,
            int return_state) {
        this.id = id;
        this.order_id = order_id;
        this.title = title;
        this.prices = prices;
        this.price = price;
        this.code = code;
        this.goods_id = goods_id;
        this.num = num;
        this.return_num = return_num;
        this.standard = standard;
        this.discount_pre_price = discount_pre_price;
        this.return_state = return_state;
    }

    @Generated(hash = 268085433)
    public OrderDetail() {
    }

    public String getOrder_id() {
        return this.order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getGoods_id() {
        return this.goods_id;
    }

    public void setGoods_id(long goods_id) {
        this.goods_id = goods_id;
    }

    public double getNum() {
        return this.num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public double getReturn_num() {
        return this.return_num;
    }

    public void setReturn_num(double return_num) {
        this.return_num = return_num;
    }

    public int getStandard() {
        return this.standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public double getPrices() {
        return this.prices;
    }

    public void setPrices(double prices) {
        this.prices = prices;
    }

    public double getDiscount_pre_price() {
        return this.discount_pre_price;
    }

    public void setDiscount_pre_price(double discount_pre_price) {
        this.discount_pre_price = discount_pre_price;
    }

    public int getReturn_state() {
        return this.return_state;
    }

    public void setReturn_state(int return_state) {
        this.return_state = return_state;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
