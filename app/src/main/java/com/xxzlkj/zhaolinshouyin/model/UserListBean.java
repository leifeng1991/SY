package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshouyin.db.User;

import java.util.List;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/9 15:28
 */


public class UserListBean extends BaseBean{

    private List<User> data;

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }

}
