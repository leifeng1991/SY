package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.GuaDan;

import java.util.Locale;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/22 15:21
 */


public class EntryOrdersLeftAdapter extends BaseAdapter<GuaDan> {
    public int selectPosition = 0;

    public EntryOrdersLeftAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, GuaDan itemBean) {
        LinearLayout mParentLinearLayout = holder.getView(R.id.id_left_parent);
        TextView mEntrOrderNumberTextView = holder.getView(R.id.id_entry_order_number);//挂单号
        TextView mTimeTextView = holder.getView(R.id.id_time);//操作时间

        mParentLinearLayout.setBackgroundColor(selectPosition == position ? 0xffffeae2 : 0xffffffff);

        mEntrOrderNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mTimeTextView.setText(DateUtils.getYearMonthDay(itemBean.getAddtime() * 1000, "yyyy/MM/dd HH:mm"));
    }
}
