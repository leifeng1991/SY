package com.xxzlkj.zhaolinshare.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 描述:和时间相关的工具类
 *
 * @author zhangrq
 *         2017/1/3 10:46
 */

public class DateUtils {
    /**
     * 获取时间戳timeInMillis的星期几
     *
     * @param timeInMillis 毫秒的时间戳
     * @return 返回格式为：星期XX
     */
    public static String getDayOfWeek(long timeInMillis) {
        //设置时间值
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(timeInMillis);
        int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK) - 1;
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weekDays[dayOfWeek];
    }

    /**
     * 获取时间戳timeInMillis的年月日
     *
     * @param timeInMillis 毫秒的时间戳
     * @return 返回格式为：XX年XX月XX日
     */
    public static String getYearMonthDay(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("yyyy年MM月dd日").format(new Date(timeInMillis));
    }

    public static String getMonthDay(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("M月d日").format(new Date(timeInMillis));
    }

    public static String getYearMonthDay(long timeInMillis, String pattern) {
        //设置时间值
        return new SimpleDateFormat(pattern).format(new Date(timeInMillis));
    }

    public static String getMonthDayHourMinute(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("MM-dd HH:mm").format(new Date(timeInMillis));
    }

    public static String getYearMonthDayHourMinute(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(timeInMillis));
    }

    public static String getYearMonthDayHourMinute2(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(timeInMillis));
    }

    public static String getYearMonthDayHourMinuteSeconds(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeInMillis));
    }

    public static String getYearMonthDayHourMinuteSeconds2(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(timeInMillis));
    }

    public static String getYearMonthDay2(long timeInMillis) {
        //设置时间值
        return new SimpleDateFormat("yyyy.MM.dd").format(new Date(timeInMillis));
    }

    /**
     * 获取时间戳timeInMillis的年月日
     *
     * @param timeInMillis 毫秒的时间戳
     * @return 返回说明：int[0]:年、int[1]:月（1-12）、int[2]:日
     */
    public static int[] getYearMonthDate(long timeInMillis) {
        int[] ints = new int[3];
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(timeInMillis);
        ints[0] = ca.get(Calendar.YEAR);//获取年份
        ints[1] = ca.get(Calendar.MONTH) + 1;//获取月份
        ints[2] = ca.get(Calendar.DATE);//获取日
        return ints;
    }

    public static long getTimeInMillis(String source) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(source);
        return date.getTime() / 1000;
    }

    /**
     * 将时间字符串转换为时间值（毫秒）
     */
    public static long getTimeInMillis(String pattern, String source) {
        try {
            return new SimpleDateFormat(pattern).parse(source).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 把这个格式2017-03-18 11:18:21解析
     * 返回 String[0] = 2017-03-18
     * 返回 String[1] = 11:18
     */
    public static String[] splitDataStr(String rawTime) {
        String[] out = new String[2];
        out[0] = "";
        out[1] = "";
        if (rawTime == null)
            return out;
        String[] split = rawTime.split(" ");
        if (split.length > 0) {
            out[0] = split[0];
        }
        if (split.length > 1) {
            // 再截取最后的：
            String[] split1 = split[1].split(":");
            String time = "";
            if (split1.length > 0) {
                time += split1[0] + ":";
            }
            if (split1.length > 1) {
                time += split1[1];
            }
            out[1] = time;
        }
        return out;
    }

    /**
     * 判断当前日期是星期几
     */
    public static String getDayForWeek(long time) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time));
        int dayForWeek = 0;
        String week = null;
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        switch (dayForWeek) {
            case 1:
                week = "周一";
                break;
            case 2:
                week = "周二";
                break;
            case 3:
                week = "周三";
                break;
            case 4:
                week = "周四";
                break;
            case 5:
                week = "周五";
                break;
            case 6:
                week = "周六";
                break;
            case 7:
                week = "周日";
                break;
        }
        return week;
    }

    public static String getHour(String numStr) {
        int num = NumberFormatUtils.toInt(numStr);
        double v = num * 0.5;
        return StringUtil.subZeroAndDot(v + "") + "小时";
    }

    /**
     * 获取时间间隔
     *
     * @param time
     * @return
     */
    public static String getTimeType(long time) {
        LogUtil.e("getTimeType", time + "");
        String timeStr;
        if (time < 60000) {
            timeStr = "刚刚";
        } else if (time >= 60000 && time < 3600000) {
            timeStr = (time / 60000) + "分钟前";
        } else if (time >= 3600000 && time < 86400000) {
            timeStr = (time / 3600000) + "小时前";
        } else if (time >= 86400000 && time < 2592000000L) {
            timeStr = (time / 86400000) + "天前";
        } else {
            timeStr = "30天前";
        }

        return timeStr;
    }
}
