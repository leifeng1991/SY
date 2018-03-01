package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xxzlkj.zhaolinshouyin.R;

import java.util.List;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/14 15:29
 */


public class SpinnerAdapter extends ArrayAdapter<String> {
    private Spinner spinner;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, Spinner spinner) {
        super(context, resource, objects);
        this.spinner = spinner;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_spinner_item, null);
        TextView mTitleTextView = (TextView) view.findViewById(R.id.id_title);
        // 设置数据
        mTitleTextView.setText(getItem(position));
        if (spinner.getSelectedItemPosition() == position) {
            mTitleTextView.setBackgroundColor(0xffffeae2);
            mTitleTextView.setTextColor(0xffff725c);
        } else {
            mTitleTextView.setBackgroundColor(0xffffffff);
            mTitleTextView.setTextColor(0xff444444);
        }

        return view;
    }
}
