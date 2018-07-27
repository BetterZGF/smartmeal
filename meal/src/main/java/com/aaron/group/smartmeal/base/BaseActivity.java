package com.aaron.group.smartmeal.base;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.receiver.NetStatusReceiver;
import com.aaron.group.smartmeal.retrofit.RetrofitUtil;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.widget.ProgressPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Retrofit;

/**
 * 说明: activity基类，初始化公共组件资源
 *       动态监测手机网络状态

 */

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * application
     */
    public BaseApplication application;
    /**
     * 系统资源钩子
     */
    public Resources resources;
    /**
     * 系统窗口钩子
     */
    public WindowManager windowManager;
    /**
     * handler,
     * handler处理系统中的延时操作，不做消息转发
     */
    public Handler mHandler;
    /**
     * EventBus替代handler做消息转发
     */
    public EventBus eventBus;
    /**
     * 检测手机网络receiver
     */
    private NetStatusReceiver netStatusReceiver;
    /**
     * 自定义进度条
     */
    public ProgressPopupWindow progress;
    /**
     * 系统布局钩子
     */
    public LayoutInflater inflater;
    public Retrofit retrofit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (BaseApplication) BaseActivity.this.getApplication();
        resources = this.getResources();
        windowManager = this.getWindowManager();
        mHandler = new Handler();
        inflater = LayoutInflater.from(BaseActivity.this);
        eventBus = EventBus.getDefault();
        eventBus.register(BaseActivity.this);
        retrofit = RetrofitUtil.obtainRetrofit();
        progress = new ProgressPopupWindow(BaseActivity.this, BaseActivity.this);
        //禁止横屏
        BaseActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //监测网络
        netStatusReceiver = new NetStatusReceiver(eventBus);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        BaseActivity.this.registerReceiver(netStatusReceiver, filter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onBaseEvent(EventBusMsg msg)
    {
        switch (msg.getType())
        {
            case NET_CHECK:
            {
                Toast.makeText(BaseActivity.this, CommonUtils.obtainString(resources, R.string.phone_net_null), Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=progress)
        {
            progress.dismissView();
        }
        if(null!=eventBus)
        {
            eventBus.unregister(BaseActivity.this);
        }
        if(null!=netStatusReceiver)
        {
            this.unregisterReceiver(netStatusReceiver);
        }
    }

    public abstract void initTitle();
    public abstract void initContent();
}
