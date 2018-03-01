package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.OrderDesAdapter;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.db.Account;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.PrintHelper;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 描述:对账弹框
 *
 * @author zhangrq
 *         2017/1/7 13:16
 */

public class AcountCheckDialogActivity extends AppCompatActivity {
    public static final String TYPE = "type";
    public static final String UID = "uid";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    private Button mBackButton, mHistoryButton, mRefreshButton, mSureButton;
    private TextView mStoreNameTextView, mPosNumberTextView, mCashierNumberTextView,
            mCashierTextView, mReconciliationDateTextView, mStarTimeTextView, mEndTimeTextView,
            mFirstTimeTextView, mLastTimeTextView, mZfbReceiptNumberTextView, mZfbReceivableAmountTextView,
            mZfbRefundNumberTextView, mZfbRefundAmountTextView, mWxReceiptNumberTextView,
            mWxReceivableAmountTextView, mWxRefundNumberTextView, mWxRefundAmountTextView, mCashReceiptNumberTextView,
            mCashReceivableAmountTextView, mCashRefundNumberTextView, mCashRefundAmountTextView,
            mCashTotalTextView, mAllTotalTextView, mTotalNumberTextView;
    private OrderDesAdapter adapter;
    private int type;
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
    // 开始时间
    private long startTime = -1;
    private User loginUser;
    // 以下三个是从上个界面传递过来的值 type=2时用到
    private String mUserUid;
    private long mStartTime;
    private long mEndTime;
    // 用于打印用
    private String mPrintNames;
    private String mPrintStartTime;
    private String mPrintAccountTime;
    private PrintHelper printHelper = new PrintHelper();// 初始化打印

    /**
     * @param type      1:收银对账 2：对账详情
     * @param uid       用户id 当type=2时必传
     * @param startTime 开始时间 当type=2时必传
     * @param endTime   结束时间 当type=2时必传
     */
    public static Intent newIntent(Context context, int type, String uid, long startTime, long endTime) {
        Intent intent = new Intent(context, AcountCheckDialogActivity.class);
        intent.putExtra(TYPE, type);
        intent.putExtra(UID, uid);
        intent.putExtra(START_TIME, startTime);
        intent.putExtra(END_TIME, endTime);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        Intent intent = getIntent();
        type = intent.getIntExtra(TYPE, 1);
        mUserUid = intent.getStringExtra(UID);
        mStartTime = intent.getLongExtra(START_TIME, System.currentTimeMillis());
        mEndTime = intent.getLongExtra(END_TIME, System.currentTimeMillis());
        setContentView(type == 1 ? R.layout.dialog_acount_check : R.layout.dialog_bill_detail);
    }

