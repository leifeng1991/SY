package com.xxzlkj.zhaolinshouyin;

/**
 * 描述:
 *
 * @author zhangrq
 * 2018/1/9 9:15
 */

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Presentation;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.adapter.ViceScreenInputGoodsAdapter;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;
import com.xxzlkj.zhaolinshouyin.event.ClosePayEvent;
import com.xxzlkj.zhaolinshouyin.event.FinishPayEvent;
import com.xxzlkj.zhaolinshouyin.event.InitStoreIdFinishReloadEvent;
import com.xxzlkj.zhaolinshouyin.event.OpenPayEvent;
import com.xxzlkj.zhaolinshouyin.event.RealDiscountMoneyEvent;
import com.xxzlkj.zhaolinshouyin.event.RefreshViceDisplayListEvent;
import com.xxzlkj.zhaolinshouyin.event.SelectionPositionEvent;
import com.xxzlkj.zhaolinshouyin.event.StopWorkEvent;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ViceScreenDisplay extends Presentation {

    private ViewGroup vg_left_content;
    private WebView webView;
    private TextView mTotalNumberTextView, mTotalPriceTextView, mPayTypeTextView, mRealMoneyTextView, mDiscountsMoneyTextView;
    private LinearLayout mGoodsLayout;
    private RelativeLayout mDialogRelativeLayout, mNoGoodsRelativeLayout;
    private RecyclerView mGoodsRecyclerView;
    private ImageView mPayImageView, mDefaultImageView, mLoadingImageView;
    private ViceScreenInputGoodsAdapter goodsAdapter;
    private boolean isError;// 网页加载是否错误
    private AnimationDrawable animationDrawable;
    private Timer timer = null;
    private TimerTask timerTask = null;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopAnim();
        }
    };

    public ViceScreenDisplay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vice_screen);
        findViewById();
        setListener();
        processLogic();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().unregister(this);
    }

    protected void findViewById() {
        vg_left_content = findViewById(R.id.vg_left_content);// 左边的内容
        webView = findViewById(R.id.webView);// webView
        mDialogRelativeLayout = findViewById(R.id.id_top_dialog);// 支付界面
        mGoodsLayout = findViewById(R.id.id_goods_layout);// 商品内容
        mNoGoodsRelativeLayout = findViewById(R.id.id_no_goods_layout);// 没商品
        mTotalNumberTextView = findViewById(R.id.id_goods_number);// 商品总数
        mTotalPriceTextView = findViewById(R.id.id_order_price);// 订单金额
        mGoodsRecyclerView = findViewById(R.id.id_recycler_view);// 商品列表
        mGoodsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goodsAdapter = new ViceScreenInputGoodsAdapter(getContext(), R.layout.item_vice_scree_goods_list);
        mGoodsRecyclerView.setAdapter(goodsAdapter);
        mPayImageView = findViewById(R.id.id_pay_image);// 支付类型对应的图片
        mPayTypeTextView = findViewById(R.id.id_pay_type);// 支付类型对应的文字
        mDefaultImageView = findViewById(R.id.id_default_image);// 无网络显示的图片
        mRealMoneyTextView = findViewById(R.id.id_real_money);// 实付金额
        mDiscountsMoneyTextView = findViewById(R.id.id_discounts_money);// 实金额
        mLoadingImageView = findViewById(R.id.id_loading);// loading
    }

    protected void setListener() {
        EventBus.getDefault().register(this);
    }

    protected void processLogic() {
        // 初始化 WebView
        initWebView();
        // 加载网页
        webView.loadUrl(ZLURLConstants.WEB_URL_STORE_ADS + ZLUtils.getStoreId());
    }

    /**
     * 开启定时器 开始之前如果已经开启先关闭再开启
     */
    private void startTimer() {
        // 先关闭
        stopTimer();
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    // 发送
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            };
        }
        // 延迟30s开启定时器
        if (timer != null)
            timer.schedule(timerTask, 30000);
    }

    /**
     * 关闭定时器
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 进入工作状态
     */
    private void startAnim() {
        if (vg_left_content.getWidth() == 1280) {

            ValueAnimator animator = ValueAnimator.ofInt(1280, getResources().getDimensionPixelOffset(R.dimen.vice_screen_left_width));  //定义动画
            animator.setDuration(200);
            animator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = vg_left_content.getLayoutParams();
                layoutParams.width = value;
                vg_left_content.setLayoutParams(layoutParams);
                webView.post(() -> webView.loadUrl("javascript:setwidth(" + value + ")"));
            });
            animator.start();
        }

    }

    /**
     * 结束工作状态
     */
    private void stopAnim() {
        if (vg_left_content.getWidth() == getResources().getDimensionPixelOffset(R.dimen.vice_screen_left_width)) {
            ValueAnimator animator = ValueAnimator.ofInt(getResources().getDimensionPixelOffset(R.dimen.vice_screen_left_width), 1280);  //定义动画
            animator.setDuration(200);
            animator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = vg_left_content.getLayoutParams();
                layoutParams.width = value;
                vg_left_content.setLayoutParams(layoutParams);
                webView.post(() -> webView.loadUrl("javascript:setwidth(" + value + ")"));
            });
