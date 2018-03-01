package com.xxzlkj.zhaolinshouyin.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xxzlkj.zhaolinshouyin.db.DaoMaster;
import com.xxzlkj.zhaolinshouyin.db.OrderDetailDao;
import com.xxzlkj.zhaolinshouyin.db.ThreeGoodsDao;

import org.greenrobot.greendao.database.Database;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/23 11:59
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        System.out.println("onUpgrade" + "==oldVersion==" + oldVersion + "==newVersion==" + newVersion);
        for (int i = oldVersion + 1; i <= newVersion; i++) {
            switch (i) {
                case 2:
                    // 新版本2
                    // order 表增加 double discount_price;// 未优惠（优惠前）金额、 String coupon;// 优惠券、String members_code;// 会员码
                    // OrderDetail 表增加 double discount_pre_price;// 未优惠（优惠前）金额
                    db.execSQL("alter table 'order' add 'discount_pre_price' double default 0");// 未优惠（优惠前）金额
                    db.execSQL("alter table 'order' add 'coupon' text");// 优惠券
                    db.execSQL("alter table 'order' add 'members_code' text");// 会员码
                    db.execSQL("alter table 'order_detail' add 'discount_pre_price' double default 0");// 未优惠（优惠前）金额
                    break;
                case 3:
                    // 新版本3
                    // 更新版本2时，处理订单表、清单表discount_pre_price为0的情况
                    db.execSQL("update 'order' set discount_pre_price = price where discount_pre_price = 0");// 未优惠（优惠前）金额
                    db.execSQL("update 'order_detail' set discount_pre_price = price where discount_pre_price = 0");// 未优惠（优惠前）金额
                    // OrderDetail 表增加 int return_state;// 退款状态:0:可退；非0不可退
                    db.execSQL("alter table 'order_detail' add 'return_state' int default 0");// 退款状态:0:可退；非0不可退
                    // 添加退款，order 表增加  source_order_id;// 原始订单id，退款订单关联原始订单表
                    db.execSQL("alter table 'order' add 'source_order_id' text");// 优惠券

                    // 清单表重新的创建，因为要新增主键，主键不能 alter 添加
                    db.execSQL("create table order_detail_temp as select * from order_detail");// 复制一个临时表
                    OrderDetailDao.dropTable(db, false);// 删除原表
                    OrderDetailDao.createTable(db, false);// 创建新表
                    db.execSQL("replace into order_detail(order_id,title,prices,prices,price,code,goods_id,num,return_num,standard,discount_pre_price,return_state) select order_id,title,prices,prices,price,code,goods_id,num,return_num,standard,discount_pre_price,return_state from order_detail_temp");// 临时表数据复制到新表
                    db.execSQL("drop table order_detail_temp");// 删除临时表

                    // 新增加了一个三方商品表
                    ThreeGoodsDao.createTable(db, false);// 创建新表
                    break;
                case 4:
                    // 新版本4
                    // 创建一个表
                    // User2Dao.createTable(db, true);
                    break;
            }
        }
    }
}