    protected void findView() {
        mBackButton = findViewById(R.id.id_back);// 返回
        mStoreNameTextView = findViewById(R.id.id_store_name);// 分店
        mPosNumberTextView = findViewById(R.id.id_pos_number);// 收银机号
        mCashierNumberTextView = findViewById(R.id.id_cashier_number);// 收银员工号
        mCashierTextView = findViewById(R.id.id_cashier);// 收银员
        mReconciliationDateTextView = findViewById(R.id.id_reconciliation_date);// 对账日期
        mStarTimeTextView = findViewById(R.id.id_start_time);// 开始时间
        mEndTimeTextView = findViewById(R.id.id_end_time);// 结束时间
        mFirstTimeTextView = findViewById(R.id.id_first_time);// 首笔时间
        mLastTimeTextView = findViewById(R.id.id_last_time);// 末笔时间
        mZfbReceiptNumberTextView = findViewById(R.id.id_zfb_receipt_number);// 支付宝收款笔数
        mZfbReceivableAmountTextView = findViewById(R.id.id_zfb_receivable_amount);// 支付宝收款金额
        mZfbRefundNumberTextView = findViewById(R.id.id_zfb_refund_number);// 支付宝退款笔数
        mZfbRefundAmountTextView = findViewById(R.id.id_zfb_refund_amount);// 支付宝退款金额

        mWxReceiptNumberTextView = findViewById(R.id.id_wx_receipt_number);// 微信收款笔数
        mWxReceivableAmountTextView = findViewById(R.id.id_wx_receivable_amount);// 微信收款金额
        mWxRefundNumberTextView = findViewById(R.id.id_wx_refund_number);// 微信退款笔数
        mWxRefundAmountTextView = findViewById(R.id.id_wx_refund_amount);// 微信退款金额

        mCashReceiptNumberTextView = findViewById(R.id.id_cash_receipt_number);// 现金收款笔数
        mCashReceivableAmountTextView = findViewById(R.id.id_cash_receivable_amount);// 现金收款金额
        mCashRefundNumberTextView = findViewById(R.id.id_cash_refund_number);// 现金退款笔数
        mCashRefundAmountTextView = findViewById(R.id.id_cash_refund_amount);// 现金退款金额

        mCashTotalTextView = findViewById(R.id.id_cash_total);// 现金总额
        mAllTotalTextView = findViewById(R.id.id_all_total);// 全部总额
        mTotalNumberTextView = findViewById(R.id.id_total_number);// 总笔数

        if (type == 1) {
            // 1:收银对账
            mHistoryButton = findViewById(R.id.id_history);// 历史对账单
            mRefreshButton = findViewById(R.id.id_refresh);// 刷新
            mSureButton = findViewById(R.id.id_sure);// 确认对账
        } else {
            // 2：对账详情
            RecyclerView mRecyclerView = findViewById(R.id.id_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(AcountCheckDialogActivity.this));
            adapter = new OrderDesAdapter(AcountCheckDialogActivity.this, R.layout.adapter_bill_list_item);
            mRecyclerView.setAdapter(adapter);
        }


    }

    protected void setListener() {
        // 返回
        mBackButton.setOnClickListener(v -> finish());
        if (type == 1) {
            // 1:收银对账
            // 历史对账单
            mHistoryButton.setOnClickListener(v -> startActivity(HistoryStatementDialogActivity.newIntent(AcountCheckDialogActivity.this)));
            // 刷新
            mRefreshButton.setOnClickListener(v -> {
                // 所有价格归原
                // 消费总金额
                total = 0;
                // 退款总金额
                refundTotal = 0;
                // 支付宝订单数
                zfbOrderNumber = 0;
                // 支付宝退款订单数
                zfbRefundNumber = 0;
                // 支付宝退款金额
                zfbRefundTotal = 0;
                // 支付宝收款金额
                zfbTotal = 0;
                // 微信订单数
                wxOrderNumber = 0;
                // 微信收款金额
                wxTotal = 0;
                // 微信退款订单数
                wxRefundNumber = 0;
                // 微信退款金额
                wxRefundTotal = 0;
                // 现金订单数
                cashOrderNumber = 0;
                // 现金总金额
                cashTotal = 0;
                // 现金退款订单数
                cashRefundNumber = 0;
                // 现金退款金额
                cashRefundTotal = 0;
                processLogic();
            });
            // 确认对账
            mSureButton.setOnClickListener(v -> account());
        }

    }

    protected void processLogic() {

        // 获取用户信息
        loginUser = ZhaoLinApplication.getInstance().getLoginUser();
        if (loginUser == null) return;
        // 门店
        String store_title = ZLUtils.getStoreTitle();
        mStoreNameTextView.setText(String.format("分店：%s", store_title));
        // 收银机
        mPosNumberTextView.setText(String.format("收银机号：%s", ZLUtils.getDevicesNum()));
        mReconciliationDateTextView.setText(String.format("对账日期：%s", DateUtils.getYearMonthDay(type == 1 ? System.currentTimeMillis() : mEndTime * 1000, "yyyy/MM/dd")));
        // 获取用户信息
        DaoUtils.getUserByUserId(AcountCheckDialogActivity.this, type == 1 ? loginUser.getUid() : mUserUid, new DaoUtils.OnDaoResultListener<User>() {
            @Override
            public void onSuccess(User bean) {
                setCashierInfo(bean.getUid(), bean.getName());
            }

            @Override
            public void onFailed() {
                setCashierInfo(type == 1 ? loginUser.getUid() : "0", type == 1 ? loginUser.getName() : "");
            }
        });

        if (type == 1) {
            // 对账
            // 获取最后对账时间
            DaoUtils.getLastAccountTime(AcountCheckDialogActivity.this, bean -> {
                startTime = bean;
                setStartAndEndTime(bean, System.currentTimeMillis() / 1000);
            });
        } else {
            // 详情
            setStartAndEndTime(mStartTime, mEndTime);
        }

    }

