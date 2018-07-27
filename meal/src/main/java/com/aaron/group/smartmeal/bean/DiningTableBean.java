package com.aaron.group.smartmeal.bean;

import com.aaron.group.smartmeal.base.BaseBean;
import com.aaron.group.smartmeal.base.BaseSugarBean;
import com.orm.dsl.Column;

/**
 * 说明: 餐桌数据库实体类

 */

public class DiningTableBean extends BaseBean {

    /**
     * 餐桌ID
     */
    public int diningTableId;

    /**
     * 餐桌no.
     */
    public String diningTableNo;

    /**
     * 餐桌可容纳人数.
     */
    public int diningTableToatalNums;

    /**
     * 餐桌已坐人数.
     */
    public int diningTableUsedNums;

    /**
     * 餐桌状态.
     *  0：可用
     *  1：不可用
     */
    public int diningTableStatus;
}
