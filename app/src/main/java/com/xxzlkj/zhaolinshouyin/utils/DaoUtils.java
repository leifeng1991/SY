package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.xxzlkj.zhaolinshare.base.base.BaseActivity;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.db.Account;
import com.xxzlkj.zhaolinshouyin.db.AccountDao;
import com.xxzlkj.zhaolinshouyin.db.CommonGoods;
import com.xxzlkj.zhaolinshouyin.db.CommonGoodsDao;
import com.xxzlkj.zhaolinshouyin.db.DaoSession;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.db.GoodsBarCode;
import com.xxzlkj.zhaolinshouyin.db.GoodsBarCodeDao;
import com.xxzlkj.zhaolinshouyin.db.GoodsClass;
import com.xxzlkj.zhaolinshouyin.db.GoodsClassDao;
import com.xxzlkj.zhaolinshouyin.db.GoodsDao;
import com.xxzlkj.zhaolinshouyin.db.GuaDan;
import com.xxzlkj.zhaolinshouyin.db.GuaDanDao;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBeanDao;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.OrderDao;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.db.OrderDetailDao;
import com.xxzlkj.zhaolinshouyin.db.Params;
import com.xxzlkj.zhaolinshouyin.db.ParamsDao;
import com.xxzlkj.zhaolinshouyin.db.ThreeGoods;
import com.xxzlkj.zhaolinshouyin.db.ThreeGoodsDao;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.db.UserDao;
import com.xxzlkj.zhaolinshouyin.model.OrderItemBean;
import com.xxzlkj.zhaolinshouyin.model.ThreeGoodsOrGoodsInfoBean;
import com.xxzlkj.zhaolinshouyin.model.UnLoadOrderListBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/5 14:42
 */
public class DaoUtils {

