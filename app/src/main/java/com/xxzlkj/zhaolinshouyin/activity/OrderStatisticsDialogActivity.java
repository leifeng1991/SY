package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.utils.PrintHelper;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 描述:订单统计弹框
 *
 * @author zhangrq
 *         2017/1/7 13:16
 */

public class OrderStatisticsDialogActivity extends AppCompatActivity {
    private Button mBackButton, mSureButton;
    private TextView mStoreNameTextView, mPosNumberTextView, mCashierTextView, mStarTimeTextView, mEndTimeTextView,
            mFirstTimeTextView, mLastTimeTextView, mZfbReceiptNumberTextView, mZfbReceivableAmountTextView,
            mZfbRefundNumberTextView, mZfbRefundAmountTextView, mWxReceiptNumberTextView,
            mWxReceivableAmountTextView, mWxRefundNumberTextView, mWxRefundAmountTextView, mCashReceiptNumberTextView,
            mCashReceivableAmountTextView, mCashRefundNumberTextView, mCashRefundAmountTextView,
            mCashTotalTextView, mAllTotalTextView, mTotalNumberTextView;
    // 消费总金额
    private double total = 0;
    // 退款总金额
    private double refundTotal = 0;
    // 支付宝订单数
    private int zfbOrderNumber = 0;
    // 支付宝退款订单数
    private int zfbRefundNumber = 0;
    // 支付宝退款金额
    private double zfbRefundTotal = 0;
    // 支付宝收款金额
    private double zfbTotal = 0;
    // 微信订单数
    private int wxOrderNumber = 0;
    // 微信收款金额
    private double wxTotal = 0;
    // 微信退款订单数
    private int wxRefundNumber = 0;
    // 微信退款金额
    private double wxRefundTotal = 0;
    // 现金订单数
    private int cashOrderNumber = 0;
    // 现金总金额
    private double cashTotal = 0;
    // 现金退款订单数
    private int cashRefundNumber = 0;
    // 现金退款金额
    private double cashRefundTotal = 0;
    private User loginUser;
    // 用于打印用
    private String mPrintNames;
    private String mPrintStartTime;
    private String mPrintEndTime;
    private PrintHelper printHelper = new PrintHelper();// 初始化打印
    private static OrderListActivity orderListActivity;
    private long mStartTime = -1;
    private long mEndTime = -1;

    /**
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @return
     */
    public static Intent newIntent(Context context, OrderListActivity activity, long starTime, long endTime) {
        orderListActivity = activity;
        Intent intent = new Intent(context, OrderStatisticsDialogActivity.class);
        intent.putExtra("startTime", starTime);
        intent.putExtra("endTime", endTime);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadViewLayout();
        findView();
        setListener();
        processLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        printHelper.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printHelper.onDestroy();
    }

    protected void loadViewLayout() {
        setContentView(R.layout.dialog_order_statistics);
    }

    protected void findView() {
        mBackButton = findViewById(R.id.id_back);// 返回
        mStoreNameTextView = findViewById(R.id.id_store_name);// 分店
        mPosNumberTextView = findViewById(R.id.id_pos_number);// 收银机号
        mCashierTextView = findViewById(R.id.id_cashier);// 收银员
        mStarTimeTextView = findViewById(R.id.id_start_time);// 开始时间
        mEndTimeTextView = findViewById(R.id.id_end_time);// 结束时间
        mFirstTimeTextView = findViewById(R.id.id_first_time);// 首笔时间
        mLastTimeTextView = findViewById(R.id.id_last_time);// 末笔时间
        // 支付宝
        mZfbReceiptNumberTextView = findViewById(R.id.id_zfb_receipt_number);// 支付宝收款笔数
        mZfbReceivableAmountTextView = findViewById(R.id.id_zfb_receivable_amount);// 支付宝收款金额
        mZfbRefundNumberTextView = findViewById(R.id.id_zfb_refund_number);// 支付宝退款笔数
        mZfbRefundAmountTextView = findViewById(R.id.id_zfb_refund_amount);// 支付宝退款金额
        // 微信
        mWxReceiptNumberTextView = findViewById(R.id.id_wx_receipt_number);// 微信收款笔数
        mWxReceivableAmountTextView = findViewById(R.id.id_wx_receivable_amount);// 微信收款金额
        mWxRefundNumberTextView = findViewById(R.id.id_wx_refund_number);// 微信退款笔数
        mWxRefundAmountTextView = findViewById(R.id.id_wx_refund_amount);// 微信退款金额
        // 现金
        mCashReceiptNumberTextView = findViewById(R.id.id_cash_receipt_number);// 现金收款笔数
        mCashReceivableAmountTextView = findViewById(R.id.id_cash_receivable_amount);// 现金收款金额
        mCashRefundNumberTextView = findViewById(R.id.id_cash_refund_number);// 现金退款笔数
        mCashRefundAmountTextView = findViewById(R.id.id_cash_refund_amount);// 现金退款金额
        // 总计
        mCashTotalTextView = findViewById(R.id.id_cash_total);// 现金总额
        mAllTotalTextView = findViewById(R.id.id_all_total);// 全部总额
        mTotalNumberTextView = findViewById(R.id.id_total_number);// 总笔数
        mSureButton = findViewById(R.id.id_sure);// 打印
    }

