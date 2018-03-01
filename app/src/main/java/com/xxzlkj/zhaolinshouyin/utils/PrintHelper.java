package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.BitmapUtils;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.PrintAdapter;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.model.OrderItemBean;
import com.zrq.spanbuilder.Spans;

import java.util.List;

import aclasdriver.AclasReceiptPrinter;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/20 11:04
 */
public class PrintHelper {
    private AclasReceiptPrinter mPrinter = null;
    private AclasReceiptPrinter.LabelStatus mLastPrinterStatus;

    /**
     * 打印商品列表
     *
     * @param orderItemBean 封装好的，订单信息
     */
    public void printGoodsList(Activity activity, OrderItemBean orderItemBean) {
        final ViewGroup rootView = (ViewGroup) View.inflate(activity, R.layout.view_print_content, null);
        TextView tv_store_name = rootView.findViewById(R.id.tv_store_name);// 店名
        TextView tv_add_time = rootView.findViewById(R.id.tv_add_time);// 生成时间
        TextView tv_order_id = rootView.findViewById(R.id.tv_order_id);// 订单id
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);// 内容
        TextView tv_all_weight = rootView.findViewById(R.id.tv_all_weight);// 数量
        TextView tv_all_num = rootView.findViewById(R.id.tv_all_num);// 件数
        TextView tv_discount_price = rootView.findViewById(R.id.tv_discount_price);// 优惠金额
        TextView tv_all_prices = rootView.findViewById(R.id.tv_all_prices);// 合计
        ViewGroup vg_all_price_layout = rootView.findViewById(R.id.vg_all_price_layout);// 实付金额布局
        TextView tv_all_price = rootView.findViewById(R.id.tv_all_price);// 实付金额
        ViewGroup vg_pay_payment_layout = rootView.findViewById(R.id.vg_pay_payment_layout);// 付款金额布局
        TextView tv_pay_payment = rootView.findViewById(R.id.tv_pay_payment);// 支付类型
        TextView tv_pay_price = rootView.findViewById(R.id.tv_pay_price);// 付款金额
        ViewGroup vg_zhaoling_price_layout = rootView.findViewById(R.id.vg_zhaoling_price_layout);// 找零金额布局
        TextView tv_zhaoling_price = rootView.findViewById(R.id.tv_zhaoling_price);// 找零金额
        TextView tv_hint = rootView.findViewById(R.id.tv_hint);// 底部提示
        // 初始化控件
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        PrintAdapter printAdapter = new PrintAdapter(activity, R.layout.item_print);
        recyclerView.setAdapter(printAdapter);
        // 获取值
        Order order = orderItemBean.getOrder();
        List<OrderDetail> orderDetails = orderItemBean.getOrderDetails();
        int state = order.getState();//  流水类型： 1：消费 2：退款
        // 设置值
        // --设置头
        tv_store_name.setText(ZLUtils.getStoreTitle());// 店名
        tv_add_time.setText(DateUtils.getYearMonthDay(order.getAddtime() * 1000, "yyyy.MM.dd - HH:mm:ss"));// 生成时间(2017.12.07 - 09:32:08)
        tv_order_id.setText(String.format("NO.%s", order.getOrder_id()));// 订单id
        // --设置内容
        printAdapter.setState(state);// 流水类型： 1：消费 2：退款
        printAdapter.clearAndAddList(orderItemBean.getOrderDetails());// 商品列表
        // --设置底部信息
        double allNum = 0;
        double allWeight = 0;
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getStandard() == 1) {//  标品:1 非标:2
                // 标品，数量加num
                allNum += orderDetail.getNum();
            } else {
                // 非标品，数量加1
                allNum += 1;
            }
            allWeight += orderDetail.getNum();
        }
        tv_all_weight.setText(getItemSpan("数量：", StringUtil.saveThreeDecimal(allWeight)));// 数量
        tv_all_num.setText(getItemSpan("件数：", StringUtil.saveInt(allNum)));// 件数

        if (state == 1) {
            // 消费，用的默认xml的布局样式
            double discount_pre_price = order.getDiscount_pre_price();// 商品未优惠前的价格
            tv_discount_price.setText(getItemSpan("优惠金额：", StringUtil.saveTwoDecimal(Math.abs(discount_pre_price - order.getPrice() - order.getMuoling_price()))));
            tv_all_prices.setText(getItemSpan("合计：", StringUtil.saveTwoDecimal(discount_pre_price)));// 合计
            tv_all_price.setText(StringUtil.saveTwoDecimal(order.getPrice()));// 实付金额
            // 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
            String paymentStr = "";
            switch (order.getPayment()) {
                case 1:
                    paymentStr = "支付宝";
                    break;
                case 2:
                    paymentStr = "微信";
                    break;
                case 6:
                    paymentStr = "现金";
                    break;
            }
            tv_pay_payment.setText(String.format("付款：%s", paymentStr));// 支付类型
            tv_pay_price.setText(StringUtil.saveTwoDecimal(order.getPay_price()));// 付款金额
            tv_zhaoling_price.setText(StringUtil.saveTwoDecimal(order.getZhaoling_price()));// 找零金额
        } else {
            // 退款
            tv_discount_price.setText(String.format("退款金额：-%s", StringUtil.saveTwoDecimal(order.getPrice())));// 退款金额
            tv_all_prices.setVisibility(View.GONE);// 原价金额隐藏
            vg_all_price_layout.setVisibility(View.GONE);// 合计栏目隐藏
            vg_pay_payment_layout.setVisibility(View.GONE);// 付款栏目隐藏
            vg_zhaoling_price_layout.setVisibility(View.GONE);// 找零栏目隐藏
            tv_hint.setText("请妥善保管退货小票,如有疑问请到店咨询");
        }

        // 打印布局
        printBitmapByView(activity, rootView);
    }

    /**
     * 打印对账单
     *
     * @param isAccount true:对账 false:统计
     * @param values    按照顺序依次加入对应的值(必须一一对应)
     */
    public void printAccountCheck(Activity activity, boolean isAccount, List<String> values) {
        final ViewGroup rootView = (ViewGroup) View.inflate(activity, R.layout.view_print_account_check, null);
        TextView mTimeTextView1 = rootView.findViewById(R.id.id_time_1);
        TextView mTimeTextView2 = rootView.findViewById(R.id.id_time_2);
        TextView mTitleTextView1 = rootView.findViewById(R.id.id_title_1);
        TextView mTitleTextView2 = rootView.findViewById(R.id.id_title_2);
        TextView mBottomTextView = rootView.findViewById(R.id.id_bottom_tv);
        TextView mTotalTipTextView = rootView.findViewById(R.id.id_account_total_tip);
        mTimeTextView1.setText(isAccount ? "对账时间：" : "开始时间：");
        mTimeTextView2.setText(isAccount ? "开始时间：" : "结束时间：");
        mTitleTextView1.setText(isAccount ? "对账详情：" : "订单小计：");
        mTitleTextView2.setText(isAccount ? "对账合计：" : "订单合计：");
        mTotalTipTextView.setText(isAccount ? "对账总额" : "订单合计");
        mBottomTextView.setVisibility(isAccount ? View.VISIBLE : View.GONE);
        TextView[] textViews = new TextView[18];
        TextView mStoreNameTextView = rootView.findViewById(R.id.tv_store_name);// 店名
        textViews[0] = rootView.findViewById(R.id.id_account_check_time);// 对账时间
        textViews[1] = rootView.findViewById(R.id.id_start_time);// 开始时间
        textViews[2] = rootView.findViewById(R.id.id_cashier);// 收银员
        // 支付宝相关
        textViews[3] = rootView.findViewById(R.id.id_zfb_receipt_number);// 支付宝收款笔数
        textViews[4] = rootView.findViewById(R.id.id_zfb_receivable_amount);// 支付宝收款金额
        textViews[5] = rootView.findViewById(R.id.id_zfb_refund_number);// 支付宝退款笔数
        textViews[6] = rootView.findViewById(R.id.id_zfb_refund_amount);// 支付宝退款金额

        // 微信相关
        textViews[7] = rootView.findViewById(R.id.id_wx_receipt_number);// 微信收款笔数
        textViews[8] = rootView.findViewById(R.id.id_wx_receivable_amount);// 微信收款金额
        textViews[9] = rootView.findViewById(R.id.id_wx_refund_number);// 微信退款笔数
        textViews[10] = rootView.findViewById(R.id.id_wx_refund_amount);// 微信退款金额

        // 现金相关
        textViews[11] = rootView.findViewById(R.id.id_cash_receipt_number);// 现金收款笔数
        textViews[12] = rootView.findViewById(R.id.id_cash_receivable_amount);// 现金收款金额
        textViews[13] = rootView.findViewById(R.id.id_cash_refund_number);// 现金退款笔数
        textViews[14] = rootView.findViewById(R.id.id_cash_refund_amount);// 现金退款金额
        // 总计相关
        textViews[15] = rootView.findViewById(R.id.id_cash_total);// 现金总额
        textViews[16] = rootView.findViewById(R.id.id_account_total);// 对账总额
        textViews[17] = rootView.findViewById(R.id.id_total_number);// 总笔数

        // 设置数据
        mStoreNameTextView.setText(ZLUtils.getStoreTitle());
        for (int i = 0; i < values.size(); i++) {
            textViews[i].setText(values.get(i));
        }
        // 打印布局
        printBitmapByView(activity, rootView);
    }

    /**
     * 打印布局
     */
    private void printBitmapByView(Activity activity, View rootView) {
        // 初始化宽高
        rootView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        rootView.layout(0, 0, rootView.getMeasuredWidth(), rootView.getMeasuredHeight());

        printBitmap(activity, BitmapUtils.getBitmapByView(rootView));
    }

    /**
     * 打印图片
     *
     * @param bitmap 要打印的图片
     */
    private void printBitmap(Activity activity, Bitmap bitmap) {
        new Thread() {
            public void run() {
                if (mPrinter != null) {
                    synchronized (AclasReceiptPrinter.class) {
                        mPrinter.EnableEpsonMode(true);
                        String errorHintStr = null;
//                        isBusy iscapout isok ispapernottaken ispaperout
                        if (mLastPrinterStatus == null)
                            errorHintStr = null;
                        else if (mLastPrinterStatus.isBusy)
                            errorHintStr = "打印忙";
//                        else if (mLastPrinterStatus.isPaperNotTaken)
//                            errorHintStr = "纸未取出";
                        else if (mLastPrinterStatus.isCapOut)
                            errorHintStr = "开盖";
                        else if (mLastPrinterStatus.isPaperOut)
                            errorHintStr = "缺纸";

                        if (TextUtils.isEmpty(errorHintStr)) {
                            // 没有错误提示，正常打印
                            mPrinter.PrintReceipt(bitmap);
                        } else {
                            // 有错误提示，提示
                            String finalErrorHintStr = errorHintStr;
                            activity.runOnUiThread(() -> ToastManager.showShortToast(activity, finalErrorHintStr));
                        }
                    }
                }
            }
        }.start();
    }

    private CharSequence getItemSpan(String title, String value) {
        if (title == null) title = "";
        if (value == null) value = "";
        return Spans.builder().text(title).size(20).text(value).size(22).build();
    }

    public void onResume() {
        // 归原
        onDestroy();
        mPrinter = new AclasReceiptPrinter();
        if (mPrinter.Open(null) != 0) {
            mPrinter = null;
        } else {
            mPrinter.SetReceiptType(AclasReceiptPrinter.LabelStatus.LABELPRINTER_CLIP_SERIES);
            mPrinter.SetStatusChangedListener(status -> mLastPrinterStatus = status);
        }
    }

    public void onDestroy() {
        if (mPrinter != null) {
            mPrinter.Close();
            mPrinter = null;
        }
    }
}
