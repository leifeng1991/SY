package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述: 挂单表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class GuaDan {
    @Id
    private String id;// 挂单id
    private long addtime;// 订单增加时间
    @Generated(hash = 1247682471)
    public GuaDan(String id, long addtime) {
        this.id = id;
        this.addtime = addtime;
    }
    @Generated(hash = 1220801167)
    public GuaDan() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public long getAddtime() {
        return this.addtime;
    }
    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

}
