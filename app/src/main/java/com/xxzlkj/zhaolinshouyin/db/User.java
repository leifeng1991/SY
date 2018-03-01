package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/9 14:51
 */

@Entity
public class User {
    @Id
    private String uid;
    @NotNull@Unique
    private String phone;
    private String name;
    @NotNull
    private String password;
    private int id;
    @Generated(hash = 713493703)
    public User(String uid, @NotNull String phone, String name,
            @NotNull String password, int id) {
        this.uid = uid;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.id = id;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

}
