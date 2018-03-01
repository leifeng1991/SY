package com.xxzlkj.zhaolinshouyin.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.GoodsClass;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;

import java.util.List;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/14 15:29
 */


public class GoodsListSpinnerAdapter extends ArrayAdapter<GoodsClass> {
    private Spinner[] spinner;
    private Activity activity;

    public GoodsListSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<GoodsClass> objects, Activity activity, Spinner... spinner) {
        super(context, resource, objects);
        this.spinner = spinner;
        this.activity = activity;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_spinner_item, null);
        TextView mTitleTextView = (TextView) view.findViewById(R.id.id_title);
        GoodsClass item = getItem(position);
        if (item != null) {
            // 设置数据
            mTitleTextView.setText(item.getTitle());
            if (spinner[0].getSelectedItemPosition() == position) {
                mTitleTextView.setBackgroundColor(0xffffeae2);
                mTitleTextView.setTextColor(0xffff725c);
            } else {
                mTitleTextView.setBackgroundColor(0xffffffff);
                mTitleTextView.setTextColor(0xff444444);
            }
            if (spinner.length == 3) {
                spinner[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        GoodsClass item1 = getItem(position);
                        if (item1 != null)
                            // 获取二级分类
                            DaoUtils.getGoodsClassTwoTitleList(activity, item1.getId(), new DaoUtils.OnDaoResultListListener<GoodsClass>() {
                                @Override
                                public void onSuccess(List<GoodsClass> list) {
                                    GoodsListSpinnerAdapter adapter = new GoodsListSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, list, activity, spinner[1], spinner[2]);
                                    spinner[1].setAdapter(adapter);
                                    //设置样式
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                }

                                @Override
                                public void onFailed() {

                                }
                            });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            } else if (spinner.length == 2) {
                spinner[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        GoodsClass item1 = getItem(position);
                        if (item1 != null)
                            // 获取三级分类
                            DaoUtils.getGoodsClassThreeTitleList(activity, item1.getId(), new DaoUtils.OnDaoResultListListener<GoodsClass>() {
                                @Override
                                public void onSuccess(List<GoodsClass> list) {
                                    GoodsListSpinnerAdapter adapter = new GoodsListSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, list, activity, spinner[1]);
                                    spinner[1].setAdapter(adapter);
                                    //设置样式
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                }

                                @Override
                                public void onFailed() {

                                }
                            });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } else {
                spinner[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        getItem(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }

        return view;
    }
}
