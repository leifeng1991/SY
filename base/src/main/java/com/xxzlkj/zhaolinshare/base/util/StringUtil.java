package com.xxzlkj.zhaolinshare.base.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 字符串的工具类
 *
 * @author zhangrq
 */
public class StringUtil {
    /**
     * 对里面的内容content进行判断，为空则返回——
     */
    public static String getContent(String content) {
        return getContent(content, "——");
    }

    /**
     * 对里面的内容content进行判断，为空则返回defaultString
     */
    public static String getContent(String content, String defaultString) {
        return TextUtils.isEmpty(content) ? defaultString : content;
    }

    /**
     * 编辑为开头保留startKeepLength长度，结尾保留endKeepLength长度
     *
     * @param startKeepLength 开头保留的长度
     * @param endKeepLength   结尾保留的长度
     */
    public static String editIdNumber(String content, int startKeepLength,
                                      int endKeepLength) {
        if (content != null && startKeepLength >= 0 && endKeepLength >= 0
                && content.length() > startKeepLength + endKeepLength) {
            char[] charArray = content.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                if (i >= startKeepLength
                        && i < charArray.length - endKeepLength) {
                    charArray[i] = '*';
                }
            }
            return new String(charArray);
        }
        return content;
    }

    /**
     * 开头保留startKeepLength位数，之后全部为*
     *
     * @param content         内容
     * @param startKeepLength 开头保留的长度
     */
    public static String editName(String content, int startKeepLength) {
        if (content != null && startKeepLength >= 0 && content.length() > startKeepLength) {
            char[] charArray = content.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                if (i >= startKeepLength) {
                    charArray[i] = '*';
                }
            }
            return new String(charArray);
        }
        return content;
    }

    /**
     * 小数的 取小数点和零 如 12.30 -> 12.3 | 23.00 -> 23
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 给金额显示添加千分位","
     *
     * @param val 金额
     */
    public static String parseMoney(Object val) {
        try {
            String pattern = "##,###,##0.00";
            if (val == null || val.equals(""))
                return "";
            String valStr = val + "";
            DecimalFormat df = new DecimalFormat(pattern);
            valStr = df.format(new BigDecimal(valStr));
            return valStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "——";
        }
    }

    /**
     * 格式化手机号
     *
     * @param phoneNumberStr 手机号
     * @return 返回格式为：151 6666 8888
     */
    public static String formatPhoneNumber(String phoneNumberStr) {
        if (phoneNumberStr == null || phoneNumberStr.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = phoneNumberStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 3 || (i - 3) % 4 == 0)
                sb.append(" ");
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * 折扣数字转中文汉字
     *
     * @param numStr 折扣数字
     * @return 中文汉字
     */
    public static String discountNumber2ChineseText(String numStr) {
        double numDouble = NumberFormatUtils.toDouble(numStr);
        int valueInt = (int) (numDouble * 100);
        if (valueInt > 100 || valueInt < 1) {
            // 小于1 或者 大于100 都是有问题的
            return "——";
        }
        String valueStr = valueInt + "";
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = valueStr.toCharArray();
        for (char aChar : chars) {
            String aCharStr = "";
            switch (aChar) {
                case '1':
                    aCharStr = "一";
                    break;
                case '2':
                    aCharStr = "二";
                    break;
                case '3':
                    aCharStr = "三";
                    break;
                case '4':
                    aCharStr = "四";
                    break;
                case '5':
                    aCharStr = "五";
                    break;
                case '6':
                    aCharStr = "六";
                    break;
                case '7':
                    aCharStr = "七";
                    break;
                case '8':
                    aCharStr = "八";
                    break;
                case '9':
                    aCharStr = "九";
                    break;
                case '0':
                    aCharStr = "零";
                    break;

            }
            stringBuilder.append(aCharStr);
        }

        return stringBuilder.toString().replaceAll("零", "");
    }

    /**
     * 保留三位小数
     *
     * @return 金额四舍五入
     */
    public static String saveThreeDecimal(Object price) {
        try {
            DecimalFormat df = new DecimalFormat("##0.000");
            if (price instanceof String) {
                return df.format(new BigDecimal((String) price));
            } else
                return df.format(price);

        } catch (Exception e) {
            e.printStackTrace();
            return "- -";
        }
    }

    /**
     * 保留两位小数
     *
     * @return 金额四舍五入，例如：0.22222 返回0.22；0.55555 返回0.56
     */
    public static String saveTwoDecimal(Object price) {
        try {
            DecimalFormat df = new DecimalFormat("##0.00");
            if (price instanceof String) {
                return df.format(new BigDecimal((String) price));
            } else
                return df.format(price);

        } catch (Exception e) {
            e.printStackTrace();
            return "- -";
        }
    }

    /**
     * 保留一位小数
     *
     * @return 金额四舍五入，例如：0.22222 返回0.2；0.55555 返回0.6
     */
    public static String saveOneDecimal(Object price) {
        try {
            DecimalFormat df = new DecimalFormat("##0.0");
            if (price instanceof String) {
                return df.format(new BigDecimal((String) price));
            } else
                return df.format(price);

        } catch (Exception e) {
            e.printStackTrace();
            return "- -";
        }
    }

    /**
     * 保留一位小数
     *
     * @return 金额四舍五入，例如：0.22222 返回0.2；0.55555 返回0.6
     */
    public static String saveZeroDecimal(Object price) {
        try {
            DecimalFormat df = new DecimalFormat("##0");
            if (price instanceof String) {
                return df.format(new BigDecimal((String) price));
            } else
                return df.format(price);

        } catch (Exception e) {
            e.printStackTrace();
            return "- -";
        }
    }

    /**
     * 保留整数
     */
    public static String saveInt(Object price) {
        try {
            DecimalFormat df = new DecimalFormat("##0");
            if (price instanceof String) {
                return df.format(new BigDecimal((String) price));
            } else
                return df.format(price);

        } catch (Exception e) {
            e.printStackTrace();
            return "- -";
        }
    }

    /**
     * 相加 結果保留两位小数
     *
     * @param doubleValA
     * @param doubleValB
     * @return
     */
    public static double add(String doubleValA, String doubleValB) {
        BigDecimal a2 = new BigDecimal(doubleValA);
        BigDecimal b2 = new BigDecimal(doubleValB);
        BigDecimal b = new BigDecimal(a2.add(b2).doubleValue());
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 相減 結果保留两位小数
     *
     * @param doubleValA
     * @param doubleValB
     * @return
     */
    public static double sub(String doubleValA, String doubleValB) {
        BigDecimal a2 = new BigDecimal(doubleValA);
        BigDecimal b2 = new BigDecimal(doubleValB);
        BigDecimal b = new BigDecimal(a2.subtract(b2).doubleValue());
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 相乘 結果保留两位小数
     *
     * @param doubleValA
     * @param doubleValB
     * @return
     */
    public static double mul(String doubleValA, String doubleValB) {
        BigDecimal a2 = new BigDecimal(doubleValA);
        BigDecimal b2 = new BigDecimal(doubleValB);
        BigDecimal b = new BigDecimal(a2.multiply(b2).doubleValue());
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 相除
     *
     * @param doubleValA
     * @param doubleValB
     * @param scale      除不尽时指定精度
     * @return
     */
    public static double div(String doubleValA, String doubleValB, int scale) {
        BigDecimal a2 = new BigDecimal(doubleValA);
        BigDecimal b2 = new BigDecimal(doubleValB);
        return a2.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}