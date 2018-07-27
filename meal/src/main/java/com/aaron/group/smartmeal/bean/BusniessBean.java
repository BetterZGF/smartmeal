package com.aaron.group.smartmeal.bean;

import com.aaron.group.smartmeal.base.BaseBean;

/**
 * 说明:

 */

public class BusniessBean extends BaseBean {

    public BusniessInnerBean result;

    public class BusniessInnerBean extends BaseBean
    {
        public String k;
        public String v;
    }
}
