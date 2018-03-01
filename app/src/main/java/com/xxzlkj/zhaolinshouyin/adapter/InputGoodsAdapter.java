package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.listener.OnWeightingFinishListener;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class InputGoodsAdapter extends BaseAdapter<InputGoodsBean> {
    private int selectedPosition;
    private OnWeightingFinishListener onWeightingFinishListener;
    private boolean isShowBigLayout;

    public InputGoodsAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, InputGoodsBean itemBean) {
        TextView tv_id = holder.getView(R.id.tv_id);// 编号
        TextView tv_goods_code = holder.getView(R.id.tv_goods_code);// 货号
        TextView tv_title = holder.getView(R.id.tv_title);// 标题
        ImageView iv_weight_bg = holder.getView(R.id.iv_weight_bg);// 数量--称重背景
        TextView tv_num = holder.getView(R.id.tv_num);// 数量
        TextView tv_prices = holder.getView(R.id.tv_prices);// 原价
        TextView tv_price = holder.getView(R.id.tv_price);// 现价
        TextView tv_all_price = holder.getView(R.id.tv_all_price);// 总价
        TextView tv_weighting_finish = holder.getView(R.id.tv_weighting_finish);// 称重中-完成按钮
        // 判断显示哪些条目
        tv_id.setVisibility(isShowBigLayout ? View.GONE : View.VISIBLE);// 编号
        tv_prices.setVisibility(isShowBigLayout ? View.GONE : View.VISIBLE);// 原价
        tv_all_price.setVisibility(isShowBigLayout ? View.GONE : View.VISIBLE);// 总价

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
                // 没有录入商品，称重背景显示，称重完成按钮隐藏
                iv_weight_bg.setVisibility(View.VISIBLE);
                tv_weighting_finish.setVisibility(View.GONE);
            } else {
                // 已录入了商品，称重背景显示，称重完成按钮显示
                iv_weight_bg.setVisibility(View.VISIBLE);
                tv_weighting_finish.setVisibility(View.VISIBLE);
                // 设置点击
                tv_weighting_finish.setOnClickListener(v -> {
                    // 更改状态，通知
                    itemBean.setWeighing(false);
                    itemBean.setAllPrices(itemBean.getNum() * itemBean.getPrice());// 设置商品总价
                    notifyItemChanged(position);
                    if (onWeightingFinishListener != null)
                        onWeightingFinishListener.onWeightingFinish();
                });
            }
        } else {
            // 普通的状态，称重背景隐藏，称重完成按钮隐藏
            iv_weight_bg.setVisibility(View.GONE);
            tv_weighting_finish.setVisibility(View.GONE);
        }

        // 设置数据
        tv_id.setText(String.valueOf(position + 1));// 编号
        tv_goods_code.setText(itemBean.getCode());// 货号
        tv_title.setText(itemBean.getTitle());// 标题
        tv_num.setText(StringUtil.saveThreeDecimal(itemBean.getNum()));// 数量(重量)
        tv_prices.setText(StringUtil.saveTwoDecimal(itemBean.getPrices()));// 原价
        tv_price.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));// 现价
        tv_all_price.setText(StringUtil.saveTwoDecimal(itemBean.getAllPrices()));// 总价
        // 设置通用
        tv_id.setTextColor(ContextCompat.getColor(mContext, colorRes));// 编号
        tv_goods_code.setTextColor(ContextCompat.getColor(mContext, colorRes));// 货号
        tv_title.setTextColor(ContextCompat.getColor(mContext, colorRes));// 标题
        tv_num.setTextColor(ContextCompat.getColor(mContext, colorRes));// 数量(重量)
        tv_prices.setTextColor(ContextCompat.getColor(mContext, colorRes));// 原价
        tv_price.setTextColor(ContextCompat.getColor(mContext, colorRes));// 现价
        tv_all_price.setTextColor(ContextCompat.getColor(mContext, colorRes));// 总价
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

    public void setOnWeightingFinishListener(OnWeightingFinishListener onWeightingFinishListener) {
        this.onWeightingFinishListener = onWeightingFinishListener;
    }

    /**
     * 设置大布局是否显示
     */
    public void setShowBigLayoutAndNotify(boolean isShowBigLayout) {
        this.isShowBigLayout = isShowBigLayout;
        notifyDataSetChanged();
    }
}
