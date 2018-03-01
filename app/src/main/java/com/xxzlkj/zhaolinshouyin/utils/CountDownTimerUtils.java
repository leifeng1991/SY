package com.xxzlkj.zhaolinshouyin.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.DateUtils;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/12 13:52
 */
public class CountDownTimerUtils {
    /**
     * 设置时钟
     *
     * @param textView 要显示时间的TextView
     */
    public static void addCountDownTimer(TextView textView) {
        if (textView == null) {
            return;
        }
        // 设置时间
        CountDownTimer countDownTimer = new CountDownTimer(Integer.MAX_VALUE, 1000) {
            // 30000(30秒)倒计时，每1000(1秒)调用一下onTick();
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText(DateUtils.getYearMonthDayHourMinuteSeconds(System.currentTimeMillis()));
            }

            @Override
            public void onFinish() {

            }

        };
        countDownTimer.start(); // 开始执行
        textView.setTag(countDownTimer);
    }

    /**
     * 移除显示
     *
     * @param textView 要显示时间的TextView
     */
    public static void removeCountDownTimer(TextView textView) {
        if (textView != null && textView.getTag() != null && textView.getTag() instanceof CountDownTimer) {
            CountDownTimer countDownTimer = (CountDownTimer) textView.getTag();
            countDownTimer.cancel();
            textView.setTag(null);
        }
    }
}
