package com.aaron.group.smartmeal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

/**
 * 说明: 界面跳转工具类，集合了各种界面的跳转，包括
 *  界面之间传递数据，界面跳转动画等等

 */

public class ActivityUtils {

    /**
     * 采用单例模式，避免出现重复实例对象
     */
    private static class Holder
    {
        private static final ActivityUtils instance = new ActivityUtils();
    }

    private ActivityUtils()
    {

    }

    public static final ActivityUtils getInstance()
    {
        return Holder.instance;
    }

    public void showActivity(Activity aty, Class clazz)
    {
        Intent i = new Intent(aty, clazz);
        aty.startActivity(i);
    }

    public void showActivity(Activity aty , Class clazz , String key , Serializable serialize ){
        Intent i = new Intent(aty, clazz);
        i.putExtra(key, serialize);
        aty.startActivity(i);
    }

    public void showActivity(Activity aty , Class clazz , Bundle bundle ){
        Intent i = new Intent(aty, clazz);
        i.putExtras(bundle);
        aty.startActivity(i);
    }

    public void skipActivity(Activity aty, Class clazz)
    {
        Intent i = new Intent(aty, clazz);
        aty.startActivity(i);
        aty.finish();
    }

    public void skipActivity(Activity aty, Class clazz, Bundle bundle)
    {
        Intent i = new Intent(aty, clazz);
        i.putExtras(bundle);
        aty.startActivity(i);
        aty.finish();
    }

    public void closeSelf(Activity aty)
    {
        aty.finish();
    }

    public void showActivityForResult(Activity aty, int requestCode , Class clazz)
    {
        Intent i = new Intent(aty,clazz);
        aty.startActivityForResult(i, requestCode);
    }

    public void showActivity(Activity aty  ,int flags, Class clazz ){
        Intent i = new Intent(aty,clazz);
        i.setFlags(flags);
        aty.startActivity(i);
    }

    public void skipActivity(Activity aty  ,int flags, Class clazz ){
        Intent i = new Intent(aty,clazz);
        i.setFlags(flags);
        aty.startActivity(i);
        aty.finish();
    }

    public void skipActivity(Activity aty, Bundle bundle  ,int flags, Class clazz )
    {
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        i.setFlags(flags);
        aty.startActivity(i);
        aty.finish();
    }

    public void showActivity(Activity aty  ,int flags, Bundle bundle, Class clazz)
    {
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        i.setFlags(flags);
        aty.startActivity(i);
    }

    public void showActivity(Context aty  , int flags, Bundle bundle, Class clazz)
    {
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        i.setFlags(flags);
        aty.startActivity(i);
    }

    public void showActivity(Context aty  ,int flags, Class clazz ){
        Intent i = new Intent(aty,clazz);
        i.setFlags(flags);
        aty.startActivity(i);
    }

    public void showActivityAnima(Activity aty, Class clazz, Bundle bundle, int inAnima, int outAnima ){
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        aty.startActivity(i);
        aty.overridePendingTransition(inAnima, outAnima);
    }

    public void showActivityAnima(Activity aty, Class clazz, int flags, int inAnima, int outAnima ){
        Intent i = new Intent(aty,clazz);
        i.setFlags(flags);
        aty.startActivity(i);
        aty.overridePendingTransition(inAnima, outAnima);
    }

    public void skipActivityAnima(Activity aty, Class clazz, Bundle bundle, int inAnima, int outAnima ){
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        aty.startActivity(i);
        aty.overridePendingTransition(inAnima, outAnima);
        aty.finish();
    }

    public void showActivityAnima(Activity aty, Class clazz, int inAnima, int outAnima ){
        Intent i = new Intent(aty,clazz);
        aty.startActivity(i);
        aty.overridePendingTransition(inAnima, outAnima);
    }

    public void showActivityAnima(Activity aty, Class clazz, int flags, Bundle bundle, int inAnima, int outAnima ){
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        i.setFlags(flags);
        aty.startActivity(i);
        aty.overridePendingTransition(inAnima, outAnima);
    }

    public void showActivity(Context aty, Intent intent)
    {
        aty.startActivity(intent);
    }
}
