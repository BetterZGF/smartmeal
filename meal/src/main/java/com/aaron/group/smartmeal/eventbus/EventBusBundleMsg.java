package com.aaron.group.smartmeal.eventbus;

import android.os.Bundle;

/**
 * 说明:封装eventbus发送消息的类，
 * 该类的消息携带带内容，发送指令并发送内容

 */

public class EventBusBundleMsg {

    private EventBusType type;
    private Bundle bundle;
    public EventBusBundleMsg(EventBusType type, Bundle bundle) {
        this.type = type;
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
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
        BUNDLE_TO_ORDER,
        BUNDLE_LOGIN_ERROR,
        BUNDLE_LOGIN_SUCCESS,
        BUNDLE_SETTING_PEOPLE,
        BUNDLE_REGISTER_ERROR,
        BUNDLE_REGISTER_SUCCESS,
        BUNDLE_PAY,
        BUNDLE_MODIFY_SUCCESS,
        BUNDLE_MODIFY_ERROR
        ;
    }
}
