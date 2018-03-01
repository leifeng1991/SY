package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;

import java.util.Locale;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/22 15:21
 */


public class EntryOrdersRightAdapter extends BaseAdapter<InputGoodsBean> {
    public EntryOrdersRightAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, InputGoodsBean itemBean) {
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mGoodsNameTextView = holder.getView(R.id.id_goods_name);// 商品名称
        TextView mNoTextView = holder.getView(R.id.id_no);// 货号
        TextView mPriceTextView = holder.getView(R.id.id_price);// 单价
        TextView mNumberTextView = holder.getView(R.id.id_number);// 数量

        mLineNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mGoodsNameTextView.setText(itemBean.getTitle());
        mNoTextView.setText(itemBean.getCode());
        mPriceTextView.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));
        mNumberTextView.setText(StringUtil.saveThreeDecimal(itemBean.getNum()));
    }
}
