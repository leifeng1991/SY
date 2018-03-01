package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/9 14:51
 */

@Entity
public class Params {
    @Id
    private long id;// id
    private String syn_time_goods;// 同步时间-商品
    private String syn_time_goods_class;// 同步时间-商品分类
    private String syn_time_goods_code;// 同步时间-商品条形码
    private String store_id;// 门店id
    private String store_title;// 门店名
    private String devices_num;// 设备编号
    private String rong_token;// 融云token


    @Generated(hash = 413797063)
    public Params(long id, String syn_time_goods, String syn_time_goods_class,
            String syn_time_goods_code, String store_id, String store_title,
            String devices_num, String rong_token) {
        this.id = id;
        this.syn_time_goods = syn_time_goods;
        this.syn_time_goods_class = syn_time_goods_class;
        this.syn_time_goods_code = syn_time_goods_code;
        this.store_id = store_id;
        this.store_title = store_title;
        this.devices_num = devices_num;
        this.rong_token = rong_token;
    }

    @Generated(hash = 1432417716)
    public Params() {
    }


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSyn_time_goods() {
        return this.syn_time_goods;
    }

    public void setSyn_time_goods(String syn_time_goods) {
        this.syn_time_goods = syn_time_goods;
    }

    public String getSyn_time_goods_class() {
        return this.syn_time_goods_class;
    }

    public void setSyn_time_goods_class(String syn_time_goods_class) {
        this.syn_time_goods_class = syn_time_goods_class;
    }

    public String getSyn_time_goods_code() {
        return this.syn_time_goods_code;
    }

    public void setSyn_time_goods_code(String syn_time_goods_code) {
        this.syn_time_goods_code = syn_time_goods_code;
    }

    public String getStore_id() {
        return this.store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_title() {
        return this.store_title;
    }

    public void setStore_title(String store_title) {
        this.store_title = store_title;
    }

    public String getDevices_num() {
        return this.devices_num;
    }

    public void setDevices_num(String devices_num) {
        this.devices_num = devices_num;
    }

    public String getRong_token() {
        return this.rong_token;
    }

    public void setRong_token(String rong_token) {
        this.rong_token = rong_token;
    }


}
