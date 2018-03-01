package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.CommonGoods;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.DragItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class CommonGoodsListAdapter extends BaseAdapter<CommonGoods> implements DragItemTouchHelper.ItemTouchAdapter {
    private OnIsAllCheckedListener listener;

    public CommonGoodsListAdapter(Context context, int itemId, OnIsAllCheckedListener listener) {
        super(context, itemId);
        this.listener = listener;
    }

    @Override
    public void convert(BaseViewHolder holder, int position, CommonGoods itemBean) {
        Goods goods = itemBean.getGoods();
        LinearLayout mCheckAllLayout = holder.getView(R.id.id_check_all_layout);// 选中布局
        mCheckAllLayout.setVisibility(View.VISIBLE);
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
        TextView mAddTimeTextView = holder.getView(R.id.id_add_time);// 添加时间
        mAddTimeTextView.setVisibility(View.VISIBLE);
        // 设置数据
        mLineNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mProductNameTextView.setText(goods.getTitle());
        mNoTextView.setText(goods.getCode());
        mPriceTextView.setText(StringUtil.saveTwoDecimal(goods.getPrice()));
        mStockTextView.setText(StringUtil.saveThreeDecimal(goods.getStock()));
        mSafeStockTextView.setText(StringUtil.saveThreeDecimal(goods.getMin_qty()));
        mAccumulatedSalesTextView.setText(StringUtil.saveThreeDecimal(goods.getNum()));
        mAddTimeTextView.setText(DateUtils.getYearMonthDayHourMinuteSeconds(itemBean.getAddTime() * 1000));
        // 选中点击事件
        mCheckAllLayout.setOnClickListener(v -> {
            itemBean.setIsChecked(!itemBean.getIsChecked());
            notifyItemChanged(position);
            if (listener != null)
                listener.onIsAllChecked(getGoodList().size() == getList().size());
        });
    }

    @Override
    public void clearAndAddList(List<CommonGoods> list) {
        // 选中归原
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setIsChecked(false);
        }
        super.clearAndAddList(list);
    }

    @Override
    public void addList(List<CommonGoods> list) {
        // 选中归原
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setIsChecked(false);
        }
        super.addList(list);
    }

    /**
     * 获取所有被选中商品
     */
    public List<CommonGoods> getGoodList() {
        List<CommonGoods> newList = new ArrayList<>();
        List<CommonGoods> list = getList();
        for (int i = 0; i < list.size(); i++) {
            CommonGoods commonGoods = list.get(i);
            if (commonGoods.getIsChecked())
                newList.add(commonGoods);
        }
        return newList;
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition != toPosition) {
            // 移动商品
            CommonGoods fromCommonGoods = getList().get(fromPosition);
            // 与之交换的商品
            CommonGoods toCommonGoods = getList().get(toPosition);
            long fromTime = fromCommonGoods.getEditTime();
            long toTime = toCommonGoods.getEditTime();
            List<CommonGoods> list = new ArrayList<>();
            list.add(fromCommonGoods);
            list.add(toCommonGoods);
            DaoUtils.updateCommonGoodsList(list, fromTime, toTime, new DaoUtils.OnDaoResultListener<String>() {
                @Override
                public void onSuccess(String bean) {

                }

                @Override
                public void onFailed() {
                    ToastManager.showShortToast(mContext, "移动失败");
                }
            });

        }

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getList(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(getList(), i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
    }

    @Override
    public void onSwiped(int position) {
    }

    public interface OnIsAllCheckedListener {
        void onIsAllChecked(boolean isAllChecked);
    }

}
