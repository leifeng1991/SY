package com.xxzlkj.zhaolinshouyin.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.OrderDesActivity;
import com.xxzlkj.zhaolinshouyin.model.OnLineOrderListBean;
import com.zrq.spanbuilder.Spans;

import java.util.Locale;

/**
 * 描述:线上订单
 *
 * @author leifeng
 *         2018/1/18 16:17
 */


public class OnLineOrderListAdapter extends BaseAdapter<OnLineOrderListBean.DataBean> {
    private Activity activity;

    public OnLineOrderListAdapter(Context context, Activity activity, int itemId) {
        super(context, itemId);
        this.activity = activity;
    }

    @Override
    public void convert(BaseViewHolder holder, int position, OnLineOrderListBean.DataBean itemBean) {
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mOrderNumberTextView = holder.getView(R.id.id_order_number);// 订单号
        TextView mWaterTypeTextView = holder.getView(R.id.id_water_type);// 流水类型
        TextView mOrderTypeTextView = holder.getView(R.id.id_order_type);// 订单类型
        TextView mPayTypeTextView = holder.getView(R.id.id_pay_type);// 支付方式
        TextView mOrderPriceTextView = holder.getView(R.id.id_order_price);// 订单金额
        TextView mAmountPaidTextView = holder.getView(R.id.id_amount_paid);// 实付金额
        TextView mOrderStateTextView = holder.getView(R.id.id_sync_type_order);// 订单状态
        TextView mPsTypeTextView = holder.getView(R.id.id_ps_type);// 配送方式
        View mPsTypeLineView = holder.getView(R.id.id_ps_type_line);
        mPsTypeTextView.setVisibility(View.VISIBLE);
        mPsTypeLineView.setVisibility(View.VISIBLE);
        TextView mOrderTimeTextView = holder.getView(R.id.id_order_time);// 下单时间
        // 设置数据
        holder.itemView.setBackgroundColor((position + 1) % 2 == 0 ? 0xffF7F8FC : 0xffffffff);
        mLineNumberTextView.setText(String.format(Locale.CHINA, "%d", position + 1));
        mOrderNumberTextView.setText(Spans.builder().text(itemBean.getId()).color(0xff0074FC).underLine().color(0xff0074FC).build());
        // 合计
        double orderPrice = NumberFormatUtils.toDouble(itemBean.getPrice()) + NumberFormatUtils.toDouble(itemBean.getCoupon_price());
        mOrderPriceTextView.setText(StringUtil.saveTwoDecimal(orderPrice));
        mAmountPaidTextView.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));
        mOrderTimeTextView.setText(DateUtils.getYearMonthDayHourMinuteSeconds(NumberFormatUtils.toLong(itemBean.getBuytime()) * 1000));
        // 全部则不传 2待发货 3配送中（待确认，状态跑到了已完成）  1待付款 6订单退款（细分分为3种：退款中，系统退款，已退款） 4已完成（细分分为2种：配送中列表中的待确认，已完成） 5已取消
        String state = itemBean.getState();
        mWaterTypeTextView.setText("6".equals(state) ? "退款" : "消费");
        if ("2".equals(state)) {
            // 待发货
            mOrderStateTextView.setText("待发货");
        } else if ("3".equals(state)) {
            // 配送中（待确认状态，跑到了已完成状态）
            mOrderStateTextView.setText("配送中");

        } else if ("4".equals(state)) {
            // 4已完成(判读)
//            (finishtime-buytime) -(endtime -buytime)=finishtime-endtime
//            完成状态里面的，已耗时时间判读：(finishtime-addtime) -(endtime -addtime)>0 超时，<0正常耗时;;;简化：finishtime-endtime>0 超时，<0正常耗时
//            配送中的待确认状态：是完成状态4里面区分uidtime，非0：已完成；0：待确认;
            mOrderStateTextView.setText("已完成");

        } else if ("6".equals(state)) {
            // 订单退款，细分 store_uid  0为:系统退款;
            if ("0".equals(itemBean.getStore_uid())) {
                // 0为:系统退款;
                mOrderStateTextView.setText("系统退款");
            } else {
                // 1正常：已退款
                mOrderStateTextView.setText("已退款");
            }
        }
        // 0：未处理 1：已处理
        mOrderStateTextView.setTextColor(ContextCompat.getColor(mContext, "0".equals(itemBean.getNot_operating()) ? R.color.orange_ff725c : R.color.black_444444));
        // 配送方式 1自营配送 2门店自提
        mPsTypeTextView.setText("2".equals(itemBean.getDelivery()) ? "门店自提" : "兆邻配送");

        //  订单类型：0普通订单 1 预售订单 2团购订单
        String activity_type = itemBean.getActivity_type();
        if ("1".equals(activity_type)) {
            // 预售订单
            mOrderTypeTextView.setText("预售订单");
        } else if ("2".equals(activity_type)) {
            // 团购订单
            mOrderTypeTextView.setText("团购订单");
        } else {
            // 普通订单
            mOrderTypeTextView.setText("普通订单");
        }
        // 支付方式
        String payment = itemBean.getPayment();
        String paymentStr = null;
        switch (payment) {
            case "1":
                paymentStr = "支付宝";
                break;
            case "2":
                paymentStr = "微信";
                break;
            case "3":
                paymentStr = "银联";
                break;
            case "4":
                paymentStr = "钱包";
                break;
            case "5":
                paymentStr = "货到付款";
                break;
        }
        mPayTypeTextView.setText(paymentStr);
        // 订单号点击事件
        mOrderNumberTextView.setOnClickListener(v -> {
            // 跳转到订单详情
            activity.startActivity(OrderDesActivity.newIntent(mContext, true, itemBean.getId(), ""));
        });
    }

}
