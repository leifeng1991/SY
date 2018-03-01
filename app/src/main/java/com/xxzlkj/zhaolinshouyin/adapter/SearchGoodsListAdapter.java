package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.Goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * 描述: 搜索商品列表适配器
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class SearchGoodsListAdapter extends BaseAdapter<Goods> {
    public int selectPosition = -1;

    public SearchGoodsListAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, Goods itemBean) {
        ImageView mCheckedImageView = holder.getView(R.id.id_check_image);// 选中图片
        mCheckedImageView.setVisibility(View.VISIBLE);
        mCheckedImageView.setImageResource(selectPosition == position ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
        holder.itemView.setBackgroundColor((position + 1) % 2 == 0 ? 0xffF7F8FC : 0xffffffff);
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mProductNameTextView = holder.getView(R.id.id_product_name);// 货品名称
        TextView mNoTextView = holder.getView(R.id.id_no);// 货号
        TextView mPriceTextView = holder.getView(R.id.id_price);// 单价
        // 设置数据
        mLineNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mProductNameTextView.setText(itemBean.getTitle());
        mNoTextView.setText(itemBean.getCode());
        mPriceTextView.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));

    }

}
