package com.aaron.group.smartmeal.base;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * 说明: 继承SugarRecord，表明是基于Sugar的数据库处理实体

 */

public class BaseSugarBean extends SugarRecord implements Serializable {

    /**
     * 接口调用返回说明
     */
    public String msg;

    /**
     * 接口调用返回码
     */
    public int retCode;
}
