package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.Order;

import java.util.Locale;

/**
 * 描述:对账订单详情
 *
 * @author leifeng
 *         2017/12/15 13:37
 */


public class OrderDesAdapter extends BaseAdapter<Order> {
    public OrderDesAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, Order itemBean) {
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mOrderNumberTextView = holder.getView(R.id.id_order_number);// 订单号
        TextView mWaterTypeTextView = holder.getView(R.id.id_water_type);// 流水类型
        TextView mPayTypeTextView = holder.getView(R.id.id_pay_type);// 支付类型
        TextView mAmountPaidTextView = holder.getView(R.id.id_amount_paid);// 实付金额
        TextView mOrderTimeTextView = holder.getView(R.id.id_order_time);// 下单时间
        View lineView = holder.getView(R.id.id_line);// 分割线
        lineView.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
        // 设置数据
        // 1：消费 2：退款
        mWaterTypeTextView.setText(1 == itemBean.getState() ? "消费" : "退款");
        // 1：支付宝 2：微信 6：现金
        int payType = itemBean.getPayment();
        switch (payType) {
            case 1:
                mPayTypeTextView.setText("支付宝");
                break;
            case 2:
                mPayTypeTextView.setText("微信");
                break;
            case 6:
                mPayTypeTextView.setText("现金");
                break;
        }
        mAmountPaidTextView.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));
        mOrderTimeTextView.setText(DateUtils.getYearMonthDayHourMinuteSeconds(itemBean.getAddtime() * 1000));
        mLineNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mOrderNumberTextView.setText(itemBean.getOrder_id());
    }
}
