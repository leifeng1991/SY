package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

/**
 * 描述:常用商品
 *
 * @author leifeng
 *         2018/1/3 11:39
 */

@Entity
public class CommonGoods {
    private long addTime;// 添加时间
    private long editTime;// 编辑时间
    private int standard;// 是否是标品:1：标品；2:非标品
    private boolean isChecked;//是否选中
    @Id
    private String goodsCode;// 商品货号
    @ToOne(joinProperty = "goodsCode")
    private Goods goods;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1666664736)
    private transient CommonGoodsDao myDao;

    @Generated(hash = 868422414)
    public CommonGoods(long addTime, long editTime, int standard, boolean isChecked,
            String goodsCode) {
        this.addTime = addTime;
        this.editTime = editTime;
        this.standard = standard;
        this.isChecked = isChecked;
        this.goodsCode = goodsCode;
    }

    @Generated(hash = 1614438756)
    public CommonGoods() {
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getGoodsCode() {
        return this.goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    @Generated(hash = 1339616396)
    private transient String goods__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 212958642)
    public Goods getGoods() {
        String __key = this.goodsCode;
        if (goods__resolvedKey == null || goods__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GoodsDao targetDao = daoSession.getGoodsDao();
            Goods goodsNew = targetDao.load(__key);
            synchronized (this) {
                goods = goodsNew;
                goods__resolvedKey = __key;
            }
        }
        return goods;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 547528369)
    public void setGoods(Goods goods) {
        synchronized (this) {
            this.goods = goods;
            goodsCode = goods == null ? null : goods.getCode();
            goods__resolvedKey = goodsCode;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getStandard() {
        return this.standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public long getEditTime() {
        return this.editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 766040939)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCommonGoodsDao() : null;
    }

}
