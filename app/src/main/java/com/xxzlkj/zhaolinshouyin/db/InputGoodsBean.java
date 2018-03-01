package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/5 11:20
 */
@Entity
public class InputGoodsBean {
    @Id
    private String uniqueId;
    private String guadan_id;// 挂单id

    private int standard;//    标品:1 非标:2
    private long id;// 商品id
    private String code;// 商品货号
    private String title;// 商品名
    private double price;// 商品单价
    private double prices;// 商品原价
    private double allPrices;// 商品总价（price * num）
    private double num;// 商品数量，或者重量
    private boolean isWeighing;// 是否是称重中

    @Generated(hash = 2051738025)
    public InputGoodsBean(String uniqueId, String guadan_id, int standard, long id, String code,
            String title, double price, double prices, double allPrices, double num,
            boolean isWeighing) {
        this.uniqueId = uniqueId;
        this.guadan_id = guadan_id;
        this.standard = standard;
        this.id = id;
        this.code = code;
        this.title = title;
        this.price = price;
        this.prices = prices;
        this.allPrices = allPrices;
        this.num = num;
        this.isWeighing = isWeighing;
    }

    @Generated(hash = 1082142620)
    public InputGoodsBean() {
    }

    public InputGoodsBean(boolean isWeighing) {
        this.isWeighing = isWeighing;
    }

    public String getGuadan_id() {
        return guadan_id;
    }

    public void setGuadan_id(String guadan_id) {
        this.guadan_id = guadan_id;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrices() {
        return prices;
    }

    public void setPrices(double prices) {
        this.prices = prices;
    }

    public double getAllPrices() {
        return allPrices;
    }

    public void setAllPrices(double allPrices) {
        this.allPrices = allPrices;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public boolean isWeighing() {
        return isWeighing;
    }

    public void setWeighing(boolean weighing) {
        isWeighing = weighing;
    }

    public boolean getIsWeighing() {
        return this.isWeighing;
    }

    public void setIsWeighing(boolean isWeighing) {
        this.isWeighing = isWeighing;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