    protected void setListener() {
        // 返回
        mBackButton.setOnClickListener(v -> finish());
        // 打印
        mSureButton.setOnClickListener(v -> {
            // 打印小票
            printHelper.printAccountCheck(OrderStatisticsDialogActivity.this, false, getPrintValues());
        });

    }

    protected void processLogic() {
        mStartTime = getIntent().getLongExtra("startTime", -1);
        mEndTime = getIntent().getLongExtra("endTime", -1);
        if (mStartTime != -1 && mEndTime != -1) {
            mPrintEndTime = DateUtils.getYearMonthDay(mEndTime * 1000, "yyyy/MM/dd HH:mm");
            mPrintStartTime = DateUtils.getYearMonthDay(mStartTime * 1000, "yyyy/MM/dd HH:mm");
            mEndTimeTextView.setText(String.format("结束时间：%s", mPrintEndTime));
            mStarTimeTextView.setText(String.format("开始时间：%s", mPrintStartTime));
        }
        // 门店
        String store_title = ZLUtils.getStoreTitle();
        mStoreNameTextView.setText(String.format("分店：%s", store_title));
        // 收银机
        mPosNumberTextView.setText(String.format("收银机号：%s", ZLUtils.getDevicesNum()));
        setCashierInfo(orderListActivity.getNames());
        setData(orderListActivity.getOrderList());
    }

    /**
     * 设置收银员
     *
     * @param name 名字
     */
    private void setCashierInfo(String name) {
        this.mPrintNames = name;
        mCashierTextView.setText(String.format("收银员：%s", name));
    }