    /**
     * 对账
     */
    private void account() {
        if (loginUser == null) return;
        long starttime = startTime == -1 ? System.currentTimeMillis() / 1000 : startTime;// 开始时间
        long endtime = System.currentTimeMillis() / 1000;// 结束时间，既也是此表的增加时间
        String uid = loginUser.getUid();// 操作人员id
        double cash_price = StringUtil.sub(cashTotal + "", cashRefundTotal + "");// 现金总额
        double all_price = StringUtil.sub(total + "", refundTotal + "");// 全部总额
        int order_num = zfbOrderNumber + wxOrderNumber + cashOrderNumber + zfbRefundNumber + wxRefundNumber + cashRefundNumber;// 订单数
        Account account = new Account(starttime, endtime, uid, cash_price, all_price, order_num);
        DaoUtils.insertOrReplaceAccount(account, new DaoUtils.OnDaoResultListener<String>() {
            @Override
            public void onSuccess(String bean) {
                // 对账成功,打印小票
                printHelper.printAccountCheck(AcountCheckDialogActivity.this, true, getPrintValues());
                // 弹框
                showSuccessDialog();
            }

            @Override
            public void onFailed() {
                ToastManager.showShortToast(AcountCheckDialogActivity.this, "对账失败");
            }
        });
    }

    /**
     * 设置收银员工号和名字
     *
     * @param id   收银员工号
     * @param name 名字
     */
    private void setCashierInfo(String id, String name) {
        this.mPrintNames = name;
        mCashierNumberTextView.setText(String.format(Locale.CHINESE, "收银员工号：%s", id));
        mCashierTextView.setText(String.format("收银员：%s", name));
    }

    /**
     * 设置开始时间和结束时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    private void setStartAndEndTime(long startTime, long endTime) {
        this.mPrintStartTime = DateUtils.getYearMonthDay(startTime * 1000, "yyyy/MM/dd - HH:mm:ss");
        this.mPrintAccountTime = DateUtils.getYearMonthDay(endTime * 1000, "yyyy/MM/dd - HH:mm:ss");
        mStarTimeTextView.setText(String.format("开始时间：%s", DateUtils.getYearMonthDay(startTime * 1000, "yyyy/MM/dd HH:mm")));
        mEndTimeTextView.setText(String.format("结束时间：%s", DateUtils.getYearMonthDay(endTime * 1000, "yyyy/MM/dd HH:mm")));
        getOrderList(startTime, endTime);
    }

    /**
     * 获取对账时订单列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    private void getOrderList(long startTime, long endTime) {
        // 获取操作员订单列表
        DaoUtils.getOrderListByTime(AcountCheckDialogActivity.this, startTime, endTime, new DaoUtils.OnDaoResultListListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if (type == 2)
                    adapter.clearAndAddList(list);

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

            @Override
            public void onFailed() {
                mFirstTimeTextView.setText(String.format("首笔时间：%s", "未产生首笔订单"));
                mLastTimeTextView.setText(String.format("末笔时间：%s", "未产生末笔订单"));
            }
        });
    }

    /**
     * 打印数据
     *
     * @return
     */
    private List<String> getPrintValues() {
        // 打印
        List<String> list = new ArrayList<>();
        list.add(mPrintAccountTime);
        list.add(mPrintStartTime);
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

    /**
     * 展示对账成功
     */
    private void showSuccessDialog() {
        mFirstTimeTextView.postDelayed(() -> ZLDialogUtil.showShiftSuccessDialog(AcountCheckDialogActivity.this, this::finish), 500);

    }
}
