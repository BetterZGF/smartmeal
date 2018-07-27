package com.aaron.group.smartmeal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 说明: 实时监测手机网络状态

 */

public class NetStatusReceiver extends BroadcastReceiver {

    private EventBus eventBus;

    public NetStatusReceiver(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            if(!CommonUtils.checkNet(context))
            {
                //没有网络，发出手机断网请求
                //采用eventbus替代handler处理消息转发
                if(null!=eventBus)
                {
                    eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.NET_CHECK));
                }
            }
        }
    }
}