    /**
     * 获取对账时订单列表
     */
    private void setData(List<Order> list) {
        for (int i = 0; i < list.size(); i++) {
            Order order = list.get(i);
            // 支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
            switch (order.getPayment()) {
                case 1:// 支付宝
                    // 流水类型： 1：消费 2：退款
                    if (order.getState() == 2) {
                        // 退款
                        zfbRefundTotal = StringUtil.add(zfbRefundTotal + "", order.getPrice() + "");
                        zfbRefundNumber++;
                    } else {
                        // 消费
                        zfbTotal = StringUtil.add(zfbTotal + "", order.getPrice() + "");
                        zfbOrderNumber++;
                    }
                    break;
                case 2:// 微信
                    // 流水类型： 1：消费 2：退款
                    if (order.getState() == 2) {
                        // 退款
                        wxRefundTotal = StringUtil.add(wxRefundTotal + "", order.getPrice() + "");
                        wxRefundNumber++;
                    } else {
                        // 消费
                        wxTotal = StringUtil.add(wxTotal + "", order.getPrice() + "");
                        wxOrderNumber++;
                    }
                    break;
                case 6:// 现金支付
                    // 流水类型： 1：消费 2：退款
                    if (order.getState() == 2) {
                        // 退款
                        cashRefundTotal = StringUtil.add(cashRefundTotal + "", order.getPrice() + "");
                        cashRefundNumber++;
                    } else {
                        // 消费
                        cashTotal = StringUtil.add(cashTotal + "", order.getPrice() + "");
                        cashOrderNumber++;
                    }
                    break;

            }

            if (order.getState() == 2) {
                // 退款
                refundTotal = StringUtil.add(refundTotal + "", order.getPrice() + "");
            } else {
                // 消费
                total = StringUtil.add(total + "", order.getPrice() + "");
            }

        }

        if (list.size() > 0) {
            // 有订单
            // 首笔订单
            Order firstOrder = list.get(list.size() - 1);
            if (firstOrder != null)
                mFirstTimeTextView.setText(String.format("首笔时间：%s", DateUtils.getYearMonthDay(firstOrder.getAddtime() * 1000, "yyyy/MM/dd HH:mm")));
            else
                mFirstTimeTextView.setText(String.format("首笔时间：%s", "未产生首笔订单"));
            // 末笔订单
            Order endOrder = list.get(0);
            if (endOrder != null)
                mLastTimeTextView.setText(String.format("末笔时间：%s", DateUtils.getYearMonthDay(endOrder.getAddtime() * 1000, "yyyy/MM/dd HH:mm")));
            else
                mLastTimeTextView.setText(String.format("末笔时间：%s", "未产生末笔订单"));
        } else {
            // 无订单
            mFirstTimeTextView.setText(String.format("首笔时间：%s", "未产生首笔订单"));
            mLastTimeTextView.setText(String.format("末笔时间：%s", "未产生末笔订单"));
        }

        // 支付宝
        mZfbReceiptNumberTextView.setText(String.format(Locale.CHINESE, "%d", zfbOrderNumber));
        mZfbReceivableAmountTextView.setText(StringUtil.saveTwoDecimal(zfbTotal));
        mZfbRefundNumberTextView.setText(String.format(Locale.CHINESE, "%d", zfbRefundNumber));
        mZfbRefundAmountTextView.setText(StringUtil.saveTwoDecimal(zfbRefundTotal));

        // 微信
        mWxReceiptNumberTextView.setText(String.format(Locale.CHINESE, "%d", wxOrderNumber));
        mWxReceivableAmountTextView.setText(StringUtil.saveTwoDecimal(wxTotal));
        mWxRefundNumberTextView.setText(String.format(Locale.CHINESE, "%d", wxRefundNumber));
        mWxRefundAmountTextView.setText(StringUtil.saveTwoDecimal(wxRefundTotal));

        // 现金
        mCashReceiptNumberTextView.setText(String.format(Locale.CHINESE, "%d", cashOrderNumber));
        mCashReceivableAmountTextView.setText(StringUtil.saveTwoDecimal(cashTotal));
        mCashRefundNumberTextView.setText(String.format(Locale.CHINESE, "%d", cashRefundNumber));
        mCashRefundAmountTextView.setText(StringUtil.saveTwoDecimal(cashRefundTotal));
        // 现金总额
        mCashTotalTextView.setText(StringUtil.saveTwoDecimal(StringUtil.sub(cashTotal + "", cashRefundTotal + "")));
        // 对账总额
        mAllTotalTextView.setText(StringUtil.saveTwoDecimal(StringUtil.sub(total + "", refundTotal + "")));
        // 总笔数
        int totalNumber = zfbOrderNumber + wxOrderNumber + cashOrderNumber + zfbRefundNumber + wxRefundNumber + cashRefundNumber;
        mTotalNumberTextView.setText(String.format(Locale.CHINESE, "%d", totalNumber));
    }

    /**
     * 打印数据
     *
     * @return
     */
    private List<String> getPrintValues() {
        // 打印
        List<String> list = new ArrayList<>();
        list.add(mPrintStartTime);
        list.add(mPrintEndTime);
        list.add(mPrintNames);
        // 支付宝相关
        list.add(mZfbReceiptNumberTextView.getText().toString());
        list.add(mZfbReceivableAmountTextView.getText().toString());
        list.add(mZfbRefundNumberTextView.getText().toString());
        list.add(mZfbRefundAmountTextView.getText().toString());
        // 微信相关
        list.add(mWxReceiptNumberTextView.getText().toString());
        list.add(mWxReceivableAmountTextView.getText().toString());
        list.add(mWxRefundNumberTextView.getText().toString());
        list.add(mWxRefundAmountTextView.getText().toString());
        // 现金相关
        list.add(mCashReceiptNumberTextView.getText().toString());
        list.add(mCashReceivableAmountTextView.getText().toString());
        list.add(mCashRefundNumberTextView.getText().toString());
        list.add(mCashRefundAmountTextView.getText().toString());

        list.add(mCashTotalTextView.getText().toString());
        list.add(mAllTotalTextView.getText().toString());
        list.add(mTotalNumberTextView.getText().toString());
        return list;
    }

}
