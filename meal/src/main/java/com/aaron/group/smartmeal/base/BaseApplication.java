package com.aaron.group.smartmeal.base;

import android.app.Application;
import android.content.res.Configuration;

import com.aaron.group.smartmeal.retrofit.RetrofitUtil;
import com.aaron.group.smartmeal.utils.CrashHandler;
import com.orm.SugarContext;

/**
 * 说明: 全局application类，初始化全局变量

 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitUtil.init(getApplicationContext());
        //全局异常捕获处理
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        //数据库初始化，Sugar是一个易用的sqllite组件
        SugarContext.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
