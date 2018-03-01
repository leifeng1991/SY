package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

import java.util.ArrayList;
import java.util.List;

public class OrderDesBean extends BaseBean {


    /**
     * data : {"id":"1000000311","uid":"111341","price":"5.00","addtime":"1490077233","sendtime":"0","finishtime":"0","uidtime":"0","store_id":"2222316","store_uid":"111346","state":"3","is_tui":"2","content":"","type":"1","orderid":"20170321142033173826","ems":"0.00","delivery":"2","payment":"0","address_name":"周杰伦","address_address":"中国人保寿险大厦北京市朝阳区朝阳门外北大街14号楼19层。 请极速送达","address_phone":"13010001001","endtime":"1490078133","nowtime":"1490263733","discount":"0.00","detail":[{"id":"9504328","price":"5.00","prices":"7.00","addtime":"0","state":"1","simg":"http://zhaolin-10029121.image.myqcloud.com/sample1489134729899461","title":"雪花啤酒纯生 500ml","num":"1","goods_id":"9503848"}]}
     */

    private DataBean data = new DataBean();

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1000000311
         * uid : 111341
         * price : 5.00
         * addtime : 1490077233
         * sendtime : 0
         * finishtime : 0
         * uidtime : 0
         * store_id : 2222316
         * store_uid : 111346
         * state : 3
         * is_tui : 2
         * content :
         * type : 1
         * orderid : 20170321142033173826
         * ems : 0.00
         * delivery : 2
         * payment : 0
         * address_name : 周杰伦
         * address_address : 中国人保寿险大厦北京市朝阳区朝阳门外北大街14号楼19层。 请极速送达
         * address_phone : 13010001001
         * endtime : 1490078133
         * nowtime : 1490263733
         * discount : 0.00
         * detail : [{"id":"9504328","price":"5.00","prices":"7.00","addtime":"0","state":"1","simg":"http://zhaolin-10029121.image.myqcloud.com/sample1489134729899461","title":"雪花啤酒纯生 500ml","num":"1","goods_id":"9503848"}]
         */

        private String id;
        private String uid;
        private String price;
        private String addtime;
        private String sendtime;
        private String finishtime;
        private String uidtime;
        private String store_id;
        private String store_uid;
        private String state;
        private String is_tui;
        private String content;
        private String type;
        private String orderid;
        private String ems;
        private String delivery;
        private String payment;
        private String address_name;
        private String address_address;
        private String address_phone;
        private String endtime;
        private String nowtime;
        private String discount;
        private String prices;
        private String send_phone;
        private String send_username;
        private String store_address;
        private String coupon_price;
        private String not_operating;
        private String is_groupon_team;// 1:已成团 0：未成团
        private String activity_type;// 0:普通订单 1：预售订单 2：团购订单
        private List<DetailBean> detail = new ArrayList<>();
        private List<CouponBean> coupon = new ArrayList<>();

        /**
         * 付款时间
         */
        private String buytime;
        /**
         * 1退款中 2已退款
         */
        private String refund_status;
        /**
         * 退款状态 0未申请退款 1申请中 2通过 3否定  5用户撤销申请
         */
        private String refund_state;
        /**
         * 用户申请售后时间
         */
        private String refund_addtime;
        /**
         * 服务器审核结束时间
         */
        private String statetime;

        public String getIs_groupon_team() {
            return is_groupon_team;
        }

        public void setIs_groupon_team(String is_groupon_team) {
            this.is_groupon_team = is_groupon_team;
        }

        public String getNot_operating() {
            return not_operating;
        }

        public void setNot_operating(String not_operating) {
            this.not_operating = not_operating;
        }

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

        public List<CouponBean> getCoupon() {
            return coupon;
        }

        public void setCoupon(List<CouponBean> coupon) {
            this.coupon = coupon;
        }

        public String getPrices() {
            return prices;
        }

        public void setPrices(String prices) {
            this.prices = prices;
        }

        public String getStore_address() {
            return store_address;
        }

        public void setStore_address(String store_address) {
            this.store_address = store_address;
        }

        public String getSend_phone() {
            return send_phone;
        }

        public void setSend_phone(String send_phone) {
            this.send_phone = send_phone;
        }

        public String getSend_username() {
            return send_username;
        }

        public void setSend_username(String send_username) {
            this.send_username = send_username;
        }

        public String getBuytime() {
            return buytime;
        }

        public void setBuytime(String buytime) {
            this.buytime = buytime;
        }

        public String getRefund_status() {
            return refund_status;
        }

        public void setRefund_status(String refund_status) {
            this.refund_status = refund_status;
        }

        public String getRefund_state() {
            return refund_state;
        }

        public void setRefund_state(String refund_state) {
            this.refund_state = refund_state;
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


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
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

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getIs_tui() {
            return is_tui;
        }

        public void setIs_tui(String is_tui) {
            this.is_tui = is_tui;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getEms() {
            return ems;
        }

        public void setEms(String ems) {
            this.ems = ems;
        }

        public String getDelivery() {
            return delivery;
        }

        public void setDelivery(String delivery) {
            this.delivery = delivery;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
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

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getNowtime() {
            return nowtime;
        }

        public void setNowtime(String nowtime) {
            this.nowtime = nowtime;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public List<DetailBean> getDetail() {
            return detail;
        }

        public void setDetail(List<DetailBean> detail) {
            this.detail = detail;
        }

        public static class DetailBean {
            /**
             * id : 9504328
             * price : 5.00
             * prices : 7.00
             * addtime : 0
             * state : 1
             * simg : http://zhaolin-10029121.image.myqcloud.com/sample1489134729899461
             * title : 雪花啤酒纯生 500ml
             * num : 1
             * goods_id : 9503848
             */

            private String id;
            private String price;
            private String prices;
            private String addtime;
            private String state;
            private String simg;
            private String title;
            private String ads;
            private String num;
            private String goods_id;

            public String getAds() {
                return ads;
            }

            public void setAds(String ads) {
                this.ads = ads;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getPrices() {
                return prices;
            }

            public void setPrices(String prices) {
                this.prices = prices;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getSimg() {
                return simg;
            }

            public void setSimg(String simg) {
                this.simg = simg;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getNum() {
                return num;
            }

            public void setNum(String num) {
                this.num = num;
            }

            public String getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(String goods_id) {
                this.goods_id = goods_id;
            }
        }

        public static class CouponBean {
            /**
             * id : 15
             * uid : 111355
             * type : 1
             * class : 1
             * marketid : 0
             * filled :
             * discount : 9
             * starttime : 1498725393
             * endtime : 1498811798
             * addtime : 1497335933
             * title : 仅限商城商品使用
             */

            private String id;
            private String uid;
            private String type;
            private String sort;
            private String marketid;
            private String filled;
            private String discount = "";
            private String starttime;
            private String endtime;
            private String addtime;
            private String title;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getSort() {
                return sort;
            }

            public void setSort(String sort) {
                this.sort = sort;
            }

            public String getMarketid() {
                return marketid;
            }

            public void setMarketid(String marketid) {
                this.marketid = marketid;
            }

            public String getFilled() {
                return filled;
            }

            public void setFilled(String filled) {
                this.filled = filled;
            }

            public String getDiscount() {
                return discount;
            }

            public void setDiscount(String discount) {
                this.discount = discount;
            }

            public String getStarttime() {
                return starttime;
            }

            public void setStarttime(String starttime) {
                this.starttime = starttime;
            }

            public String getEndtime() {
                return endtime;
            }

            public void setEndtime(String endtime) {
                this.endtime = endtime;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
