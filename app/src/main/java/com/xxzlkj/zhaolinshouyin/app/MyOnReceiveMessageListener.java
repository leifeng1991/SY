package com.xxzlkj.zhaolinshouyin.app;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.google.gson.Gson;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.ReceiveOrderDialogActivity;
import com.xxzlkj.zhaolinshouyin.event.ReceiveOrderPushEvent;
import com.xxzlkj.zhaolinshouyin.model.RongPushBean;

import org.greenrobot.eventbus.EventBus;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/5/22 11:27
 */
public class MyOnReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
    private final Context mContext;
    private MediaPlayer mediaPlayer;

    MyOnReceiveMessageListener(Context context) {
        this.mContext = context;
    }

    /**
     * 收到消息的处理。
     *
     * @param message 收到的消息实体。
     * @param left    剩余未拉取消息数目。
     */
    @Override
    public boolean onReceived(Message message, int left) {
        // 开发者根据自己需求自行处理
        System.out.println("接收到新消息" + message);
        MessageContent content = message.getContent();
        if (content instanceof TextMessage) {
            // 发的是文本消息
            try {
                String jsonStr = ((TextMessage) content).getContent();
                System.out.println("收到消息=" + jsonStr);
                RongPushBean rongPushBean = new Gson().fromJson(jsonStr, RongPushBean.class);
                if ("messageTypeSystem".equals(rongPushBean.getContent())) {
                    // 线上订单推送
                    // 弹框提示
                    Intent intent = ReceiveOrderDialogActivity.newIntent(mContext, rongPushBean);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    // 播放声音
                    playSound(mContext, R.raw.new_order_remark);
                    // 显示总的数量
                    EventBus.getDefault().postSticky(new ReceiveOrderPushEvent(NumberFormatUtils.toInt(rongPushBean.getNot_operating())));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void playSound(Context context, int rawId) {
        if (mediaPlayer == null) {
            String uriStr = "android.resource://" + context.getPackageName() + "/" + rawId;
            Uri uri = Uri.parse(uriStr);
            mediaPlayer = MediaPlayer.create(context, uri);
        }
        if (!mediaPlayer.isPlaying()) {
            // 没播放，播放
            setAudioMax(context, AudioManager.STREAM_MUSIC);
            mediaPlayer.start();
        }
    }

    private static void setAudioMax(Context context, int streamSystem) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            //最大音量
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(streamSystem, maxVolume, 0);
        }
    }

}
