package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xxzlkj.zhaolinshare.base.config.BaseConstants;
import com.xxzlkj.zhaolinshare.base.model.User;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/9/20 9:33
 */
public class UserUtils {

    public static final String BBS_ = "bbs_";

    /**
     * 获取登录用户
     */
    public static User getLoginUser(Context context) {
        String userString = PreferencesSaver.getStringAttr(context, BaseConstants.Strings.USER);
        if (!TextUtils.isEmpty(userString)) {
            // 用户没登录，保存本地保存了，用本地的
            return new Gson().fromJson(userString, User.class);
        }
        return null;
    }

    /**
     * 获取用户Id，里面处理了去userId前缀的操作
     *
     * @param userID    （必传）用户id
     * @param isGetRongYunUserId （必传）是否是获取融云的id，
     *                  true 代表获取融云的用户id，即（格式为：bbs_123456）;
     *                  false 代表获取兆邻的用户id，即（格式为：123456）
     */
    public static String getUserID(String userID, boolean isGetRongYunUserId) {
        if (isGetRongYunUserId) {
            // 融云的，判断是否拼接
            if (!TextUtils.isEmpty(userID) && !userID.contains(BBS_)) {
                // 不包含前缀， 拼接
                userID = BBS_ + userID;
            }
        } else {
            // 兆邻的，判断是否截取
            if (!TextUtils.isEmpty(userID) && userID.contains(BBS_)) {
                // 不包含前缀， 拼接
                userID = userID.substring(BBS_.length());
            }
        }
        return userID;
    }
}
