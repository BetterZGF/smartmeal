package com.aaron.group.smartmeal.eventbus;

/**
 * 说明: 封装eventbus发送消息的类，
 * 该类的消息不带内容，只发送指令

 */

public class EventBusMsg {
    /**
     * 指令类别
     */
    private EventBusType type;

    public EventBusMsg(EventBusType type) {
        this.type = type;
    }

    public EventBusType getType() {
        return type;
    }

    public void setType(EventBusType type) {
        this.type = type;
    }

    /**
     * 采用枚举设置指令数据
     */
    public enum EventBusType
    {
        /**
         * 检测手机网络状态
         */
        NET_CHECK,
        /**
         * 下单界面获取不了数据
         */
        POP_ORDER_NODATA,
        /**
         * 下单界面取消
         */
        POP_ORDER_CANCEL,
        /**
         * 用户去登录
         */
        INFO_TO_LOGIN,
        /**
         *  取消登录
         */
        INFO_LOGIN_CANCEL,
        /**
         *  设置用餐人数为空
         */
        INFO_SETTING_PEOPLE_NULL,
        INFO_REGISTER_CANCEL,
        PAY_BALANCE_ERROR,
        INFO_MODIFY_CANCEL,
        LOGOUT,
        TO_HOME,
        ORDERS_REFLUSH,
        INFO_COMMENT_CANCEL,
        INFO_COMMENT_SUCCESS,
        POP_ORDERINFO_CANCEL
        ;
    }
}
