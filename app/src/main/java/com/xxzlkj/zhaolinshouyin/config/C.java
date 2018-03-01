package com.xxzlkj.zhaolinshouyin.config;


/**
 * 常量
 *
 * @author zhangrq
 */
public interface C {
    interface S {
        String TYPE = "type";
        String UID = "uid";
        String PHONE = "phone";
        String PASSWROD = "passwrod";
        String TITLE = "title";
        String GROUPID = "groupId";
        String LOGIN_USER_NAME = "login_user_name";
        String POI_ITEM = "poi_item";
        String PID = "pid";
        String BID = "bid";
        String IS_REFUND = "isRefund";
        String SELECT_BEAN = "selectBean";
        String MAP_LOCATION = "aMapLocation";
        String ID = "id";
        String COMMUNITY_ID = "community_id";
        String COMMUNITY_NAME = "community_name";
        String SEX = "sex";
        String SHARE_SITUATION = "share_situation";
        String SYSTEM_MESSAGE_ID = "bbs_1000";// 系统消息
        String INTERACTIVE_MESSAGE_ID = "bbs_1001";// 互动消息
        String VERSION_NAME = "version_name";//版本名
        String INTENT_PARAM_HOUSE_ID = "house_id";
        String KEYBOARD = "Keyboard";
        String BBS_COMMUNITY_BEAN = "bbs_community_bean";
        String UN_PRINT_ORDER_NUM = "un_print_order_num";
        String FLAG = "flag";
        String IS_ATTENTION = "is_attention";
        String ER_WEI_MA_URL = "er_Wei_Ma_Url";
        String USER_ICON_URL = "user_icon_url";
        String IS_REFRESH = "is_refresh";
        String INDEX = "index";
        String SELECTED_CONNECT_USB_TITLE = "selected_connect_usb_title";// 已选择的链接的usb名

    }

    interface I {
        int PAY_STATE_PAY = 1;// 支付状态--支付
        int PAY_STATE_RETURN = 2;// 支付状态--退款

        int PAY_TYPE_ALIPAY = 1;// 支付类型--支付宝
        int PAY_TYPE_WECHAT = 2;// 支付类型--微信
        int PAY_TYPE_CASH = 6;// 支付类型--现金

        int RESULT_FINISH = 888;
        int REQUEST_CODE_LGOIN_FINISH = 101;
        int REQUEST_CODE_LOCATION_FINISH = 102;
        int REQUEST_CODE_SELECT = 103;
        int REQUEST_CODE_PERSON_MAIN = 104;
        int REQUEST_CODE_SESSION_DES = 105;
    }

    interface P {
        String ID = "id";
        String IDS = "ids";
        String UID = "uid";
        String PID = "pid";
        String FID = "fid";
        String BID = "bid";
        String IS_HOME = "is_home";
        String TYPE = "type";
        String PHONE = "phone";
        String YZM = "yzm";
        String PASSWORD = "password";
        String SYSTEM_CODE = "system_code";
        String JSON = "json";
        String JPUSH_ID = "jpushid";
        String USER_NAME = "username";
        String UNIT = "unit";
        String SIMG = "simg";
        String SEX = "sex";
        String BIRTHDAY = "birthday";
        String PASS = "pass";
        String MY_TAGS = "my_tags";
        String TAGS = "tags";
        String VIDEO = "video";
        String ADDRESS = "address";
        String LONGITUDE = "longitude";
        String LATITUDE = "latitude";
        String COMMUNITY_ID = "community_id";
        String PAGE = "page";
        String GROUPID = "groupid";
        String ORD = "ord";
        String STYLE = "style";
        String TITLE = "title";
        String DESC = "desc";
        String IMG_SIMG = "img_simg";
        String IMG_W = "img_w";
        String IMG_H = "img_h";
        String PRICES = "prices";
        String PRICE = "price";
        String BBS_ID = "bbs_id";
        String CONTENT = "content";
        String STATE = "state";
        String DYNAMIC_ID = "dynamic_id";
        String STORE_ID = "store_id";
        String USERNAME = "username";
        String HIDE = "hide";
        String IS_BBS = "is_bbs";
        String USER_ID = "user_id";
        String BLACK_USER_ID = "black_user_id";
        String LAST_EDIT_TIME = "last_edit_time";
    }
}
