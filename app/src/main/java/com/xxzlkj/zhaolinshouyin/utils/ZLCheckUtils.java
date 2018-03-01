package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;

import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.db.ThreeGoods;
import com.xxzlkj.zhaolinshouyin.model.OrderItemBean;
import com.xxzlkj.zhaolinshouyin.model.ThreeGoodsOrGoodsInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2018/2/3 16:46
 */

public class ZLCheckUtils {
    /**
     * 收银支付，检测是否含有不可销售商品
     *
     * @return true，代表有，false，代表没有
     */
    public static boolean checkHasNoCanBuyGoodsByPay(Activity activity, OrderItemBean mOrderItemBean) {
        List<OrderDetail> orderDetails = mOrderItemBean.getOrderDetails();
        if (orderDetails != null) {
            for (OrderDetail orderDetail : orderDetails) {
                String goodsCode = orderDetail.getCode();
                Goods goods = DaoUtils.getGoodsByGoodsCode(goodsCode);
                if (goods != null) {
                    // 自营商品
                    if (goods.getState() == 2 || goods.getState() == 4) {
                        // 不可销售商品
                        ZLDialogUtil.showNoGoodsDialog(activity, goodsCode, "该商品已被禁止销售！");
                        return true;
                    }
                } else {
                    ThreeGoods threeGoods = DaoUtils.getThreeGoodsByGoodsCode(goodsCode);
                    if (threeGoods != null) {
                        // 三方商品
                        // state;// 控制是否可售:1：下架；2:上架；
                        if (threeGoods.getState() == 1) {
                            // 下架
                            ZLDialogUtil.showNoGoodsDialog(activity, goodsCode, "该商品已被禁止销售！");
                            return true;
                        }
                    } else {
                        // 商品库无此商品
                        ZLDialogUtil.showNoGoodsDialog(activity, goodsCode, "商品库无此商品");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检测是否含有不可销售商品
     *
     * @return true，代表有，false，代表没有
     */
    public static boolean checkHasNoCanBuyGoods(Activity activity, Goods goods) {
//      state;// 控制是否可售:1：可采购，可销售；2:可采购，不可销售；3不可采购，可销售，4不可采购，不可销售
        if (goods != null && (goods.getState() == 2 || goods.getState() == 4)) {
            ZLDialogUtil.showNoGoodsDialog(activity, goods.getCode(), "该商品已被禁止销售！");
            return true;
        }
        return false;
    }

    /**
     * 收银扫码，检测是否含有不可销售商品
     *
     * @return true，代表有，false，代表没有
     */
    public static boolean checkHasNoCanBuyGoodsByScanCode(Activity activity, ThreeGoodsOrGoodsInfoBean threeGoodsOrGoodsInfoBean) {
        if (threeGoodsOrGoodsInfoBean == null)
            return false;
        if (threeGoodsOrGoodsInfoBean.isThreeGoods()) {
            // 三方商品
            // state;// 控制是否可售:1：下架；2:上架；
            if (threeGoodsOrGoodsInfoBean.getState() == 1) {
                ZLDialogUtil.showNoGoodsDialog(activity, threeGoodsOrGoodsInfoBean.getCode(), "该商品已被禁止销售！");
                return true;
            }
        } else {
            // 自营商品
            // state;// 控制是否可售:1：可采购，可销售；2:可采购，不可销售；3不可采购，可销售，4不可采购，不可销售
            if (threeGoodsOrGoodsInfoBean.getState() == 2 || threeGoodsOrGoodsInfoBean.getState() == 4) {
                ZLDialogUtil.showNoGoodsDialog(activity, threeGoodsOrGoodsInfoBean.getCode(), "该商品已被禁止销售！");
                return true;
            }
        }
        return false;
    }

    /**
     * 挂单过滤可销售商品
     */
    public static List<InputGoodsBean> entryOrderFilterHasCanBuyGoods(Activity activity, List<InputGoodsBean> inputGoodsBeans) {
        List<InputGoodsBean> filterInputGoodsBeans = new ArrayList<>();
        if (inputGoodsBeans != null) {
            for (InputGoodsBean inputGoodsBean : inputGoodsBeans) {
                String goodsCode = inputGoodsBean.getCode();
                Goods goods = DaoUtils.getGoodsByGoodsCode(goodsCode);
                if (goods != null) {
                    // 自营商品
                    if (goods.getState() == 2 || goods.getState() == 4) {
                        // 不可销售商品
                        ZLDialogUtil.showNoGoodsDialog(activity, goodsCode, "该商品已被禁止销售！");
                    } else {
                        // 可销售
                        filterInputGoodsBeans.add(inputGoodsBean);
                    }
                } else {
                    ThreeGoods threeGoods = DaoUtils.getThreeGoodsByGoodsCode(goodsCode);
                    if (threeGoods != null) {
                        // 三方商品
                        // state;// 控制是否可售:1：下架；2:上架；
                        if (threeGoods.getState() == 1) {
                            // 下架
                            ZLDialogUtil.showNoGoodsDialog(activity, goodsCode, "该商品已被禁止销售！");
                        } else {
                            // 上架
                            filterInputGoodsBeans.add(inputGoodsBean);
                        }
                    } else {
                        // 商品库无此商品
                        ZLDialogUtil.showNoGoodsDialog(activity, goodsCode, "商品库无此商品");
                    }
                }

            }
        }
        return filterInputGoodsBeans;
    }
}
