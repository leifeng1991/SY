package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xxzlkj.zhaolinshare.base.base.TitleFragmentPagerAdapter;
import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.event.ClosePayEvent;
import com.xxzlkj.zhaolinshouyin.event.OpenPayEvent;
import com.xxzlkj.zhaolinshouyin.fragment.CashPayFragment;
import com.xxzlkj.zhaolinshouyin.fragment.TriplicitiesPayFragment;
import com.xxzlkj.zhaolinshouyin.listener.OnPayListener;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/1/7 13:16
 */

public class PayDialogActivity extends AppCompatActivity {
    public static final String PAY_TYPE = "pay_type";
    public static final String PAY_MONEY = "payMoney";
    public static final String PAY_STATE = "payState";
    private static OnPayListener onPayListener;
    private int payType, payState;
    private double payMoney;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageView mDeleteImageView;
    private StringBuffer mStringBufferResult = new StringBuffer(); // 扫码内容
    private List<Fragment> fragments;
    private boolean mCaps;
    private boolean isNetAvailable;
    private String discountCodeValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadViewLayout();
        findViewById();
        setListener();
        processLogic();
        setFinishOnTouchOutside(false);
    }

    protected void loadViewLayout() {
        setContentView(R.layout.dialog_pay_layout);
    }

    protected void findViewById() {
        mDeleteImageView = findViewById(R.id.id_delete);// 关闭
        mTabLayout = findViewById(R.id.id_tab_layout);
        mViewPager = findViewById(R.id.id_view_pager);
    }

    protected void setListener() {
        // 关闭按钮
        mDeleteImageView.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new ClosePayEvent(false));
            finish();
        });
    }

    protected void processLogic() {
        payState = getIntent().getIntExtra(PAY_STATE, C.I.PAY_STATE_PAY);// 支付状态   1：支付 2：退款
        payType = getIntent().getIntExtra(PAY_TYPE, 0);//  支付类型  1：支付宝 2：微信 6：现金
        payMoney = getIntent().getDoubleExtra(PAY_MONEY, 0);// 支付金额
        // 初始化
        fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        if (payState == C.I.PAY_STATE_PAY) {
            // 支付状态，全部显示
            titles.add("支付宝");
            titles.add("微信");
            titles.add("现金");
            fragments.add(TriplicitiesPayFragment.newInstance(payMoney, 1, onPayListener));
            fragments.add(TriplicitiesPayFragment.newInstance(payMoney, 2, onPayListener));
            fragments.add(CashPayFragment.newInstance(payState, payMoney, onPayListener));
            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    // 通知副屏打开支付界面
                    EventBus.getDefault().postSticky(new OpenPayEvent(position == 0 ? 1 : position == 1 ? 2 : 6));
                    // 检测优惠金额，退款的收银页面已拦截
                    setOnPayListenerOnCheckDiscount(discountCodeValue, position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
            mViewPager.addOnPageChangeListener(onPageChangeListener);
            onPageChangeListener.onPageSelected(0);
        } else {
            // 退款状态，只显示现金
            titles.add("现金退款");
            fragments.add(CashPayFragment.newInstance(payState, payMoney, onPayListener));
            mTabLayout.setBackgroundResource(R.drawable.selector_tab_bg);
        }
        TitleFragmentPagerAdapter mPagerAdapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(fragments.size());
        mTabLayout.setupWithViewPager(mViewPager);
        // 跳到某个位置
        int currentPosition = 0;
        switch (payType) {
            case 1:
                // 支付宝
                currentPosition = 0;
                break;
            case 2:
                // 微信
                currentPosition = 1;
                break;
            case 6:
                // 现金
                currentPosition = 2;
                break;
        }
        mViewPager.setCurrentItem(currentPosition);

        isNetAvailable = NetStateManager.isAvailable(getApplicationContext());// 当前网络是否可用，可用，则一直只用此网络，否则使用本地
        // 一进来就检测优惠金额，退款的收银页面已拦截
        if (onPayListener != null)
            onPayListener.onCheckDiscount(isNetAvailable, "", payType);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //字母大小写判断
        checkLetterStatus(event);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
                // 字母
                mStringBufferResult.append((char) ((mCaps ? 'A' : 'a') + keyCode - KeyEvent.KEYCODE_A));
            } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                // 数字
                mStringBufferResult.append((char) ('0' + keyCode - KeyEvent.KEYCODE_0));
            } else {
                //其他符号
                switch (keyCode) {
                    case KeyEvent.KEYCODE_SEMICOLON:
                        mStringBufferResult.append(mCaps ? ':' : ';');
                        break;
                    case KeyEvent.KEYCODE_SLASH:
                        mStringBufferResult.append(mCaps ? '?' : '/');
                        break;
                    case KeyEvent.KEYCODE_PERIOD:
                        mStringBufferResult.append('.');
                        break;
                    case KeyEvent.KEYCODE_EQUALS:
                        mStringBufferResult.append(mCaps ? '+' : '=');
                        break;
                }

            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
                String string = mStringBufferResult.toString();
                mStringBufferResult.setLength(0);
                // 输入扫描码，支付
                if (string.startsWith("http") && string.contains("code=")) {
                    // 优惠券、会员等
                    String discountCodeValue = ZLUtils.getDiscountCodeValue(string);
                    if (!TextUtils.isEmpty(discountCodeValue) && discountCodeValue.contains("cc")) {
                        // 优惠券
                        this.discountCodeValue = discountCodeValue;
                        setOnPayListenerOnCheckDiscount(discountCodeValue, mViewPager.getCurrentItem());

                    } else {
                        // 错误或未开发的会员
                        ToastManager.showShortToast(this, "码格式错误");
                    }
                } else {
                    // 扫描到的支付码
                    int currentItem = mViewPager.getCurrentItem();
                    if (currentItem < fragments.size() && fragments.get(currentItem) instanceof TriplicitiesPayFragment) {
                        TriplicitiesPayFragment fragment = (TriplicitiesPayFragment) fragments.get(currentItem);
                        // 设置扫描到的码，并请求
                        fragment.setScanCodeAndRequestPay(string);
                    }
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    //检查shift键
    private void checkLetterStatus(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            //按着shift键，表示大写
            //松开shift键，表示小写
            mCaps = event.getAction() == KeyEvent.ACTION_DOWN;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 通知关闭副屏支付界面
        EventBus.getDefault().postSticky(new ClosePayEvent(false));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            // 通知关闭副屏支付界面
            EventBus.getDefault().postSticky(new ClosePayEvent(false));
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    /**
     * 检测优惠金额
     *
     * @param discountCodeValue 优惠券
     * @param currentIndex      ViewPager当前的位置
     */
    private void setOnPayListenerOnCheckDiscount(String discountCodeValue, int currentIndex) {
        int payment = 0;
        switch (currentIndex) {
            case 0:
                // 支付宝
                payment = 1;
                break;
            case 1:
                // 微信
                payment = 2;
                break;
            case 2:
                // 现金
                payment = 6;
                break;
        }
        if (onPayListener != null)
            onPayListener.onCheckDiscount(isNetAvailable, discountCodeValue, payment);
    }

    /**
     * @param payState 必传，支付状态   1：支付 2：退款
     * @param payType  必传，支付类型  1：支付宝 2：微信 6：现金
     * @param payMoney 必传，支付金额
     */
    public static Intent newIntent(Context context, int payState, int payType, double payMoney, OnPayListener onPayListener) {
        Intent intent = new Intent(context, PayDialogActivity.class);
        intent.putExtra(PAY_STATE, payState);
        intent.putExtra(PAY_TYPE, payType);
        intent.putExtra(PAY_MONEY, payMoney);
        PayDialogActivity.onPayListener = onPayListener;
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

}
