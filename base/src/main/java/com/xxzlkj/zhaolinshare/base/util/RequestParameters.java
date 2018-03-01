package com.xxzlkj.zhaolinshare.base.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 请求参数拼接工具类
 *
 * @author zhangrq
 */
public class RequestParameters {
    /**
     * 请求参数拼接（以-拼接）
     *
     * @param questUrl   请求地址
     * @param parameters 参数
     */
    public static String appendParameters(String questUrl, String... parameters) {
        if (TextUtils.isEmpty(questUrl))
            return "";

        if (parameters == null || parameters.length == 0)
            return questUrl;
        StringBuilder builder = new StringBuilder(questUrl);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null || parameters[i].equals(""))
                parameters[i] = "null";
            builder.append(parameters[i]);
            if (i != parameters.length - 1) {
                builder.append("-");
            }
        }
        return builder.toString();
    }

    /**
     * 请求参数拼接（以&拼接）
     *
     * @param questUrl   请求地址
     * @param parameters 参数
     */
    public static String appendParametersOther(String questUrl,
                                               String... parameters) {
        if (TextUtils.isEmpty(questUrl))
            return "";

        if (parameters == null || parameters.length == 0)
            return questUrl;
        StringBuilder builder = new StringBuilder(questUrl);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null || parameters[i].equals(""))
                parameters[i] = "null";
            builder.append(parameters[i]);
            if (i != parameters.length - 1) {
                builder.append("&");
            }
        }
        return builder.toString();
    }

    /**
     * 获取请求地址，后面拼接的参数
     *
     * @param urlString 请求地址
     * @return 返回拼接的参数，如果没有参数则返回“”
     */
    public static String getParameters(String urlString) {
        if (urlString == null)
            return "";
        int lastIndexOf = urlString.lastIndexOf("/");
        if (lastIndexOf == -1) //请求地址没有“/”
            return "";

        return urlString.substring(lastIndexOf + 1, urlString.length());
    }

    /**
     * 根据请求内容获取请求参数
     * @param parString 请求参数内容
     * @return 参数集合
     */
    public static ArrayList<String> getParArrayList(String parString) {
        ArrayList<String> parArrayList = new ArrayList<>();
        if (parString == null || parString.length() == 0) {
            return parArrayList;
        }
        String[] parArray = parString.split("-");
        if (parArray.length > 0) {
            //含有“-”
            Collections.addAll(parArrayList, parArray);
        }

        return parArrayList;
    }
}
