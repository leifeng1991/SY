package com.xxzlkj.zhaolinshouyin.model;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/9 15:28
 */


public class RongPushBean implements Serializable{

    /**
     * address_address : 中国人保寿险大厦北京市朝阳区朝阳门外北大街14号楼19层。 请极速送达
     * address_name : 周杰伦
     * address_phone : 13010001001
     * buytime : 0
     * content : messageTypeSystem
     * id : 1000000307
     * not_operating : 1390
     * num : 1.000
     * price : 5.00
     */

    private String address_address;
    private String address_name;
    private String address_phone;
    private String buytime;
    private String content;
    private String id;
    private String not_operating;
    private String num;
    private String price;

    public String getAddress_address() {
        return address_address;
    }

    public void setAddress_address(String address_address) {
        this.address_address = address_address;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getAddress_phone() {
        return address_phone;
    }

    public void setAddress_phone(String address_phone) {
        this.address_phone = address_phone;
    }

    public String getBuytime() {
        return buytime;
    }

    public void setBuytime(String buytime) {
        this.buytime = buytime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNot_operating() {
        return not_operating;
    }

    public void setNot_operating(String not_operating) {
        this.not_operating = not_operating;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
