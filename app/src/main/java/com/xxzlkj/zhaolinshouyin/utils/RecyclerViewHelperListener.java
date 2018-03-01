package com.xxzlkj.zhaolinshouyin.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/11/17 9:31
 */
public class RecyclerViewHelperListener extends RecyclerView.OnScrollListener {
    private final RecyclerView recyclerView;
    private final LinearLayoutManager layoutManager;
    private boolean move;
    private int mIndex;

    public RecyclerViewHelperListener(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //在这里进行第二次滚动（最后的100米！）
        if (move) {
            move = false;
            //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
            int n = mIndex - layoutManager.findFirstVisibleItemPosition();
            if (0 <= n && n < this.recyclerView.getChildCount()) {
                //获取要置顶的项顶部离RecyclerView顶部的距离
                if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    // 垂直的
                    int top = this.recyclerView.getChildAt(n).getTop();
                    //最后的移动
                    this.recyclerView.scrollBy(0, top);
                } else {
                    // 水平的
                    int left = this.recyclerView.getChildAt(n).getLeft();
                    //最后的移动
                    this.recyclerView.scrollBy(left, 0);
                }

            }
        }
    }

    public void scrollToPosition(int position) {
        mIndex = position;

        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = layoutManager.findFirstVisibleItemPosition();
        int lastItem = layoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (position < firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView.scrollToPosition(position);
        } else if (position <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                // 垂直滚动
                int top = recyclerView.getChildAt(position - firstItem).getTop();
                recyclerView.scrollBy(0, top);
            } else {
                // 水平滚动
                int left = recyclerView.getChildAt(position - firstItem).getLeft();
                recyclerView.scrollBy(left, 0);
            }
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            recyclerView.scrollToPosition(position);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }

    }
}