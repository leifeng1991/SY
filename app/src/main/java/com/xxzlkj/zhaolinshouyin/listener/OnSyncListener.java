package com.xxzlkj.zhaolinshouyin.listener;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/19 9:17
 */


public interface OnSyncListener {
    /**
     * 同步成功
     */
    void onSyncSuccess();

    /**
     * 同步失败
     */
    void onSyncFailed();
}
