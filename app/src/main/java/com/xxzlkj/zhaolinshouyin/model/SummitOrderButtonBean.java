package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/3/25 11:22
 */
public class SummitOrderButtonBean extends BaseBean {

    /**
     * data : {"address_address":"北京市东城区人保寿险大厦18层到后致电","address_name":"之后","address_phone":"13010001001","addtime":"1489996017","finishtime":"1490411890","id":"1000000297","is_tui":"1","orderid":"20170320154657126110","sendtime":"1490342605","state":"4","store_id":"2222315","store_uid":"111346","uidtime":"0"}
     */

    private OnLineOrderListBean.DataBean data=new OnLineOrderListBean.DataBean();

    public OnLineOrderListBean.DataBean getData() {
        return data;
    }

    public void setData(OnLineOrderListBean.DataBean data) {
        this.data = data;
    }
}
