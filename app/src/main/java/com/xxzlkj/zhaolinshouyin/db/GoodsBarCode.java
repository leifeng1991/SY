package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述:商品码表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class GoodsBarCode {
    @Id
    private String code;// 条形码
    private String goods_code;// 商品id或货号id
    @Generated(hash = 324118845)
    public GoodsBarCode(String code, String goods_code) {
        this.code = code;
        this.goods_code = goods_code;
    }
    @Generated(hash = 74326027)
    public GoodsBarCode() {
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getGoods_code() {
        return this.goods_code;
    }
    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }
   

}
