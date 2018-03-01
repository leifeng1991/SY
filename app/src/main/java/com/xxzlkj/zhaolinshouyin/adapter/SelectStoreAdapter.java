package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.model.StoreListBean;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class SelectStoreAdapter extends BaseAdapter<StoreListBean.DataBean> {
    private int selectPosition;

    public SelectStoreAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, StoreListBean.DataBean itemBean) {
        TextView tv_title = holder.getView(R.id.tv_title);// 标题
        // 设置选中
        tv_title.setBackgroundResource(selectPosition == position ? R.drawable.shape_rectangle_radius_ff725c : R.drawable.shape_stroke_b4b4b4_30);
        tv_title.setTextColor(selectPosition == position ? 0xffffffff : 0xff444444);
        // 设置数据
        tv_title.setText(itemBean.getTitle());
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
