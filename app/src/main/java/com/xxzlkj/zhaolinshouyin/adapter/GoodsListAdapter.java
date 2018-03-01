package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class GoodsListAdapter extends BaseAdapter<Goods> {
    private boolean isCanChecked;
    private OnIsAllCheckedListener listener;
    private OnPrintListener printListener;
    public boolean isHasPrint = false;

    /**
     * @param isCanChecked  是否可以选中
     * @param listener      选中监听事件
     * @param printListener 打印监听事件
     */
    public GoodsListAdapter(Context context, boolean isCanChecked, int itemId, OnIsAllCheckedListener listener, OnPrintListener printListener) {
        super(context, itemId);
        this.isCanChecked = isCanChecked;
        this.listener = listener;
        this.printListener = printListener;
    }

    @Override
    public void convert(BaseViewHolder holder, int position, Goods itemBean) {
        LinearLayout mCheckAllLayout = holder.getView(R.id.id_check_all_layout);// 选中布局
        mCheckAllLayout.setVisibility(isCanChecked ? View.VISIBLE : View.GONE);
        ImageView mCheckedImageView = holder.getView(R.id.id_check_image);// 选中图片
        mCheckedImageView.setImageResource(itemBean.getIsChecked() ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
        holder.itemView.setBackgroundColor((position + 1) % 2 == 0 ? 0xffF7F8FC : 0xffffffff);
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mProductNameTextView = holder.getView(R.id.id_product_name);// 货品名称
        TextView mNoTextView = holder.getView(R.id.id_no);// 货号
        TextView mPriceTextView = holder.getView(R.id.id_price);// 单价
        TextView mStockTextView = holder.getView(R.id.id_stock);// 库存量
        TextView mSafeStockTextView = holder.getView(R.id.id_safe_stock);// 安全库存
        TextView mAccumulatedSalesTextView = holder.getView(R.id.id_accumulated_sales);// 累计销售量
        RelativeLayout mPrintLayout = holder.getView(R.id.id_print_layout);// 打印按钮布局
        Button mPrintButton = holder.getView(R.id.id_print_btn);// 打印按钮
        mPrintLayout.setVisibility(isHasPrint ? View.VISIBLE : View.GONE);
        mPrintButton.setVisibility(isHasPrint ? View.VISIBLE : View.GONE);
        // 设置数据
        mLineNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mProductNameTextView.setText(itemBean.getTitle());
        mNoTextView.setText(itemBean.getCode());
        mPriceTextView.setText(StringUtil.saveTwoDecimal(itemBean.getPrice()));
        mStockTextView.setText(StringUtil.saveThreeDecimal(itemBean.getStock()));
        mSafeStockTextView.setText(StringUtil.saveThreeDecimal(itemBean.getMin_qty()));
        mAccumulatedSalesTextView.setText(StringUtil.saveThreeDecimal(itemBean.getNum()));
        // 选中点击事件
        mCheckAllLayout.setOnClickListener(v -> {
            itemBean.setIsChecked(!itemBean.getIsChecked());
            changeItem(position, itemBean);
            if (listener != null)
                listener.onIsAllChecked(getGoodList().size() == getList().size());
        });
        // 打印
        mPrintButton.setOnClickListener(v -> {
            if (printListener != null)
                printListener.onPrint(itemBean);
        });
    }

    @Override
    public void addList(List<Goods> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setIsChecked(false);
        }
        super.addList(list);
    }

    /**
     * 获取所有被选中商品
     */
    public List<Goods> getGoodList() {
        List<Goods> newList = new ArrayList<>();
        List<Goods> list = getList();
        for (int i = 0; i < list.size(); i++) {
            Goods goods = list.get(i);
            if (goods.getIsChecked())
                newList.add(goods);
        }
        return newList;
    }

    public interface OnIsAllCheckedListener {
        void onIsAllChecked(boolean isAllChecked);
    }

    public interface OnPrintListener {
        void onPrint(Goods goods);
    }
}
