package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.GoodsClass;

/**
 * 描述:三级分类
 *
 * @author leifeng
 *         2017/12/7 18:17
 */
public class ThreeLevelClassificationAdapter extends BaseAdapter<GoodsClass> {
    private int selectedPosition;

    public ThreeLevelClassificationAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, GoodsClass itemBean) {
        TextView tv_title = holder.getView(R.id.tv_title);// 标题
        // 设置选中
        tv_title.setBackgroundResource(position == selectedPosition ? R.drawable.shape_rectangle_stroke_ff725c : R.drawable.shape_rectangle_stroke_b4b4b4);
        tv_title.setTextColor(position == selectedPosition ? 0xffFF725C : 0xff444444);
        // 设置值
        tv_title.setText(itemBean.getTitle());
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
