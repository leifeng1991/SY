package com.xxzlkj.zhaolinshare.base.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类
 *
 * @author zhangrq
 */
public class DecriptUtil {
    /**
     * Md5加密
     *
     * @param str 加密内容
     * @return Md5加密后的内容
     */
    public static String encryptMD5(String str) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = md5.digest();
        StringBuilder builder = new StringBuilder();
        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1)
                builder.append("0").append(Integer.toHexString(0xFF & aByteArray));
            else
                builder.append(Integer.toHexString(0xFF & aByteArray));

        }
        return builder.toString();
    }

    public static String encryptSHA1(String decrypt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decrypt.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
