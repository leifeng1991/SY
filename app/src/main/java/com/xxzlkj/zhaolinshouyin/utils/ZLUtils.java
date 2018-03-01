package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshouyin.activity.InitStoreActivity;

import java.util.Locale;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/14 17:08
 */
public class ZLUtils {
    /**
     * 获取store_id，没有做登录操作
     *
     * @return 没有返回""
     */
    public static String getStoreIdDoInit(Activity activity) {
        Context context = activity.getApplicationContext();
        String store_id = getStoreId();
        if (TextUtils.isEmpty(store_id)) {
            activity.startActivity(InitStoreActivity.newIntent(context));
        }
        return store_id;
    }

    /**
     * 获取店铺名字
     */
    public static String getStoreTitle() {
        return DaoUtils.getParams().getStore_title();
    }

    /**
     * 获取店铺id
     */
    public static String getStoreId() {
        return DaoUtils.getParams().getStore_id();
    }

    /**
     * 获取设备号，保留了两位数字
     */
    public static String getDevicesNum() {
        return String.format(Locale.CHINA, "%02d", NumberFormatUtils.toInt(DaoUtils.getParams().getDevices_num()));
    }

    /**
     * 获取优惠码
     *
     * @return 返回null代表没有
     */
    public static String getDiscountCodeValue(String content) {
        String flagCode = "code=";
        if (content != null && content.contains(flagCode)) {
            return content.substring(content.indexOf(flagCode) + flagCode.length());
        }
        return null;
    }
}
