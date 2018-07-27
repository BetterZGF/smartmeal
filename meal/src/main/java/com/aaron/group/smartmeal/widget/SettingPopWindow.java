package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 说明:

 */

public class SettingPopWindow extends PopupWindow {


    /**
     * Context
     */
    private Context context;
    /**
     * Activity
     */
    private Activity aty;

    /**
     * eventbus实例
     */
    private EventBus eventBus;

    public
    SettingPopWindow ( Context context, Activity aty, EventBus eventBus) {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public
    void showSetting (int hitId, final int num, final int dishesId) {
        /**
         * 实例化进度条组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_setting,
                null
        );
        // 获取具体UI组件
        final EditText setting = (EditText) view.findViewById(R.id.setting);
        setting.setHint(context.getResources().getString(hitId));
        RelativeLayout btnsureL = (RelativeLayout) view.findViewById(R.id.btnsureL);
        btnsureL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(setting.getText()))
                {
                    eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_SETTING_PEOPLE_NULL));
                }
                else
                {
                    try
                    {
                        int people = Integer.parseInt(setting.getText().toString());
                        if(people>0)
                        {
                            dismissView();
                            Bundle bundle = new Bundle();
                            bundle.putInt("people", people);
                            bundle.putInt("num", num);
                            bundle.putInt("dishesId", dishesId);
                            eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_SETTING_PEOPLE, bundle));
                        }
                        else
                        {
                            eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_SETTING_PEOPLE_NULL));
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_SETTING_PEOPLE_NULL));
                    }
                }
            }
        });
        // 设置SelectPicPopupWindow的View
        this.setContentView ( view );
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth (WindowManager.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight (WindowManager.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        CommonUtils.backgroundAlpha(aty, 0.4f);
    }

    public void dismissView()
    {
        setOnDismissListener ( new PoponDismissListener( aty ) );
        dismiss ();
    }
}
