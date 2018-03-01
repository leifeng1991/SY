package com.xxzlkj.zhaolinshouyin.utils;

import aclasdriver.OpScale;

/**
 * 描述: 钱箱工具类
 *
 * @author zhangrq
 *         2018/1/20 9:22
 */

public class CashBoxUtils {
    /**
     * 打开钱箱
     */
    public static void openDrawer() {
        OpScale scale = new OpScale();
        scale.Open(null);
        scale.OpenDrawer();
    }
}
