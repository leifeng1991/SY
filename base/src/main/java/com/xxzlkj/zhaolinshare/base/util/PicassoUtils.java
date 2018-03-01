package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;
import com.xxzlkj.zhaolinshare.base.config.BaseConstants;
import com.xxzlkj.zhaolinshare.base.config.BaseURLConstants;
import com.xxzlkj.zhaolinshare.base.model.GlideApp;
import com.xxzlkj.zhaolinshare.base.model.TXSignBean;
import com.xxzlkj.zhaolinshare.base.net.OnBaseRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * picasso的工具类
 *
 * @author zhangrq
 */
public class PicassoUtils {

    private static final String SIGN_BIG = "?sign=";
    public static final String SIGN_SMALL = "?imageView2/2/w/300&sign=";
    public static final String SIGN_MIDDLE = "?imageView2/2/w/800&sign=";
    private static int PERIOD = 900 * 1000;//900秒
    private static Handler mGetImageSignatureByNetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 获取网络数据
            Context context = (Context) msg.obj;
            getImageSignatureByNet(context, null);
        }
    };


    /**
     * 设置图片，先获取图片签名，后设置图片（此为兆邻规则）
     *
     * @param context           上下文
     * @param imgUrl            网络图片地址
     * @param defaultResourceId 默认显示的图片资源id
     * @param imageView         控件
     */
    private static void setImageByImageSignature(final Context context, final String imgUrl, final String flagStr,
                                                 final int defaultResourceId, final ImageView imageView) {

        if (!TextUtils.isEmpty(imgUrl)) {
            // 地址没有问题，获取图片
            getImageSignatureInMainThread(context, new OnGetImageSignatureListener() {
                @Override
                public void onSuccess(String sign) {
                    // 获取签名成功，下载图片
                    setImageRaw(context, imgUrl + flagStr + sign, defaultResourceId, imageView);
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    // 获取签名失败，不处理

                }
            });
        } else {
            // 地址有问题，不处理或显示默认图片
            if (defaultResourceId != -1)// 有默认图片显示
                GlideApp.with(context).load(defaultResourceId).into(imageView);
        }
    }

    /**
     * 设置图片，Picasso最原始的使用方法
     */
    private static void setImageRaw(Context context, String imgUrl, int defaultResourceId, ImageView imageView) {
        if (TextUtils.isEmpty(imgUrl)) {
            // 图片有问题
            if (defaultResourceId != -1) {
                // 有默认图片显示
                GlideApp.with(context).load(defaultResourceId).into(imageView);
            }
        } else {
            // 图片没问题
            if (defaultResourceId != -1)// 有默认图片
                GlideApp.with(context).load(imgUrl).placeholder(defaultResourceId).error(defaultResourceId).into(imageView);
            else {
                GlideApp.with(context).load(imgUrl).centerCrop().into(imageView);
            }
        }

    }

    /**
     * 设置图片，Picasso最原始的使用方法
     */
    public static void setImageRaw(Context context, String imgUrl, ImageView imageView) {
        setImageRaw(context, imgUrl, -1, imageView);
    }

    /**
     * 设置图片，Picasso最原始的使用方法
     */
    public static void setImageRaw(Context context, Uri imgUrl, ImageView imageView) {
        GlideApp.with(context).load(imgUrl).centerCrop().into(imageView);
    }

    /**
     * 设置图片，Picasso最原始的使用方法
     */
    public static void setImageRaw(Context context, File file, ImageView imageView) {
        GlideApp.with(context).load(file).centerCrop().into(imageView);
    }

    /**
     * 设置大图片
     */
    public static void setImageBig(Context context, String imgUrl, ImageView imageView) {
        setImageByImageSignature(context, imgUrl, SIGN_BIG, -1, imageView);
    }

    /**
     * 设置大图片
     */
    public static void setImageBig(Context context, String imgUrl, int defaultResourceId, ImageView imageView) {
        setImageByImageSignature(context, imgUrl, SIGN_BIG, defaultResourceId, imageView);
    }

    /**
     * 设置中图片
     */
    public static void setImageMiddle(Context context, String imgUrl, ImageView imageView) {
        setImageByImageSignature(context, imgUrl, SIGN_MIDDLE, -1, imageView);
    }

    /**
     * 设置小图片
     */
    public static void setImageSmall(Context context, String imgUrl, ImageView imageView) {
        setImageByImageSignature(context, imgUrl, SIGN_SMALL, -1, imageView);
    }

    /**
     * 设置小图片
     */
    public static void setImageSmall(Context context, String imgUrl, int defaultResourceId, ImageView imageView) {
        setImageByImageSignature(context, imgUrl, SIGN_SMALL, defaultResourceId, imageView);
    }

    /**
     * 设置大图片
     */
    public static void setImageBigNoCenterCrop(Context context, String imgUrl, ImageView imageView, RequestListener<Drawable> listener) {
        if (!TextUtils.isEmpty(imgUrl)) {
            // 地址没有问题，获取图片
            getImageSignatureInMainThread(context, new OnGetImageSignatureListener() {
                @Override
                public void onSuccess(String sign) {
                    // 获取签名成功，下载图片
                    GlideApp.with(context).load(imgUrl + SIGN_BIG + sign).listener(listener).into(imageView);
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    // 获取签名失败，不处理
                    listener.onLoadFailed(null, null, null, false);
                }
            });
        }
    }

    /**
     * 设置控件宽和高后，再设置图片
     */
    public static void setWithAndHeight(Context context, String imgUrl, int width, int height, ImageView imageView) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setImageByImageSignature(context, imgUrl, SIGN_BIG, -1, imageView);
    }

    /**
     * 获取网络图片签名，获取成功后，保存到了本地
     */
    private static void getImageSignatureByNet(final Context context, final OnGetImageSignatureListener listener) {
        RequestManager.createRequest(BaseURLConstants.TX_IMG_SIGN_URL, null, new OnBaseRequestListener<TXSignBean>() {

            @Override
            public void handlerSuccess(TXSignBean bean) {
                if ("200".equals(bean.getCode())) {
                    String sign = bean.getData().getSign();
                    PreferencesSaver.setStringAttr(context, BaseConstants.Strings.IMAGE_TOKEN, sign);
                    PreferencesSaver.setLongAttr(context, BaseConstants.Strings.IMAGE_TOKEN_TIME, System.currentTimeMillis());
                    if (listener != null)
                        listener.onSuccess(sign);
                } else {
                    PreferencesSaver.setStringAttr(context, BaseConstants.Strings.IMAGE_TOKEN, "");
                    if (listener != null)
                        listener.onError(404, bean.getMessage());
                }


            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {
                if (listener != null)
                    listener.onError(errorCode, errorMessage);
            }

        });
    }

    /**
     * 获取图片签名，本地有并未超时，获取本地的，否则获取网络的，此方法必须在主线程调用（因为里面有网络请求）
     *
     * @param listener 获取签名后的回调
     */
    public static void getImageSignatureInMainThread(final Context context, final OnGetImageSignatureListener listener) {
        String imageToken = PreferencesSaver.getStringAttr(context, BaseConstants.Strings.IMAGE_TOKEN);
        long tokenTimeMillis = PreferencesSaver.getLongAttr(context, BaseConstants.Strings.IMAGE_TOKEN_TIME);
        boolean isFailure = System.currentTimeMillis() - tokenTimeMillis > PERIOD;
        if (TextUtils.isEmpty(imageToken) || isFailure) {
            // imageToken为空，或者失效的，获取网络图片
            getImageSignatureByNet(context, listener);
        } else {
            if (listener != null)
                listener.onSuccess(imageToken);
        }
    }

    /**
     * 开启签名轮询，每过一段时间，从网络重新获取下新的签名
     */
    public static void startLooperToken(final Context context) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                String oldSign = PreferencesSaver.getStringAttr(context, BaseConstants.Strings.IMAGE_TOKEN);
                long tokenTimeMillis = PreferencesSaver.getLongAttr(context, BaseConstants.Strings.IMAGE_TOKEN_TIME);
                boolean isFailure = System.currentTimeMillis() - tokenTimeMillis > PERIOD;
                if (TextUtils.isEmpty(oldSign) || isFailure) {
                    // 签名为空，或者超时失效，请求新的
                    Message message = new Message();
                    message.obj = context;
                    mGetImageSignatureByNetHandler.sendMessage(message);
                }
            }
        };
        // 常用于轮询
        timer.schedule(timerTask, 0, PERIOD);// 5000(5秒)后，开始执行第一次run方法，此后每隔2秒调用一次
    }


    public interface OnGetImageSignatureListener {
        void onSuccess(String sign);

        void onError(int errorCode, String errorMsg);
    }
}