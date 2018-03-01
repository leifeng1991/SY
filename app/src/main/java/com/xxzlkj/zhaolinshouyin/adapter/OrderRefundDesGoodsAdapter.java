package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.model.OrderRefundGoods;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/2/5 11:28
 */


public class OrderRefundDesGoodsAdapter extends BaseAdapter<OrderRefundGoods> {

    public OrderRefundDesGoodsAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, OrderRefundGoods itemBean) {
        TextView mGoodsNameTextView = holder.getView(R.id.id_goods_name);// 商品名称
        TextView mBuyPriceTextView = holder.getView(R.id.id_price);// 购买单价
        TextView mNumberTextView = holder.getView(R.id.id_number);// 数量
        TextView mSubtotalTextView = holder.getView(R.id.id_subtotal);// 小计
        View mLineView = holder.getView(R.id.id_line);// 小计
        // 设置数据
        OrderDetail orderDetail = itemBean.getOrderDetail();
        mGoodsNameTextView.setText(orderDetail.getTitle());
        double price = orderDetail.getPrice();
        mBuyPriceTextView.setText(StringUtil.saveTwoDecimal(price));
        double refundNumber = itemBean.getRefundNumber();
        mNumberTextView.setText(StringUtil.saveThreeDecimal(refundNumber));
        mSubtotalTextView.setText(StringUtil.saveTwoDecimal(refundNumber * price));
        mLineView.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
    }

}
