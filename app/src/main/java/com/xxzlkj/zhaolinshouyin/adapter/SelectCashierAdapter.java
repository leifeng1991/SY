package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:收银员列表
 *
 * @author leifeng
 *         2017/12/19 16:25
 */


public class SelectCashierAdapter extends BaseAdapter<User> {
    public Map<Integer, Boolean> map = new HashMap<>();

    public SelectCashierAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, User itemBean) {
        ImageView mCheckImageView = holder.getView(R.id.id_check_box);// 单选
        TextView mJobNumberTextView = holder.getView(R.id.id_job_number);// 工号
        TextView mCashierTextView = holder.getView(R.id.id_cashier);// 收银员

        mCheckImageView.setImageResource(map.get(position) ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);

        mJobNumberTextView.setText(itemBean.getUid());
        mCashierTextView.setText(itemBean.getName());
    }

    @Override
    public void clearAndAddList(List<User> list) {
        for (int i = 0; i < list.size(); i++) {
            map.put(i,false);
        }
        super.clearAndAddList(list);
    }
}
