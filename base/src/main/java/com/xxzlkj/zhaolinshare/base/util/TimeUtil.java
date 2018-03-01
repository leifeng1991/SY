package com.xxzlkj.zhaolinshare.base.util;

/**
 * Created by Administrator on 2017/3/25.
 */

public class TimeUtil {
    //毫秒换成00:00:00:00
    public static String getCountTimeByLong(long finishTime) {
        int totalTime = (int) (finishTime / 1000);//秒
        int hour = 0, minute = 0, second = 0;
        int millisencond = (int) ((finishTime % 1000) / 100);
        if (3600 <= totalTime) {
            hour = totalTime / 3600;
            totalTime = totalTime - 3600 * hour;
        }
        if (60 <= totalTime) {
            minute = totalTime / 60;
            totalTime = totalTime - 60 * minute;
        }
        if (0 <= totalTime) {
            second = totalTime;
        }
        StringBuilder sb = new StringBuilder();

        if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (minute < 10) {
            sb.append("0").append(minute).append(":");
        } else {
            sb.append(minute).append(":");
        }
        if (second < 10) {
            sb.append("0").append(second).append(":");
        } else {
            sb.append(second).append(":");
        }
        if (millisencond < 10){
            sb.append(millisencond);
        }else {
            sb.append(millisencond);
        }
//        LogUtil.e("TimeUtil",finishTime+"="+hour+"="+minute+"="+second+"="+millisencond);
        return sb.toString();

    }
}
