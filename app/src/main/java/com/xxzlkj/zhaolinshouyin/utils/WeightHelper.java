package com.xxzlkj.zhaolinshouyin.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import aclasdriver.OpScale;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/20 11:04
 */
public class WeightHelper {
    private static final int MESSAGE_WEIGHT_CHANGED = 12;
    private OpScale mOpScale = null;
    private OpScale.WeightInfo mLastWeightInfo;
    private OpScale.WeightChangedListener mLastWeightChangedListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WEIGHT_CHANGED:
                    if (mLastWeightChangedListener != null && mLastWeightInfo != null) {
                        // 通知改变
                        mLastWeightChangedListener.onWeightChanged(mLastWeightInfo);
                    }
                    break;
            }
        }
    };

    /**
     * 打开称重
     */
    public void openWeight(OpScale.WeightChangedListener listener) {
        // 归原
        closeWeight();

        mOpScale = new OpScale();
        if (mOpScale.Open(null) == 0) {
            mOpScale.SetWeightChangedListener(weightInfo -> {
                if (listener != null && weightInfo != null) {
                    // 发到主线程
                    mLastWeightInfo = weightInfo;
                    mLastWeightChangedListener = listener;
                    mHandler.sendEmptyMessage(MESSAGE_WEIGHT_CHANGED);
                }
            });
        } else {
            mOpScale = null;
        }
    }

    /**
     * 关闭称重
     */
    public void closeWeight() {
        if (mOpScale != null) {
            mOpScale.Close();
            mOpScale = null;
        }
    }
}
