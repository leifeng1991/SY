package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

import java.util.List;

/**
 * 描述:线上订单
 *
 * @author leifeng
 *         2018/1/18 14:49
 */


public class OnLineOrderListBean extends BaseBean{


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1000001749
         * orderid : 20180118183509470963
         * state : 4
         * delivery : 1
         * store_id : 2222332
         * store_uid : 111339
         * is_tui : 1
         * address_name : 单位
         * address_address : 黄辛庄路西200米不是滋味
         * address_phone : 15104232026
         * addtime : 1516271709
         * endtime : 1516272619
         * sendtime : 1516271750
         * finishtime : 1516271767
         * uidtime : 1516271789
         * price : 0.03
         * payment : 1
         * prices : 0.03
         * is_print : 0
         * simg : http://zhaolin-10029121.image.myqcloud.com/sample1516184550844319
         * detail_id : 9506017
         * buytime : 1516271719
         * order_status : 1
         * refund_state : 2
         * refund_status : 2
         * refund_addtime : 1516271814
         * statetime : 1516271846
         * not_operating : 0
         * count :
         * nowtime : 1516690686
         */

        private String id;
        private String orderid;
        private String state;
        private String delivery;
        private String store_id;
        private String store_uid;
        private String is_tui;
        private String address_name;
        private String address_address;
        private String address_phone;
        private String addtime;
        private String endtime;
        private String sendtime;
        private String finishtime;
        private String uidtime;
        private String price;
        private String payment;
        private String prices;
        private String is_print;
        private String simg;
        private String detail_id;
        private String buytime;
        private String order_status;
        private String refund_state;
        private String refund_status;
        private String refund_addtime;
        private String statetime;
        private String not_operating;
        private String count;
        private String nowtime;
        private String coupon_price;
        private String activity_type;

        public String getActivity_type() {
            return activity_type;
        }

        public void setActivity_type(String activity_type) {
            this.activity_type = activity_type;
        }

        public String getCoupon_price() {
            return coupon_price;
        }

        public void setCoupon_price(String coupon_price) {
            this.coupon_price = coupon_price;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDelivery() {
            return delivery;
        }

        public void setDelivery(String delivery) {
            this.delivery = delivery;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getStore_uid() {
            return store_uid;
        }

        public void setStore_uid(String store_uid) {
            this.store_uid = store_uid;
        }

        public String getIs_tui() {
            return is_tui;
        }

        public void setIs_tui(String is_tui) {
            this.is_tui = is_tui;
        }

        public String getAddress_name() {
            return address_name;
        }

        public void setAddress_name(String address_name) {
            this.address_name = address_name;
        }

        public String getAddress_address() {
            return address_address;
        }

        public void setAddress_address(String address_address) {
            this.address_address = address_address;
        }

        public String getAddress_phone() {
            return address_phone;
        }

        public void setAddress_phone(String address_phone) {
            this.address_phone = address_phone;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getSendtime() {
            return sendtime;
        }

        public void setSendtime(String sendtime) {
            this.sendtime = sendtime;
        }

        public String getFinishtime() {
            return finishtime;
        }

        public void setFinishtime(String finishtime) {
            this.finishtime = finishtime;
        }

        public String getUidtime() {
            return uidtime;
        }

        public void setUidtime(String uidtime) {
            this.uidtime = uidtime;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getPrices() {
            return prices;
        }

        public void setPrices(String prices) {
            this.prices = prices;
        }

        public String getIs_print() {
            return is_print;
        }

        public void setIs_print(String is_print) {
            this.is_print = is_print;
        }

        public String getSimg() {
            return simg;
        }

        public void setSimg(String simg) {
            this.simg = simg;
        }

        public String getDetail_id() {
            return detail_id;
        }

        public void setDetail_id(String detail_id) {
            this.detail_id = detail_id;
        }

        public String getBuytime() {
            return buytime;
        }

        public void setBuytime(String buytime) {
            this.buytime = buytime;
        }

        public String getOrder_status() {
            return order_status;
        }

        public void setOrder_status(String order_status) {
            this.order_status = order_status;
        }

        public String getRefund_state() {
            return refund_state;
        }

        public void setRefund_state(String refund_state) {
            this.refund_state = refund_state;
        }

        public String getRefund_status() {
            return refund_status;
        }

        public void setRefund_status(String refund_status) {
            this.refund_status = refund_status;
        }

        public String getRefund_addtime() {
            return refund_addtime;
        }

        public void setRefund_addtime(String refund_addtime) {
            this.refund_addtime = refund_addtime;
        }

        public String getStatetime() {
            return statetime;
        }

        public void setStatetime(String statetime) {
            this.statetime = statetime;
        }

        public String getNot_operating() {
            return not_operating;
        }

        public void setNot_operating(String not_operating) {
            this.not_operating = not_operating;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getNowtime() {
            return nowtime;
        }

        public void setNowtime(String nowtime) {
            this.nowtime = nowtime;
        }
    }
}