//        webView.post(() -> webView.loadUrl("javascript:leave()"));
            animator.start();
        }

    }

    /**
     * 初始化 WebView
     */
    private void initWebView() {
        // webView的设置
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        // 在安卓5.0之前默认允许其加载混合网络协议内容，
        // 在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webView允许其加载混合网络协议内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // Horizontal水平方向，Vertical竖直方向
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // 展示错误布局
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (isError) {
                    // 错误
                    mDefaultImageView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    // 来自错误页面，显示错误布局
                    webView.postDelayed(() -> webView.reload(), 60 * 1000);
                    // 归原
                    isError = false;
                } else if (url.startsWith("http")) {
                    // 加载成功
                    mDefaultImageView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }

            }

        });
        // js交互
//        webView.addJavascriptInterface(new WebAppInterface(), "obj");
    }

    /**
     * 未初始化、未选择店铺，选择后重新加载网络地址
     *
     * @param event 商品
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initStoreIdFinishReload(InitStoreIdFinishReloadEvent event) {
        if (webView != null) {
            webView.loadUrl(ZLURLConstants.WEB_URL_STORE_ADS + ZLUtils.getStoreId());
        }
    }

    /**
     * 设置选中
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSelectPosition(SelectionPositionEvent event) {
        // 设置选中
        goodsAdapter.setSelectedPositionAndNotifySelected(event.getSelectPosition());// 设置选中位置
        mGoodsRecyclerView.smoothScrollToPosition(event.getSelectPosition());
    }

    /**
     * 结束工作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopWorkEvent(StopWorkEvent event) {
        goodsAdapter.clear();
        // 显示无购物商品
        if (goodsAdapter.getItemCount() == 0) setVisible(false);
    }

    /**
     * 支付完成
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finishPayEvent(FinishPayEvent event) {
        // 支付成功
        if (animationDrawable != null)
            animationDrawable.stop();
        mLoadingImageView.setBackgroundResource(R.mipmap.ic_pay_success);
        mPayTypeTextView.setText("支付成功");
        // 如果支付界面没有关闭
        if (mDialogRelativeLayout.getVisibility() == View.VISIBLE) {
            // 延迟0.5s关闭 结束工作
            mGoodsLayout.postDelayed(this::closePayAnimation, 1000);
            startTimer();
        }
    }

    /**
     * 打开支付界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPay(OpenPayEvent event) {
        if (vg_left_content.getWidth() == getResources().getDimensionPixelOffset(R.dimen.vice_screen_left_width) && mDialogRelativeLayout.getVisibility() == View.GONE) {
            // 当前为工作状态并且支付类型界面未显示
            mDialogRelativeLayout.setVisibility(View.VISIBLE);
//            mDialogRelativeLayout.setTranslationY(-800);
//            mDialogRelativeLayout.animate().translationY(0).setDuration(1200).start();
            mDialogRelativeLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dialog_show));
        }
        mLoadingImageView.setBackgroundResource(R.drawable.animation_pay_loading);
        animationDrawable = (AnimationDrawable) mLoadingImageView.getBackground();
        animationDrawable.start();
        // 支付类型 1：支付宝 2：微信 6：现金
        int payType = event.getPayType();
        mPayImageView.setImageResource(payType == 1 ? R.mipmap.ic_zhifubao_pay : payType == 2 ? R.mipmap.ic_weixin_pay : R.mipmap.ic_xianjin_pay);
        mPayTypeTextView.setText("支付中...");
    }

    /**
     * 关闭支付界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closePay(ClosePayEvent event) {
        // 关闭支付界面
        closePayAnimation();
    }

    /**
     * 设置实付金额和优惠金额
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setRealDiscount(RealDiscountMoneyEvent event) {
        mRealMoneyTextView.setText(String.format("实付金额：%s", StringUtil.saveTwoDecimal(event.getRealMoney())));
        mDiscountsMoneyTextView.setText(String.format("优惠金额：%s", StringUtil.saveTwoDecimal(event.getDicountsMoney())));
    }

    /**
     * 设置实付金额和优惠金额
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshViceDisplayListEvent(RefreshViceDisplayListEvent event) {
        mTotalPriceTextView.setText(event.getPrice());
        mTotalNumberTextView.setText(event.getNumber());
        List<InputGoodsBean> list = event.getList();
        if (list.size() > 0) {
            // 关闭定时器
            stopTimer();
            // 显示有商品
            setVisible(true);
            // 设置选中
            goodsAdapter.setSelectedPosition(event.getSelectPosition());
            // 先删除后添加
            goodsAdapter.clearAndAddList(list);
        } else {
            if (goodsAdapter.getItemCount() > 0) {
                // 有条目可删除
                // 删除
                goodsAdapter.clear();
            }
            goodsAdapter.setSelectedPosition(-1);
            // 显示无购物商品
            setVisible(false);
        }
    }

    private void closePayAnimation() {
        if (mDialogRelativeLayout.getVisibility() == View.VISIBLE) {
            // 支付方式界面已经显示 动画完成后设置支付界面为隐藏
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_hide);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // 设置支付界面为隐藏
                    mDialogRelativeLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mDialogRelativeLayout.startAnimation(animation);
        }
    }

    /**
     * @param isShowGoodsLayout true:显示有商品 false:显示无商品
     */
    private void setVisible(boolean isShowGoodsLayout) {
        mNoGoodsRelativeLayout.setVisibility(isShowGoodsLayout ? View.GONE : View.VISIBLE);
        mGoodsLayout.setVisibility(isShowGoodsLayout ? View.VISIBLE : View.GONE);
        if (isShowGoodsLayout) {
            // 开始工作
            startAnim();
        } else {
            // 结束工作
            stopAnim();
        }
    }

}
