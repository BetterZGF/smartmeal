package com.aaron.group.smartmeal.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;

/**
 * 说明:

 */

public class SnackbarUi {

    public static final   int INFO = 1;
    public static final  int CONFIRM = 2;
    public static final  int WARNING = 3;
    public static final  int ALERT = 4;
    public static  int red = 0xfff44336;
    public static  int green = 0xff4caf50;
    public static  int blue = 0xff2195f3;
    public static  int orange = 0xffffc107;

    /**
     * 消息提醒，分等级
     * @param view
     * @param message
     * @param type
     * @return
     */
    public static void showMsg(View view, String message, int type){
        Snackbar snackbar = Snackbar.make(view,message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(1000);
        switchType(snackbar,type);
        snackbar.show();
    }

    /**
     * 消息提醒，分等级
     * @param view
     * @param message
     * @return
     */
    public static void showMsgInfo(View view, String message){
        Snackbar snackbar = Snackbar.make(view,message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(2000);
        switchType(snackbar,INFO);
        snackbar.show();
    }

    /**
     * 消息提醒，分等级
     * @param view
     * @param message
     * @return
     */
    public static void showMsgInfo(View view, String message, int duration){
        Snackbar snackbar = Snackbar.make(view,message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(duration);
        switchType(snackbar,INFO);
        snackbar.show();
    }

    /**
     * 消息提醒，分等级
     * @param view
     * @param message
     * @return
     */
    public static Snackbar showAlertMsg(View view, String message){
        Snackbar snackbar = Snackbar.make(view,message, Snackbar.LENGTH_INDEFINITE);
        switchType(snackbar,WARNING);
        snackbar.show();
        return snackbar;
    }

    //选择预设类型
    private static void switchType(Snackbar snackbar,int type){
        switch (type){
            case INFO:
                setSnackbarColor(snackbar, Color.WHITE, blue);
                break;
            case CONFIRM:
                setSnackbarColor(snackbar, Color.WHITE, green);
                break;
            case WARNING:
                setSnackbarColor(snackbar, Color.WHITE, orange);
                break;
            case ALERT:
                setSnackbarColor(snackbar, Color.WHITE, red);
                break;
        }
    }

    /**
     * 设置Snackbar文字和背景颜色
     * @param snackbar
     * @param messageColor
     * @param backgroundColor
     */
    private static void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) {
        Snackbar.SnackbarLayout view = (Snackbar.SnackbarLayout) snackbar.getView();
        if(view!=null){
            view.setBackgroundColor(backgroundColor);
            TextView text = ((TextView) view.findViewById(R.id.snackbar_text));
            text.setTextColor(messageColor);
            text.setTextSize(CommonUtils.px2dp(view.getContext(), view.getResources().getDimension(R.dimen.text_size_14)));
            TextPaint paint = text.getPaint();
            paint.setFakeBoldText(true);
        }
    }

    public static void showConfirm(View view, String message, String actionText, View.OnClickListener listener, Snackbar.Callback callBack)
    {
        Snackbar snackbar = Snackbar.make(view,message, Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout rootView = (Snackbar.SnackbarLayout) snackbar.getView();
        if(rootView!=null){
            rootView.setBackgroundColor(view.getResources().getColor(R.color.colorPrimary));
            TextView text = ((TextView) rootView.findViewById(R.id.snackbar_text));
            text.setTextColor(Color.WHITE);
            text.setTextSize(CommonUtils.px2dp(view.getContext(), view.getResources().getDimension(R.dimen.text_size_14)));
        }
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setAction(actionText, listener);
        if(null!=callBack)
        {
            snackbar.setCallback(callBack);
        }
        snackbar.show();
    }
}
