package com.aaron.group.smartmeal.bean;

import com.aaron.group.smartmeal.base.BaseSugarBean;
import com.orm.dsl.Column;

/**
 * 说明: 用户数据库实体

 */

public class AccountBean extends BaseSugarBean {

    public AccountBean(){}
    /**
     * 用户ID
     */
    @Column(name = "userId", unique = true)
    public int userId;
    /**
     * 用户名称
     */
    @Column(name = "userName", unique = true)
    public String userName;
    /**
     * 真实名称
     */
    public String realName;
    /**
     * 用户性别
     */
    public String userSex;
    /**
     * 用户密码
     */
    public String userPsw;
    /**
     * 用户状态
     * 0：可用
     * 1：不可用
     */
    public int userStatus;
    /**
     * 用户等级
     */
    public String userLevel;
    /**
     * 用户头像
     */
    public String userLogo;

    /**
     * 账户余额
     */
    public float balance;
    /**
     * 用户手机
     */
    @Column(name = "userPhone", unique = true)
    public String moblie;
}
