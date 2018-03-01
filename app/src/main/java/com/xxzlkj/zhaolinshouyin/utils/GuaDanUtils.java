package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;

import com.xxzlkj.zhaolinshouyin.adapter.EntryOrdersLeftAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.EntryOrdersRightAdapter;
import com.xxzlkj.zhaolinshouyin.db.GuaDan;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/23 15:50
 */


public class GuaDanUtils {

    /**
     * 插入并获取挂单列表和第一个挂单对应的商品清单
     *
     * @param list          所插入的挂单商品清单
     * @param mLeftAdapter  挂单列表适配器
     * @param mRightAdapter 商品列表适配器
     */
    public static void insertAndGetList(Activity activity, List<InputGoodsBean> list, EntryOrdersLeftAdapter mLeftAdapter, EntryOrdersRightAdapter mRightAdapter) {
        if (list != null && list.size() > 0) {
            DaoUtils.insertOrReplaceGuaDan(list, new DaoUtils.OnDaoResultListener<String>() {
                @Override
                public void onSuccess(String bean) {
                    getGuadanList(activity, mLeftAdapter, mRightAdapter);
                }

                @Override
                public void onFailed() {
                    getGuadanList(activity, mLeftAdapter, mRightAdapter);
                }
            });
        } else {
            getGuadanList(activity, mLeftAdapter, mRightAdapter);
        }
    }

    /**
     * 获取挂单右侧商品列表
     *
     * @param id            挂单id
     * @param mRightAdapter 右侧商品适配器
     */
    public static void getRightGoodsList(Activity activity, String id, EntryOrdersRightAdapter mRightAdapter) {
        DaoUtils.getInputGoodsBeanListByGuaDanId(activity, id, new DaoUtils.OnDaoResultListListener<InputGoodsBean>() {
            @Override
            public void onSuccess(List<InputGoodsBean> list1) {
                // 重置右侧数据
                mRightAdapter.clearAndAddList(list1);
            }

            @Override
            public void onFailed() {
                // 重置右侧数据
                mRightAdapter.clear();
            }
        });
    }

    /**
     * 获取挂单列表和第一个挂单对应的商品列表
     *
     * @param mLeftAdapter  挂单列表适配器
     * @param mRightAdapter 商品列表适配器
     */
    public static void getGuadanList(Activity activity, EntryOrdersLeftAdapter mLeftAdapter, EntryOrdersRightAdapter mRightAdapter) {
        DaoUtils.getGuaDanList(activity, new DaoUtils.OnDaoResultListListener<GuaDan>() {
            @Override
            public void onSuccess(List<GuaDan> list) {
                mLeftAdapter.clearAndAddList(list);
                // 获取挂单对应的商品列表
                if (list != null && list.size() > 0 && list.get(0) != null)
                    DaoUtils.getInputGoodsBeanListByGuaDanId(activity, list.get(0).getId(), new DaoUtils.OnDaoResultListListener<InputGoodsBean>() {
                        @Override
                        public void onSuccess(List<InputGoodsBean> list) {
                            mRightAdapter.addList(list);
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
            }

            @Override
            public void onFailed() {
                mLeftAdapter.clear();
            }
        });
    }

    /**
     * 挂单删除
     *
     * @param mLeftAdapter  左侧挂单适配器
     * @param mRightAdapter 右侧挂单对应的商品列表适配器
     */
    public static void delGuaDan(Activity activity, EntryOrdersLeftAdapter mLeftAdapter, EntryOrdersRightAdapter mRightAdapter) {
        if (mLeftAdapter.getItemCount() > 0) {
            // 删除挂单集合
            List<GuaDan> guaDans = new ArrayList<>();
            guaDans.add(mLeftAdapter.getList().get(mLeftAdapter.selectPosition));
            // 删除挂单以及对应的列表
            DaoUtils.delGuadanAndInputGoodsList(activity, guaDans, new DaoUtils.OnDaoResultListener<String>() {
                @Override
                public void onSuccess(String bean) {
                    // 移除当前选中项
                    mLeftAdapter.removeItem(mLeftAdapter.selectPosition);
                    // 设置默认选中为0
                    mLeftAdapter.selectPosition = 0;
                    mLeftAdapter.notifyDataSetChanged();
                    if (mLeftAdapter.getItemCount() > 0) {
                        // 右侧列表重置
                        getRightGoodsList(activity, mLeftAdapter.getList().get(0).getId(), mRightAdapter);
                    }else {
                        mRightAdapter.clear();
                    }
                }

                @Override
                public void onFailed() {

                }
            });

        }
    }

    /**
     * 清空挂单
     *
     * @param mLeftAdapter  左侧挂单适配器
     * @param mRightAdapter 右侧挂单对应的商品列表适配器
     */
    public static void clearGuaDan(Activity activity, EntryOrdersLeftAdapter mLeftAdapter, EntryOrdersRightAdapter mRightAdapter) {
        DaoUtils.delGuadanAndInputGoodsList(activity, mLeftAdapter.getList(), new DaoUtils.OnDaoResultListener<String>() {
            @Override
            public void onSuccess(String bean) {
                // 有挂单才可以清空
                if (mLeftAdapter.getItemCount() > 0) {
                    // 清空挂单以及对应的列表
                    DaoUtils.delGuadanAndInputGoodsList(activity, mLeftAdapter.getList(), new DaoUtils.OnDaoResultListener<String>() {
                        @Override
                        public void onSuccess(String bean) {
                            mLeftAdapter.clear();
                            mRightAdapter.clear();
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 挂单提取删除
     *
     * @param mLeftAdapter 左侧挂单适配器
     */
    public static void ExtractGuaDan(EntryOrdersLeftAdapter mLeftAdapter) {
        if (mLeftAdapter.getItemCount() > 0) {
            // 删除挂单集合
            List<GuaDan> guaDans = new ArrayList<>();
            guaDans.add(mLeftAdapter.getList().get(mLeftAdapter.selectPosition));
        }
    }
}
