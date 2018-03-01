package com.xxzlkj.zhaolinshouyin.model;

public class ThreeGoodsOrGoodsInfoBean {
    private boolean isThreeGoods;// 是否是三方商品
    private long id;// 商品id
    private String code;// 商品货号
    private String title;// 商品名
    private int standard;// 是否是标品:1：标品；2:非标品
    private int state;// 控制是否可售:三方商品（1：上架；2:下架；），自营商品（1：可采购，可销售；2:可采购，不可销售；3不可采购，可销售，4不可采购，不可销售）

    public ThreeGoodsOrGoodsInfoBean(boolean isThreeGoods, long id, String code, String title, int standard, int state) {
        this.isThreeGoods = isThreeGoods;
        this.id = id;
        this.code = code;
        this.title = title;
        this.standard = standard;
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isThreeGoods() {
        return isThreeGoods;
    }

    public void setThreeGoods(boolean threeGoods) {
        isThreeGoods = threeGoods;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
