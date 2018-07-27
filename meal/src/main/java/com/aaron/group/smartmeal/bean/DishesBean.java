package com.aaron.group.smartmeal.bean;

import com.aaron.group.smartmeal.base.BaseSugarBean;
import com.orm.dsl.Column;

/**
 * 说明: 菜品数据库实体

 */

public class DishesBean extends BaseSugarBean {

    public DishesBean(){}
    /**
     * 菜品ID
     */
    @Column(name = "dishesId", unique = true)
    public int dishesId;

    /**
     * 类别ID
     */
    @Column(name = "categoryId")
    public int categoryId;
    /**
     * 菜品名称
     */
    public String dishesName;
    /**
     * 菜品描述
     */
    public String dishesDesc;
    /**
     * 菜品状态
     * 0：可用
     * 1：不可用
     */
    @Column(name = "dishesStatus")
    public int dishesStatus;
    /**
     * 菜品价格
     */
    public String dishesPrice;
    /**
     * 菜品图片
     */
    public String dishesImg;
}
