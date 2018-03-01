package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class PrintAdapter extends BaseAdapter<OrderDetail> {

    private int state;

    public PrintAdapter(Context context, int itemId) {
        super(context, itemId);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, OrderDetail itemBean) {
        TextView tv_title = holder.getView(R.id.tv_title);// 标题
        TextView tv_price = holder.getView(R.id.tv_price);// 价格
        TextView tv_num = holder.getView(R.id.tv_num);// 数量
        TextView tv_all_price = holder.getView(R.id.tv_all_price);// 小计
        // 设置数据
        double discount_pre_price = itemBean.getDiscount_pre_price();// 商品未优惠前的价格
        if (discount_pre_price == itemBean.getPrices()) {
            // 原价和优惠前的价格相同，没修改价格
            tv_title.setText(itemBean.getTitle());// 标题
        } else {
            // 修改了价格，展示原价
            tv_title.setText(String.format("%s\n原价：%s", itemBean.getTitle(), StringUtil.saveTwoDecimal(itemBean.getPrices())));// 标题
        }
        // 流水类型： 1：消费 2：退款
        tv_price.setText(StringUtil.saveTwoDecimal(discount_pre_price));// 价格
        tv_num.setText(StringUtil.saveThreeDecimal(itemBean.getNum()));// 数量
        tv_all_price.setText(String.format(state == 1 ? "%s" : "-%s", StringUtil.saveTwoDecimal(discount_pre_price * itemBean.getNum())));// 小计
    }

    public void setState(int state) {
        this.state = state;
    }
}