    /**
     * 查询商品信息
     *
     * @param goodsCode 货号
     */
    public static void getGoodsByGoodsCode(Activity activity, String goodsCode, OnDaoResultListener<Goods> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsDao goodsDao = daoSession.getGoodsDao();
            Goods goods = goodsDao.load(goodsCode);
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (goods != null)
                    // 成功
                    listener.onSuccess(goods);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 查询三方、自营商品信息
     *
     * @param goodsCode 货号
     */
    public static void getThreeGoodsOrGoodsInfoByGoodsCode(Activity activity, String goodsCode, OnDaoResultListener<ThreeGoodsOrGoodsInfoBean> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询自营商品表
            GoodsDao goodsDao = daoSession.getGoodsDao();
            Goods goods = goodsDao.load(goodsCode);
            if (goods != null) {
                // 通知成功
                activity.runOnUiThread(() -> listener.onSuccess(new ThreeGoodsOrGoodsInfoBean(false, goods.getId(), goods.getCode(), goods.getTitle(), goods.getStandard(), goods.getState())));
            } else {
                // 查找三方商品表
                ThreeGoodsDao threeGoodsDao = daoSession.getThreeGoodsDao();
                ThreeGoods threeGoods = threeGoodsDao.load(goodsCode);
                if (threeGoods != null)
                    // 通知成功
                    activity.runOnUiThread(() -> listener.onSuccess(new ThreeGoodsOrGoodsInfoBean(true, threeGoods.getId(), threeGoods.getCode(), threeGoods.getTitle(), threeGoods.getStandard(), threeGoods.getState())));
                else
                    // 通知失败
                    listener.onFailed();
            }
        });
    }

    /**
     * 查询自营商品信息
     *
     * @param goodsCode 货号
     */
    public static Goods getGoodsByGoodsCode(String goodsCode) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 查询
        GoodsDao goodsDao = daoSession.getGoodsDao();
        return goodsDao.load(goodsCode);
    }

    /**
     * 查询自营商品信息
     *
     * @param goodsCode 货号
     */
    public static ThreeGoods getThreeGoodsByGoodsCode(String goodsCode) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 查询
        ThreeGoodsDao threeGoodsDao = daoSession.getThreeGoodsDao();
        return threeGoodsDao.load(goodsCode);
    }

    /**
     * 通过条形码，获取货号
     *
     * @param barCode 条形码
     */
    public static void getGoodsCodeByCarCode(Activity activity, String barCode, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsBarCodeDao goodsBarCodeDao = daoSession.getGoodsBarCodeDao();
            GoodsBarCode goodsBarCode = goodsBarCodeDao.load(barCode);
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (goodsBarCode != null)
                    // 成功
                    listener.onSuccess(goodsBarCode.getGoods_code());
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 生成订单、清单
     *
     * @param orderItemBean 封装的bean
     * @param listener      成功返回的是订单id
     */
    public static void addOrderByOrderItemBean(Activity activity, OrderItemBean orderItemBean, OnDaoResultListener<String> listener) {
        if (orderItemBean == null) {
            // 失败
            listener.onFailed();
            return;
        }
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 添加订单表、清单表
            OrderDao orderDao = daoSession.getOrderDao();
            OrderDetailDao orderDetailDao = daoSession.getOrderDetailDao();
            // 添加订单表
            orderDao.insertOrReplace(orderItemBean.getOrder());
            // 添加清单表
            orderDetailDao.insertOrReplaceInTx(orderItemBean.getOrderDetails());
            // 通知成功
            activity.runOnUiThread(() -> {
                // 成功
                listener.onSuccess(null);
            });
        });
    }

    /**
     * 获取最后一个订单
     */
    public static void getLastOrder(Activity activity, OnDaoResultListener<Order> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            OrderDao orderDao = daoSession.getOrderDao();
            List<Order> list = orderDao.queryBuilder().orderDesc(OrderDao.Properties.Addtime).list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list.get(0));
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * @param order_id 订单id
     * @param listener 成功返回的是数量
     */
    public static void getOrderDetailNumByOrderId(Activity activity, String order_id, OnDaoResultListener<Integer> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            OrderDetailDao orderDetailDao = daoSession.getOrderDetailDao();
            List<OrderDetail> orderDetails = orderDetailDao.queryBuilder().where(OrderDetailDao.Properties.Order_id.eq(order_id)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (orderDetails != null && orderDetails.size() > 0) {
                    // 成功
                    // 设置数量
                    int allNum = 0;
                    for (OrderDetail orderDetail : orderDetails) {
                        if (orderDetail.getStandard() == 1) {//  标品:1 非标:2
                            // 标品，数量加num
                            allNum += orderDetail.getNum();
                        } else {
                            // 非标品，数量加1
                            allNum += 1;
                        }
                    }
                    listener.onSuccess(allNum);
                } else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 异步获取商品，成功的回调在主线程
     *
     * @param order_id  （选传）订单id，有此id，后面的参数不生效
     * @param startTime （选传,-1代表不过滤）开始时间
     * @param endTime   （选传,-1代表不过滤）结束时间
     * @param cashierId （选传,""代表不过滤）收银员：
     * @param waterType （选传,-1代表不过滤） 流水类型： 1：消费 2：退款
     * @param payType   （选传,-1代表不过滤） 支付类型： 1：支付宝 2：微信 6：现金
     * @param payType   （选传,-1代表不过滤） 同步类型： 0：未上传，1：已上传，2：无库存
     */
    public static void getOrderList(BaseActivity activity, String order_id, long startTime, long endTime, String cashierId, int waterType, int payType, int syncType, OnDaoResultListListener<Order> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            OrderDao orderDao = daoSession.getOrderDao();
            List<Order> list;
            QueryBuilder<Order> orderQueryBuilder = orderDao.queryBuilder();
            if (!TextUtils.isEmpty(order_id)) {
                // 获取清单列表
                getOrderDetailListBySearchKeyword(activity, order_id, new OnDaoResultListListener<OrderDetail>() {
                    @Override
                    public void onSuccess(List<OrderDetail> list) {
                        Set<String> orderIds = new HashSet<>();
                        for (int i = 0; i < list.size(); i++) {
                            OrderDetail orderDetail = list.get(i);
                            orderIds.add(orderDetail.getOrder_id());
                        }
                        QueryBuilder<Order> orderQueryBuilder = orderDao.queryBuilder();
                        List<Order> orderList = orderQueryBuilder.where(OrderDao.Properties.Order_id.in(orderIds)).orderDesc(OrderDao.Properties.Addtime).list();
                        // 通知成功
                        activity.runOnUiThread(() -> {
                            // 回调
                            if (orderList != null && orderList.size() > 0)
                                // 成功
                                listener.onSuccess(orderList);
                            else
                                // 失败
                                listener.onFailed();
                        });
                    }

                    @Override
                    public void onFailed() {
                        listener.onFailed();
                    }
                });

            } else {
                // 订单号为null，过滤后面的参数
                if (startTime != -1)// 过滤开始时间
                    orderQueryBuilder = orderQueryBuilder.where(OrderDao.Properties.Addtime.ge(startTime));
                if (endTime != -1)// 过滤结束时间
                    orderQueryBuilder = orderQueryBuilder.where(OrderDao.Properties.Addtime.le(endTime));
                if (!TextUtils.isEmpty(cashierId))// 过滤收银员
                    orderQueryBuilder = orderQueryBuilder.where(OrderDao.Properties.Store_uid.eq(cashierId));
                if (waterType != -1)// 过滤流水类型
                    orderQueryBuilder = orderQueryBuilder.where(OrderDao.Properties.State.eq(waterType));
                if (payType != -1)// 过滤支付类型
                    orderQueryBuilder = orderQueryBuilder.where(OrderDao.Properties.Payment.eq(payType));
                if (syncType != -1)// 过滤同步类型
                    orderQueryBuilder = orderQueryBuilder.where(OrderDao.Properties.Upload_state.eq(syncType));
                list = orderQueryBuilder.orderDesc(OrderDao.Properties.Addtime).build().list();

                // 通知成功
                activity.runOnUiThread(() -> {
                    // 回调
                    if (list != null && list.size() > 0)
                        // 成功
                        listener.onSuccess(list);
                    else
                        // 失败
                        listener.onFailed();
                });
            }

        });
    }

    /**
     * 通过搜索关键字获取清单列表
     *
     * @param searchKeyword 搜索关键字
     */
    public static void getOrderDetailListBySearchKeyword(Activity activity, String searchKeyword, OnDaoResultListListener<OrderDetail> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            OrderDetailDao orderDetailDao = daoSession.getOrderDetailDao();
            QueryBuilder<OrderDetail> orderDetailQueryBuilder = orderDetailDao.queryBuilder();
            List<OrderDetail> list = orderDetailQueryBuilder.whereOr(
                    OrderDetailDao.Properties.Title.like("%" + searchKeyword + "%"),
                    OrderDetailDao.Properties.Order_id.like("%" + searchKeyword + "%"),
                    OrderDetailDao.Properties.Goods_id.like("%" + searchKeyword + "%")).list();

            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取订单详情
     *
     * @param order_id 订单id
     */
    public static void getOrderDetail(BaseActivity activity, String order_id, OnDaoResultListener<OrderItemBean> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            OrderDao orderDao = daoSession.getOrderDao();
            OrderDetailDao orderDetailDao = daoSession.getOrderDetailDao();
            Order order = orderDao.load(order_id);
            OrderItemBean orderItemBean = new OrderItemBean();
            if (order != null) {
                // 设置订单信息
                orderItemBean.setOrder(order);
                // 设置清单信息
                List<OrderDetail> orderDetails = orderDetailDao.queryBuilder().where(OrderDetailDao.Properties.Order_id.eq(order.getOrder_id())).build().list();
                orderItemBean.setOrderDetails(orderDetails);
            }
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (orderItemBean.getOrder() != null)
                    // 成功
                    listener.onSuccess(orderItemBean);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取未上传的订单列表
     */
    public static void getUnLoadOrderList(Activity activity, OnDaoResultListener<UnLoadOrderListBean> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            OrderDao orderDao = daoSession.getOrderDao();
            OrderDetailDao orderDetailDao = daoSession.getOrderDetailDao();
            // 上传状态，0：未上传，1：已上传，2：无库存
            // 获取非已上传的，并且按同步时间，升序，并且获取前5条
            long currentTime = System.currentTimeMillis() / 1000;
            List<Order> unloadOrderList = orderDao.queryBuilder()
                    .where(OrderDao.Properties.Upload_state.notEq(1), OrderDao.Properties.Syn_time.lt(currentTime - 60)).list();// limit 取完所有的，然后排序，然后拿前5条

            UnLoadOrderListBean unLoadOrderListBean = new UnLoadOrderListBean();
            if (unloadOrderList != null && unloadOrderList.size() > 0) {
                // 封装订单列表容器
                List<OrderItemBean> list = new ArrayList<>();
                for (Order order : unloadOrderList) {
                    // 生成订单容器
                    OrderItemBean orderItemBean = new OrderItemBean();
                    // 设置订单
                    orderItemBean.setOrder(order);
                    // 设置清单
                    List<OrderDetail> orderDetails = orderDetailDao.queryBuilder().where(OrderDetailDao.Properties.Order_id.eq(order.getOrder_id())).build().list();
                    orderItemBean.setOrderDetails(orderDetails);
                    // 添加订单容器
                    list.add(orderItemBean);
                }
                unLoadOrderListBean.setRequestList(list);
            }
            // 保存未上传的订单列表，上传成功后，修改状态用
            unLoadOrderListBean.setUnLoadOrderList(unloadOrderList);

            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (unLoadOrderListBean.getUnLoadOrderList() != null && unLoadOrderListBean.getUnLoadOrderList().size() > 0)
                    // 成功
                    listener.onSuccess(unLoadOrderListBean);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 更新订单列表，更新编辑时间、上传状态
     *
     * @param upload_state 上传状态（必传），0：未上传，1：已上传，2：无库存，-1代表不更新
     * @param synTime      同步时间（必传）
     */
    public static void updateOrderList(List<Order> unloadOrderList, int upload_state, long synTime) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            try {
                OrderDao orderDao = daoSession.getOrderDao();
                // 修改状态
                for (Order order : unloadOrderList) {
                    // 设置上传状态，0：未上传，1：已上传，2：无库存，-1代表不更新
                    if (upload_state != -1)
                        order.setUpload_state(upload_state);
                    // 设置同步时间
                    order.setSyn_time(synTime);
                }
                // 更新
                orderDao.updateInTx(unloadOrderList);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    /**
     * 异步获取商品，成功的回调在主线程
     *
     * @param order_id 订单id
     */
    public static void getOrderListByOrderId(BaseActivity activity, String order_id, OnDaoResultListListener<Order> listener) {
        activity.showLoadDataAnim();
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            OrderDao orderDao = daoSession.getOrderDao();
            List<Order> list = orderDao.queryBuilder().where(OrderDao.Properties.Order_id.eq(order_id)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 隐藏加载框
                activity.hideLoadDataAnim();
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 插入，或者替换商品
     *
     * @param goodsList 要插入的商品列表
     */
    public static void insertOrReplaceGoodsInChildThread(List<Goods> goodsList, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsDao goodsDao = daoSession.getGoodsDao();
            goodsDao.insertOrReplaceInTx(goodsList);
            // 回调
            // 成功
            listener.onSuccess(null);
        });
    }

    /**
     * 插入，或者替换三方商品
     *
     * @param threeGoodsList 要插入的三方商品列表
     */
    public static void insertOrReplaceThreeGoodsInChildThread(List<ThreeGoods> threeGoodsList, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            ThreeGoodsDao threeGoodsDao = daoSession.getThreeGoodsDao();
            threeGoodsDao.insertOrReplaceInTx(threeGoodsList);
            // 回调
            // 成功
            listener.onSuccess(null);
        });
    }

    /**
     * 是否是三方商品
     *
     * @param goodsCode 货号
     * @return true 代表是三方商品；
     */
    public static boolean isThreeGoods(String goodsCode) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 查询
        ThreeGoodsDao threeGoodsDao = daoSession.getThreeGoodsDao();
        return threeGoodsDao.load(goodsCode) != null;
    }

    /**
     * 插入，或者替换商品分类
     *
     * @param goodsList 要插入的商品分类列表
     */
    public static void insertOrReplaceGoodsClassInChildThread(List<GoodsClass> goodsList, OnDaoResultSuccessListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsClassDao goodsClassDao = daoSession.getGoodsClassDao();
            goodsClassDao.insertOrReplaceInTx(goodsList);
            // 回调
            // 成功
            listener.onSuccess(null);
        });
    }

    /**
     * 插入，或者替换商品条形码
     *
     * @param goodsBarCodes 要插入的商品条形码列表
     */
    public static void insertOrReplaceGoodsCodeInChildThread(List<GoodsBarCode> goodsBarCodes, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsBarCodeDao goodsBarCodeDao = daoSession.getGoodsBarCodeDao();
            goodsBarCodeDao.insertOrReplaceInTx(goodsBarCodes);
            // 回调
            // 成功
            listener.onSuccess(null);
        });
    }

    /**
     * 删除，或者插入
     *
     * @param users 要插入的用户列表
     */
    public static void deleteAndInsertUserInChildThread(List<User> users, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            UserDao userDao = daoSession.getUserDao();
            userDao.deleteAll();
            userDao.insertOrReplaceInTx(users);
            // 回调
            // 成功
            if (listener != null)
                listener.onSuccess(null);
        });
    }

    /**
     * 插入，或者替换对账
     *
     * @param account 要插入对账
     */
    public static void insertOrReplaceAccount(Account account, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            AccountDao accountDao = daoSession.getAccountDao();
            accountDao.insertOrReplaceInTx(account);
            // 回调
            // 成功
            if (listener != null)
                listener.onSuccess(null);
        });
    }

    /**
     * 获取参数
     */
    public static Params getParams() {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        ParamsDao paramsDao = daoSession.getParamsDao();
        Params load = paramsDao.load((long) 1);
        if (load != null) {
            // 有数据，返回
            return load;
        } else
            return new Params(1, "0", "0", "0", "", "", "0", null);
    }

    /**
     * 更新参数
     */
    public static void insertOrReplaceParams(Params params) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        ParamsDao paramsDao = daoSession.getParamsDao();
        paramsDao.insertOrReplace(params);
    }

    /**
     * 插入，或者替换挂单
     *
     * @param inputGoodsBeans 挂单对应的商品集合
     */
    public static void insertOrReplaceGuaDan(List<InputGoodsBean> inputGoodsBeans, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        long currentTimeMillis = System.currentTimeMillis();
        String guaDanId = DateUtils.getYearMonthDay(currentTimeMillis, "yyyyMMddHHmmss") + String.valueOf((int) ((Math.random() * 9 + 1) * 100000));// 随即生成的订单号,格式：20170321100850_704856
        //构建挂单bean
        GuaDan guaDan = new GuaDan(guaDanId, currentTimeMillis / 1000);
        // 循环为每个商品设置挂单id
        for (int i = 0; i < inputGoodsBeans.size(); i++) {
            InputGoodsBean inputGoodsBean = inputGoodsBeans.get(i);
            String uniqueId = DateUtils.getYearMonthDay(System.currentTimeMillis(), "yyyyMMddHHmmss") + String.valueOf((int) ((Math.random() * 9 + 1) * 100000));// 随即生成的订单号,格式：20170321100850_704856
            inputGoodsBean.setGuadan_id(guaDanId);
            inputGoodsBean.setUniqueId(uniqueId);
        }
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GuaDanDao guaDanDao = daoSession.getGuaDanDao();
            guaDanDao.insertInTx(guaDan);
            InputGoodsBeanDao inputGoodsBeanDao = daoSession.getInputGoodsBeanDao();
            inputGoodsBeanDao.insertInTx(inputGoodsBeans);
            // 回调
            // 成功
            if (listener != null)
                listener.onSuccess(null);
        });
    }

    /**
     * 获取挂单列表
     *
     * @param listener 返回挂单列表
     */
    public static void getGuaDanList(Activity activity, OnDaoResultListListener<GuaDan> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GuaDanDao guaDanDao = daoSession.getGuaDanDao();
            QueryBuilder<GuaDan> guaDanQueryBuilder = guaDanDao.queryBuilder().orderDesc(GuaDanDao.Properties.Addtime);
            List<GuaDan> list = guaDanQueryBuilder.list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 通过挂单id获取商品列表
     *
     * @param guadanId 挂单id
     * @param listener 回调
     */
    public static void getInputGoodsBeanListByGuaDanId(Activity activity, String guadanId, OnDaoResultListListener<InputGoodsBean> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            InputGoodsBeanDao inputGoodsBeanDao = daoSession.getInputGoodsBeanDao();
            List<InputGoodsBean> list = inputGoodsBeanDao.queryBuilder().where(InputGoodsBeanDao.Properties.Guadan_id.eq(guadanId)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 删除单个或多个挂单
     *
     * @param guaDans 挂单集合
     */
    public static void delGuadanAndInputGoodsList(Activity activity, List<GuaDan> guaDans, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            GuaDanDao guaDanDao = daoSession.getGuaDanDao();
            guaDanDao.deleteInTx(guaDans);

            for (int i = 0; i < guaDans.size(); i++) {
                getInputGoodsBeanListByGuaDanId(activity, guaDans.get(i).getId(), new OnDaoResultListListener<InputGoodsBean>() {
                    @Override
                    public void onSuccess(List<InputGoodsBean> list) {
                        delInputGoods(list);
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }

            // 成功
            activity.runOnUiThread(() -> {
                if (listener != null)
                    listener.onSuccess(null);
            });

        });
    }

    /**
     * 删除商品
     */
    public static void delInputGoods(List<InputGoodsBean> list) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            InputGoodsBeanDao inputGoodsBeanDao = daoSession.getInputGoodsBeanDao();
            inputGoodsBeanDao.deleteInTx(list);

            // 成功
            System.out.print("成功");

        });
    }


    /**
     * 获取商品的一级分类
     */
    public static void getGoodsClassOneTitleList(Activity activity, OnDaoResultListListener<GoodsClass> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsClassDao goodsClassDao = daoSession.getGoodsClassDao();
            List<GoodsClass> list = goodsClassDao.queryBuilder().where(GoodsClassDao.Properties.Pid.eq(0)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取商品的二级分类
     *
     * @param oneGroupId 一级分类id
     */
    public static void getGoodsClassTwoTitleList(Activity activity, long oneGroupId, OnDaoResultListListener<GoodsClass> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsClassDao goodsClassDao = daoSession.getGoodsClassDao();
            List<GoodsClass> list = goodsClassDao.queryBuilder().where(GoodsClassDao.Properties.Pid.eq(oneGroupId)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取商品的三级分类
     *
     * @param twoGroupId 二级分类id
     */
    public static void getGoodsClassThreeTitleList(Activity activity, long twoGroupId, OnDaoResultListListener<GoodsClass> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsClassDao goodsClassDao = daoSession.getGoodsClassDao();
            List<GoodsClass> list = goodsClassDao.queryBuilder().where(GoodsClassDao.Properties.Pid.eq(twoGroupId)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取商品的三级分类--根据一级id
     *
     * @param oneGroupId 一级分类id
     */
    public static void getGoodsClassThreeTitleListByOneGroupId(Activity activity, long oneGroupId, OnDaoResultListListener<GoodsClass> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询,Topid=oneGroupId && Pid!=oneGroupId
            GoodsClassDao goodsClassDao = daoSession.getGoodsClassDao();
            List<GoodsClass> list = goodsClassDao.queryBuilder()
                    .where(GoodsClassDao.Properties.Topid.eq(oneGroupId))
                    .where(GoodsClassDao.Properties.Pid.notEq(oneGroupId))
                    .build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取商品的一级分类
     */
    public static void getGoodsClassOneTitleListById(Activity activity, Set<Long> longs, OnDaoResultListListener<GoodsClass> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsClassDao goodsClassDao = daoSession.getGoodsClassDao();
            List<GoodsClass> list = goodsClassDao.queryBuilder().where(GoodsClassDao.Properties.Id.in(longs)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取商品的三级分类--根据一级id和三级分类id
     *
     * @param oneGroupId 一级分类id
     */
    public static void getGoodsClassThreeTitleListByOneThreeGroupId(Activity activity, List<Long> threeGroups, long oneGroupId, OnDaoResultListListener<GoodsClass> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询,Topid=oneGroupId && Pid!=oneGroupId
            GoodsClassDao goodsClassDao = daoSession.getGoodsClassDao();
            List<GoodsClass> list = goodsClassDao.queryBuilder()
                    .where(GoodsClassDao.Properties.Topid.eq(oneGroupId))
                    .where(GoodsClassDao.Properties.Pid.notEq(oneGroupId))
                    .where(GoodsClassDao.Properties.Id.in(threeGroups))
                    .build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取商品列表，根据商品分类
     *
     * @param classId    分类id
     * @param isFiltrate 是否过滤非标品 true：过滤 false：不过滤
     */
    public static void getGoodsListByGoodsClass(BaseActivity activity, long classId, boolean isFiltrate, OnDaoResultListListener<Goods> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsDao goodsDao = daoSession.getGoodsDao();
            QueryBuilder<Goods> goodsQueryBuilder = goodsDao.queryBuilder();
            // 判断是否过滤
            if (isFiltrate)
                goodsQueryBuilder.where(GoodsDao.Properties.Standard.eq(2));

            List<Goods> list = goodsQueryBuilder.where(GoodsDao.Properties.Groupid3.eq(classId)).build().list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 商品列表筛选
     *
     * @param code     商品货号
     * @param groupId1 一级分类id 当为-1时不筛选
     * @param groupId2 二级分类id 当为-1时不筛选
     * @param groupId3 三级分类id 当为-1时不筛选
     * @param standard 商品类型 当为-1时不筛选  其中1：标品；2:非标品
     * @param listener 商品查询回调
     */

    public static void getGoodsListBySelection(Activity activity, String code, long groupId1, long groupId2, long groupId3, int standard, OnDaoResultListListener<Goods> listener) {
        LogUtil.e("参数==code" + code + " groupId1==" + groupId1 + " groupId2==" + groupId2 + " groupId3==" + groupId3 + " standard==" + standard);
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsDao goodsDao = daoSession.getGoodsDao();
            GoodsBarCodeDao goodsBarCodeDao = daoSession.getGoodsBarCodeDao();
            QueryBuilder<Goods> goodsQueryBuilder = goodsDao.queryBuilder();
            List<Goods> list;
            if (!TextUtils.isEmpty(code)) {
                // 商品号不为空 模糊查询
                // 通过条形码获取货号
                GoodsBarCode goodsBarCode = goodsBarCodeDao.load(code);
                if (goodsBarCode != null) {
                    // 有货号，直接获取
                    list = goodsQueryBuilder.where(GoodsDao.Properties.Code.eq(goodsBarCode.getGoods_code())).build().list();
                } else {
                    // 没有货号，模糊搜索货号和商品名字
                    list = goodsQueryBuilder.whereOr(GoodsDao.Properties.Code.like("%" + code + "%"), GoodsDao.Properties.Title.like("%" + code + "%")).build().list();
                }
            } else {
                // 商品号为空，过滤后面的参数
                if (groupId1 != -1)// 过滤一级
                    goodsQueryBuilder = goodsQueryBuilder.where(GoodsDao.Properties.Groupid1.eq(groupId1));
                if (groupId2 != -1)// 过滤二级
                    goodsQueryBuilder = goodsQueryBuilder.where(GoodsDao.Properties.Groupid2.eq(groupId2));
                if (groupId3 != -1)// 过滤三级
                    goodsQueryBuilder = goodsQueryBuilder.where(GoodsDao.Properties.Groupid3.eq(groupId3));
                if (standard != -1)// 过滤商品类型
                    goodsQueryBuilder = goodsQueryBuilder.where(GoodsDao.Properties.Standard.eq(standard));
//                list = goodsQueryBuilder.limit(20).offset((page - 1) * 20).build().list();
                list = goodsQueryBuilder.build().list();
            }

            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 验证登录
     */
    public static void login(Activity activity, String account, String password, OnDaoResultListener<User> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        String mPassword = MD5Utils.GetMD5Code(password);
        daoSession.startAsyncSession().runInTx(() -> {
            UserDao userDao = daoSession.getUserDao();
            QueryBuilder queryBuilder = userDao.queryBuilder();
            queryBuilder.whereOr(UserDao.Properties.Uid.eq(account), UserDao.Properties.Phone.eq(account));
            User queryUser = (User) queryBuilder.build().unique();
            if (queryUser == null) {
                activity.runOnUiThread(() -> {
                    listener.onFailed();
                    ToastManager.showShortToast(activity.getApplicationContext(), "用户不存在");
                });
            } else {
                if ((account.equals(queryUser.getPhone()) || account.equals(queryUser.getUid())) && mPassword.equals(queryUser.getPassword())) {

                    activity.runOnUiThread(() -> {
                        // 回调
                        listener.onSuccess(queryUser);
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        listener.onFailed();
                        ToastManager.showShortToast(activity.getApplicationContext(), "账号密码错误");
                    });

                }
            }

        });
    }

    /**
     * 更新用户账号密码
     *
     * @param uid         用户id
     * @param newPassword 未加密的新密码
     */
    public static void updateUserPassword(Activity activity, String uid, String newPassword, OnDaoResultListener<User> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        daoSession.startAsyncSession().runInTx(() -> {
            UserDao userDao = daoSession.getUserDao();
            String mNewPassword = MD5Utils.GetMD5Code(newPassword);
            User queryUser = userDao.load(String.valueOf(uid));
            if (queryUser != null) {
                queryUser.setPassword(mNewPassword);
                userDao.update(queryUser);
                activity.runOnUiThread(() -> {
                    if (listener != null) {
                        listener.onSuccess(queryUser);
                    }
                });
            } else {
                activity.runOnUiThread(() -> {
                    if (listener != null) {
                        listener.onFailed();
                    }
                });
            }
        });
    }

    /**
     * 检查支付状态的订单是否有未上传的状态
     *
     * @param listener 返回true，代表有未上传订单
     */
    public static void checkPayOrderHasNoUpload(Activity activity, OnDaoResultSuccessListener<Boolean> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        daoSession.startAsyncSession().runInTx(() -> {
            OrderDao orderDao = daoSession.getOrderDao();
            List<Order> list = orderDao.queryBuilder()
                    .where(OrderDao.Properties.State.eq(1), OrderDao.Properties.Upload_state.notEq(1))
                    .limit(1).list();
            activity.runOnUiThread(() -> listener.onSuccess(list != null && list.size() > 0));// 有数据，返回true
        });
    }

    /**
     * 根据uid获取用户信息
     *
     * @param uid 用户id
     */
    public static void getUserByUserId(Activity activity, String uid, OnDaoResultListener<User> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            UserDao userDao = daoSession.getUserDao();
            User user = userDao.load(uid);
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (user != null)
                    // 成功
                    listener.onSuccess(user);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取用户列表
     *
     * @param listener 返回所有用户列表
     */
    public static void getUserList(Activity activity, OnDaoResultListListener<User> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            UserDao userDao = daoSession.getUserDao();
            List<User> users = userDao.loadAll();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (users != null && users.size() > 0)
                    // 成功
                    listener.onSuccess(users);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取用户列表
     */
    public static List<User> getUserList() {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 查询
        UserDao userDao = daoSession.getUserDao();
        return userDao.loadAll();
    }

    /**
     * 获取最后一次对账的时间，如果没有对账信息，则使用第一笔订单获取下单时间
     *
     * @param listener 成功会返回时间，单位：秒
     */
    public static void getLastAccountTime(Activity activity, OnDaoResultSuccessListener<Long> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            AccountDao accountDao = daoSession.getAccountDao();
            List<Account> list = accountDao.queryBuilder().orderDesc(AccountDao.Properties.Endtime).list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0) {
                    // 成功
                    if (list.get(0) != null) {
                        listener.onSuccess(list.get(0).getEndtime());
                    } else {
                        getFirstOrderTime(activity, listener);
                    }
                } else {
                    // 失败
                    getFirstOrderTime(activity, listener);
                }

            });
        });
    }

    /**
     * 获得第一笔订单下单时间，如果没有订单返回当前时间
     *
     * @param listener 成功会返回时间，单位：秒
     */
    public static void getFirstOrderTime(Activity activity, OnDaoResultSuccessListener<Long> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            OrderDao orderDao = daoSession.getOrderDao();
            List<Order> list = orderDao.queryBuilder().orderAsc(OrderDao.Properties.Addtime).list();
            // 通知
            activity.runOnUiThread(() -> {
                if (list != null && list.size() > 0) {
                    // 成功
                    if (list.get(0) != null) {
                        listener.onSuccess(list.get(0).getAddtime());
                    } else {
                        // 返回当前0点时间
                        long current = System.currentTimeMillis();
                        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
                        // 失败
                        listener.onSuccess(zero / 1000);
                    }

                } else {
                    // 返回当前0点时间
                    long current = System.currentTimeMillis();
                    long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
                    // 失败
                    listener.onSuccess(zero / 1000);
                }

            });
        });
    }

    /**
     * 异步获取商品，成功的回调在主线程
     *
     * @param startTime （必传）开始时间
     * @param endTime   （必传）结束时间
     */
    public static void getOrderListByTime(Activity activity, long startTime, long endTime, OnDaoResultListListener<Order> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            OrderDao orderDao = daoSession.getOrderDao();
            List<Order> list = orderDao.queryBuilder().where(OrderDao.Properties.Addtime.between(startTime, endTime)).orderDesc(OrderDao.Properties.Addtime).list();

            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取对账列表
     *
     * @param startTime 开始时间 -1时不过滤
     * @param endTime   结束时间  -1时不过滤
     * @param page      分页
     * @param uids      用户id集合 为空/大小为0不过滤
     * @param listener  返回对账列表
     */
    public static void getAccountList(Activity activity, long startTime, long endTime, int page, List<String> uids, OnDaoResultListListener<Account> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            AccountDao accountDao = daoSession.getAccountDao();
            QueryBuilder<Account> accountQueryBuilder = accountDao.queryBuilder();
            // 订单号为null，过滤后面的参数
            if (startTime != -1)// 过滤开始时间
                accountQueryBuilder = accountQueryBuilder.where(AccountDao.Properties.Endtime.ge(startTime));
            if (endTime != -1)// 过滤结束时间
                accountQueryBuilder = accountQueryBuilder.where(AccountDao.Properties.Endtime.le(endTime));
            if (uids != null && uids.size() > 0)// 过滤用户
                for (int i = 0; i < uids.size(); i++) {
                    accountQueryBuilder = accountQueryBuilder.where(AccountDao.Properties.Uid.in(uids));
                }
            List<Account> list = accountQueryBuilder.limit(20).offset((page - 1) * 20).build().list();
            // 通知
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 插入
     *
     * @param goods 要插入的常用商品
     */
    public static void insertOrReplaceCommonGoodsList(List<Goods> goods, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            CommonGoodsDao commonGoodsDao = daoSession.getCommonGoodsDao();
            List<CommonGoods> commonGoods = new ArrayList<>();
            long addTime = System.currentTimeMillis() / 1000;// 添加时间
            long editTime = addTime;// 编辑时间
            for (int i = 0; i < goods.size(); i++) {
                int standard = goods.get(i).getStandard();// 是否是标品:1：标品；2:非标品
                String goodsCode = goods.get(i).getCode();// 商品货号
                editTime++;
                CommonGoods commonGood = new CommonGoods(addTime, editTime, standard, false, goodsCode);
                commonGoods.add(commonGood);
            }
            // 插入
            commonGoodsDao.insertOrReplaceInTx(commonGoods);
            // 回调
            // 成功
            if (listener != null)
                listener.onSuccess(null);
        });
    }

    /**
     * 插入
     *
     * @param commonGoods 要插入的常用商品
     * @param isTop       true:置顶 false:置底
     */
    public static void insertOrReplaceCommonGoodsList(List<CommonGoods> commonGoods, boolean isTop, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            CommonGoodsDao commonGoodsDao = daoSession.getCommonGoodsDao();
            long addTime = System.currentTimeMillis();
            for (int i = 0; i < commonGoods.size(); i++) {
                addTime++;
                commonGoods.get(i).setEditTime(isTop ? addTime : -addTime);
            }
            // 插入
            commonGoodsDao.insertOrReplaceInTx(commonGoods);
            // 回调
            // 成功
            if (listener != null)
                listener.onSuccess(null);
        });
    }

    /**
     * 更新
     *
     * @param listener 回调
     */
    public static void updateCommonGoodsList(List<CommonGoods> list, long fromTime, long toTime, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            CommonGoodsDao commonGoodsDao = daoSession.getCommonGoodsDao();

            CommonGoods commonGoods1 = list.get(0);
            CommonGoods commonGoods2 = list.get(1);
            commonGoods1.setEditTime(toTime);
            commonGoods2.setEditTime(fromTime);
            LogUtil.e("=========" + commonGoods1.getEditTime() + "==" + commonGoods2.getEditTime());
            // 更新
            commonGoodsDao.updateInTx(list);
            // 回调
            // 成功
            if (listener != null)
                listener.onSuccess(null);
        });
    }

    /**
     * 删除常用商品
     *
     * @param commonGoods 要删除的常用商品
     */
    public static void deleteCommonGoodsList(List<CommonGoods> commonGoods, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            CommonGoodsDao commonGoodsDao = daoSession.getCommonGoodsDao();
            // 插入
            commonGoodsDao.deleteInTx(commonGoods);
            // 回调
            // 成功
            if (listener != null)
                listener.onSuccess(null);
        });
    }

    /**
     * 获取常用商品列表
     *
     * @param code     商品编号
     * @param standard 当是-1时不筛选 1：标品；2:非标品
     * @param listener 返回常用商品列表
     */
    public static void getCommonGoodsList(Activity activity, String code, int standard, OnDaoResultListListener<CommonGoods> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            CommonGoodsDao commonGoodsDao = daoSession.getCommonGoodsDao();
            QueryBuilder<CommonGoods> commonGoodsQueryBuilder = commonGoodsDao.queryBuilder();
            if (!TextUtils.isEmpty(code)) {
                // 按照商品编号筛选
                commonGoodsQueryBuilder = commonGoodsQueryBuilder.where(CommonGoodsDao.Properties.GoodsCode.eq(code));
            } else {
                if (standard != -1)
                    // 过滤标品和非标品
                    commonGoodsQueryBuilder = commonGoodsQueryBuilder.where(CommonGoodsDao.Properties.Standard.eq(standard));
            }
            // 常用商品列表
            List<CommonGoods> commonGoods = commonGoodsQueryBuilder.orderDesc(CommonGoodsDao.Properties.EditTime).list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (commonGoods != null && commonGoods.size() > 0)
                    // 成功
                    listener.onSuccess(commonGoods);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 模糊检索商品
     *
     * @param codeTitle 商品名和编号
     * @param listener  返回检索商品列表
     */
    public static void getGoodsListByCodeTitle(Activity activity, String codeTitle, OnDaoResultListListener<Goods> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            GoodsDao goodsDao = daoSession.getGoodsDao();
            QueryBuilder<Goods> goodsQueryBuilder = goodsDao.queryBuilder();
            goodsQueryBuilder = goodsQueryBuilder.whereOr(GoodsDao.Properties.Code.like("%" + codeTitle + "%"), GoodsDao.Properties.Title.like("%" + codeTitle + "%"));
            List<Goods> list = goodsQueryBuilder.list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0)
                    // 成功
                    listener.onSuccess(list);
                else
                    // 失败
                    listener.onFailed();
            });
        });
    }

    /**
     * 获取商品条形码根据商品货号
     *
     * @param goodsCode 商品货号
     * @param listener  成功返回，第一个条形码
     */
    public static void getGoodBarCodeByGoodsCode(Activity activity, String goodsCode, OnDaoResultListener<String> listener) {
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 查询
            GoodsBarCodeDao goodsBarCodeDao = daoSession.getGoodsBarCodeDao();

            List<GoodsBarCode> list = goodsBarCodeDao.queryBuilder().where(GoodsBarCodeDao.Properties.Goods_code.eq(goodsCode)).list();
            // 通知成功
            activity.runOnUiThread(() -> {
                // 回调
                if (list != null && list.size() > 0) {
                    // 成功
                    listener.onSuccess(list.get(0).getCode());
                } else {
                    // 失败
                    listener.onFailed();
                }

            });
        });
    }

    /**
     * 更新清单，退款数量
     *
     * @param returnOrderItemBean 封装的退款订单
     */
    public static void updateOrderDetailReturnNumByReturnOrderItemBean(Activity activity, OrderItemBean returnOrderItemBean, OnDaoResultListener<String> listener) {
        if (returnOrderItemBean == null) {
            // 失败
            listener.onFailed();
            return;
        }
        DaoSession daoSession = ZhaoLinApplication.getInstance().getDaoSession();
        // 异步操作
        daoSession.startAsyncSession().runInTx(() -> {
            // 添加订单表、清单表
            OrderDetailDao orderDetailDao = daoSession.getOrderDetailDao();
            String source_order_id = returnOrderItemBean.getOrder().getSource_order_id();// 原始订单id
            // 清单表，通过 原始订单id、货号、价格找到退款的商品
            // 添加清单表
            List<OrderDetail> orderDetails = returnOrderItemBean.getOrderDetails();
            for (OrderDetail returnOrderDetail : orderDetails) {
                // 通过 原始订单id、货号、价格找到退款的商品
                List<OrderDetail> list = orderDetailDao.queryBuilder().where(OrderDetailDao.Properties.Order_id.eq(source_order_id),
                        OrderDetailDao.Properties.Code.eq(returnOrderDetail.getCode()),
                        OrderDetailDao.Properties.Price.eq(returnOrderDetail.getPrice())).list();
                // 获取此商品退的数量
                double returnNum = returnOrderDetail.getNum();
                if (list != null && list.size() > 0) {
                    // 数据正确，有此清单
                    // 修改清单表的 return_num
                    for (OrderDetail currentSourceOrderDetail : list) {
                        if (returnNum > 0) {
                            // 退款数量未分配完成，继续分配
                            double currentSourceReturnNum = currentSourceOrderDetail.getReturn_num();// 原始的退货数量
                            if (returnNum <= currentSourceReturnNum) {
                                // 退货数量小于原清单的可退货数量，全部退货
                                currentSourceOrderDetail.setReturn_num(NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(currentSourceReturnNum - returnNum)));
                                orderDetailDao.update(currentSourceOrderDetail);
                                // 已经全部退了，退出此循环
                                returnNum = 0;
                            } else {
                                // 退货数量大于原清单的可退货数量，部分退货
                                currentSourceOrderDetail.setReturn_num(0);
                                orderDetailDao.update(currentSourceOrderDetail);
                                // 已经全部退了，退出此循环
                                returnNum = returnNum - currentSourceReturnNum;
                            }
                        }
                        // else 退款数量已分配完成
                    }
                    if (returnNum != 0)
                        // 此清单退货有问题
                        LogUtil.e("修改退货数量出错；订单id：" + source_order_id + "； 货号：" + returnOrderDetail.getCode() + "； 价格：" + returnOrderDetail.getPrice());
                } else {
                    // 数据错误，待处理
                    LogUtil.e("查询清单出错；订单id：" + source_order_id + "； 货号：" + returnOrderDetail.getCode() + "； 价格：" + returnOrderDetail.getPrice());
                }

            }
//            orderDetailDao
            orderDetailDao.insertOrReplaceInTx(returnOrderItemBean.getOrderDetails());
            // 通知成功
            activity.runOnUiThread(() -> {
                // 成功
                listener.onSuccess(null);
            });
        });
    }

    /**
     * 结果是个列表
     */
    public interface OnDaoResultListListener<T> {
        void onSuccess(List<T> list);

        void onFailed();
    }

    /**
     * 结果是一个
     */
    public interface OnDaoResultListener<T> {
        void onSuccess(T bean);

        void onFailed();
    }

    /**
     * 结果只有成功
     */
    public interface OnDaoResultSuccessListener<T> {
        void onSuccess(T bean);
    }
}
