package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述: 商品表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class Goods {
    private long id;// 商品id
    @Id
    private String code;// 商品货号
    private String title;// 商品名
    private String simg;// 商品图片
    private double price;// 商品价格
    private long groupid1;// 一级分类
    private long groupid2;// 二级分类
    private long groupid3;// 三级分类
    private double min_qty;// 安全库存
    private double stock;// 当前库存
    private double num;// 累计销售量
    private int standard;// 是否是标品:1：标品；2:非标品
    private int state;// 控制是否可售:1：可采购，可销售；2:可采购，不可销售；3不可采购，可销售，4不可采购，不可销售
    private boolean isChecked;
    @Generated(hash = 2125670710)
    public Goods(long id, String code, String title, String simg, double price,
            long groupid1, long groupid2, long groupid3, double min_qty,
            double stock, double num, int standard, int state, boolean isChecked) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.simg = simg;
        this.price = price;
        this.groupid1 = groupid1;
        this.groupid2 = groupid2;
        this.groupid3 = groupid3;
        this.min_qty = min_qty;
        this.stock = stock;
        this.num = num;
        this.standard = standard;
        this.state = state;
        this.isChecked = isChecked;
    }
    @Generated(hash = 1770709345)
    public Goods() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSimg() {
        return this.simg;
    }
    public void setSimg(String simg) {
        this.simg = simg;
    }
    public double getPrice() {
        return this.price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public long getGroupid1() {
        return this.groupid1;
    }
    public void setGroupid1(long groupid1) {
        this.groupid1 = groupid1;
    }
    public long getGroupid2() {
        return this.groupid2;
    }
    public void setGroupid2(long groupid2) {
        this.groupid2 = groupid2;
    }
    public long getGroupid3() {
        return this.groupid3;
    }
    public void setGroupid3(long groupid3) {
        this.groupid3 = groupid3;
    }
    public double getMin_qty() {
        return this.min_qty;
    }
    public void setMin_qty(double min_qty) {
        this.min_qty = min_qty;
    }
    public double getStock() {
        return this.stock;
    }
    public void setStock(double stock) {
        this.stock = stock;
    }
    public double getNum() {
        return this.num;
    }
    public void setNum(double num) {
        this.num = num;
    }
    public int getStandard() {
        return this.standard;
    }
    public void setStandard(int standard) {
        this.standard = standard;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public boolean getIsChecked() {
        return this.isChecked;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
 
   


}
