package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述: 三方商品表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class ThreeGoods {
    private long id;// 商品id
    @Id
    private String code;// 商品货号
    private String title;// 商品名
    private int standard;// 是否是标品:1：标品；2:非标品
    private int state;// 控制是否可售:1：下架；2:上架；
    @Generated(hash = 1219609167)
    public ThreeGoods(long id, String code, String title, int standard, int state) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.standard = standard;
        this.state = state;
    }
    @Generated(hash = 1348197449)
    public ThreeGoods() {
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

}
