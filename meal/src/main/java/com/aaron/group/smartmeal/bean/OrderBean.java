package com.aaron.group.smartmeal.bean;

import com.aaron.group.smartmeal.base.BaseSugarBean;
import com.orm.dsl.Column;

import java.util.List;

/**
 * 说明: 订单数据库实体

 */

public class OrderBean extends BaseSugarBean {

    public OrderBean(){}

    /**
     * 订单ID
     */
    @Column(name = "orderId", unique = true)
    public String orderId;
    /**
     * 用户ID
     */
    @Column(name = "userId")
    public int userId;
    /**
     * 餐桌ID
     */
    @Column(name = "diningTableId")
    public int diningTableId;
    /**
     * 下单时间
     */
    public String orderTime;
    /**
     * 结单时间
     */
    public String payTime;
    /**
     * 用餐人数
     */
    public int peoples;
    /**
     * 订单总价
     */
    public String orderPrice;
    /**
     * 订单状态
     *  0 未结单
     *  1 已结单
     */
    @Column(name = "orderStatus")
    public int orderStatus;

    public float rating;
    public String comment;
}
