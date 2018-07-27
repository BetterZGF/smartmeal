package com.aaron.group.smartmeal.bean;

import com.aaron.group.smartmeal.base.BaseSugarBean;
import com.orm.dsl.Column;

/**
 * 说明: 菜品类别数据库实体

 */

public class DishesCategoryBean extends BaseSugarBean {

    public DishesCategoryBean(){}
    /**
     * 类别ID
     */
    @Column(name = "categoryId", unique = true)
    public int categoryId;
    /**
     * 类别名称
     */
    public String categoryName;
    /**
     * 类别描述
     */
    public String categoryDesc;
    /**
     * 类别状态
     * 0：可用
     * 1：不可用
     */
    @Column(name = "categoryStatus")
    public int categoryStatus;
}
