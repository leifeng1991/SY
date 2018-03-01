package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.OrderDesActivity;
import com.xxzlkj.zhaolinshouyin.activity.OrderListActivity;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;
import com.zrq.spanbuilder.Spans;

import java.util.Locale;
import java.util.Map;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class OrderListAdapter extends BaseAdapter<Order> {
    private OrderListActivity activity;
    private Map<String, String> map;

    public OrderListAdapter(Context context, OrderListActivity activity, int itemId) {
        super(context, itemId);
        this.activity = activity;
        map = activity.getMap();
    }

    @Override
    public void convert(BaseViewHolder holder, int position, Order itemBean) {
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mOrderNumberTextView = holder.getView(R.id.id_order_number);// 订单号
        TextView mWaterTypeTextView = holder.getView(R.id.id_water_type);// 流水类型
        TextView mOrderTypeTextView = holder.getView(R.id.id_order_type);// 收银员
        TextView mPayTypeTextView = holder.getView(R.id.id_pay_type);// 支付方式
        TextView mOrderPriceTextView = holder.getView(R.id.id_order_price);// 订单金额
        TextView mAmountPaidTextView = holder.getView(R.id.id_amount_paid);// 实付金额
        TextView mSyncTypeTextView = holder.getView(R.id.id_sync_type_order);// 同步类型
        TextView mOrderTimeTextView = holder.getView(R.id.id_order_time);// 下单时间
        // 设置数据
        holder.itemView.setBackgroundColor((position + 1) % 2 == 0 ? 0xffF7F8FC : 0xffffffff);
        mLineNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mOrderNumberTextView.setText(Spans.builder().text(itemBean.getOrder_id()).color(0xff0074FC).underLine().color(0xff0074FC).build());
        // 1：消费 2：退款
        mWaterTypeTextView.setText(1 == itemBean.getState() ? "消费" : "退款");
        // 收银员
        String cashierName = map.get(itemBean.getStore_uid());
        mOrderTypeTextView.setText(TextUtils.isEmpty(cashierName) ? "" : cashierName);
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
        // 上传状态，0：未上传，1：已上传，2：无库存
        int syncType = itemBean.getUpload_state();
        switch (syncType) {
            case 0:// 未上传
                mSyncTypeTextView.setText("未上传");
                break;
            case 1:// 已上传
                mSyncTypeTextView.setText("已上传");
                break;
            case 2:// 无库存
                mSyncTypeTextView.setText("无库存");
                break;
        }
        double discount_pre_price = itemBean.getDiscount_pre_price();// 商品未优惠前的价格
        mOrderPriceTextView.setText(StringUtil.saveTwoDecimal(discount_pre_price));
        mAmountPaidTextView.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));
        mOrderTimeTextView.setText(DateUtils.getYearMonthDayHourMinuteSeconds(itemBean.getAddtime() * 1000));

        mOrderNumberTextView.setOnClickListener(v -> {
            // 跳转到详情界面
            activity.startActivity(OrderDesActivity.newIntent(mContext, false, itemBean.getOrder_id(), map.get(itemBean.getStore_uid())));
        });
    }
}
