package com.aaron.group.smartmeal.bean;

import com.aaron.group.smartmeal.base.BaseSugarBean;
import com.orm.dsl.Column;

/**
 * 说明:

 */

public class OrderDishesBean extends BaseSugarBean {

    /**
     * 订单ID
     */
    @Column(name = "orderId")
    public String orderId;
    /**
     * 菜品ID
     */
    @Column(name = "dishesId")
    public int dishesId;

    /**
     * 菜品数量
     */
    public int dishesNum;
}
