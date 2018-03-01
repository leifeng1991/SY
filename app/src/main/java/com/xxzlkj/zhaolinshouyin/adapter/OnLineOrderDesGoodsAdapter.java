package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.model.OrderDesBean;

/**
 * 描述:线上订单 商品列表
 *
 * @author leifeng
 *         2017/12/15 13:37
 */


public class OnLineOrderDesGoodsAdapter extends BaseAdapter<OrderDesBean.DataBean.DetailBean> {
    public OnLineOrderDesGoodsAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, OrderDesBean.DataBean.DetailBean itemBean) {
        TextView mGoodsNameTextView = holder.getView(R.id.id_goods_name);// 商品名字
        TextView mGoodsPriceTextView = holder.getView(R.id.id_goods_price);// 商品单价
        TextView mGoodsNumberTextView = holder.getView(R.id.id_goods_number);// 商品数量
        TextView mTotalTextView = holder.getView(R.id.id_total);// 合计
        // 设置数据
        mGoodsNameTextView.setText(itemBean.getTitle());
        mGoodsPriceTextView.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));
        mGoodsNumberTextView.setText(StringUtil.saveThreeDecimal(itemBean.getNum()));
        mTotalTextView.setText(StringUtil.saveTwoDecimal(StringUtil.mul(String.format("%s", itemBean.getPrice()), String.format("%s", itemBean.getNum()))));
    }
}
