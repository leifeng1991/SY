package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述: 商品分类表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class GoodsClass {
    @Id
    private long id;// 分类id
    private long pid;// 父级id
    private long topid;// 顶级id
    private String title;// 分类名
    @Generated(hash = 974724227)
    public GoodsClass(long id, long pid, long topid, String title) {
        this.id = id;
        this.pid = pid;
        this.topid = topid;
        this.title = title;
    }
    @Generated(hash = 563038460)
    public GoodsClass() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getPid() {
        return this.pid;
    }
    public void setPid(long pid) {
        this.pid = pid;
    }
    public long getTopid() {
        return this.topid;
    }
    public void setTopid(long topid) {
        this.topid = topid;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
   

}
