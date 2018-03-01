package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.Goods;

import java.util.List;

/**
 * 描述:中间商品
 *
 * @author leifeng
 *         2017/12/7 18:17
 */
public class CenterGoodsAdapter extends BaseAdapter<Goods> {
    public int selectPosition = -1;

    public CenterGoodsAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, Goods itemBean) {
        LinearLayout mParent = holder.getView(R.id.id_parent);
        TextView tv_title = holder.getView(R.id.tv_title);// 标题
        TextView tv_price = holder.getView(R.id.tv_price);// 价格
        // 设置值
        tv_title.setText(itemBean.getTitle());
        tv_price.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));

//        mParent.setBackgroundResource(selectPosition == position ? R.drawable.shape_rectangle_stroke_fed500 : R.drawable.shape_rectangle_stroke_b4b4b4);
    }

    @Override
    public void clearAndAddList(List<Goods> list) {
        selectPosition = -1;
        super.clearAndAddList(list);
    }
}
