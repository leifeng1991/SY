package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;
import com.xxzlkj.zhaolinshouyin.listener.OnWeightingFinishListener;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class ViceScreenInputGoodsAdapter extends BaseAdapter<InputGoodsBean> {
    private int selectedPosition;

    public ViceScreenInputGoodsAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, InputGoodsBean itemBean) {
        TextView tv_id = holder.getView(R.id.id_line_number);// 编号
        TextView tv_title = holder.getView(R.id.id_product_name);// 标题
        TextView tv_num = holder.getView(R.id.id_number);// 数量
        TextView tv_price = holder.getView(R.id.id_price);// 现价
        // 设置选中
        int colorRes;
        if (selectedPosition == position) {
            // 选中
            colorRes = R.color.orange_ff725c;
            holder.itemView.setBackgroundResource(R.drawable.shape_shouyin_item_selected);
        } else {
            // 未选中
            colorRes = R.color.black_444444;
            holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.TRANSPARENT : Color.WHITE);
        }
        // 设置是否是称重中
        if (itemBean.isWeighing()) {
            // 称重中，再判断，此条目是否录入过商品
            if (TextUtils.isEmpty(itemBean.getCode())) {
                // 没有录入商品
                tv_num.setBackgroundResource(R.drawable.shape_rectangle_b4b4b4);
            } else {
                // 已录入了商品
                tv_num.setBackgroundResource(R.drawable.shape_rectangle_b4b4b4);

            }
        } else {
            // 普通的状态
            tv_num.setBackground(null);
        }

        // 设置数据
        tv_id.setText(String.valueOf(position + 1));// 编号
        tv_title.setText(itemBean.getTitle());// 标题
        tv_num.setText(StringUtil.saveThreeDecimal(itemBean.getNum()));// 数量(重量)
        tv_price.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));// 现价
        // 设置通用
        tv_id.setTextColor(ContextCompat.getColor(mContext, colorRes));// 编号
        tv_title.setTextColor(ContextCompat.getColor(mContext, colorRes));// 标题
        tv_num.setTextColor(ContextCompat.getColor(mContext, colorRes));// 数量(重量)
        tv_price.setTextColor(ContextCompat.getColor(mContext, colorRes));// 现价
    }

    public void setSelectedPositionAndNotifySelected(int selectedPosition) {
        notifyItemChanged(this.selectedPosition);
        notifyItemChanged(selectedPosition);
        setSelectedPosition(selectedPosition);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

}
