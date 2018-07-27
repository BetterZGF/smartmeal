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
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.ui.order.ViewOrderActivity;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.EncryptUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 说明:

 */

public class ModifyPswPopWindow extends PopupWindow {

    /**
     * Context
     */
    private Context context;

    /**
     * eventbus实例，用以消息交互
     */
    private EventBus eventBus;
    /**
     * Activity
     */
    private Activity aty;

    public
    ModifyPswPopWindow ( Context context, Activity aty, EventBus eventBus) {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public
    void showModify () {
        /**
         * 实例化下单界面组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_modify_psw,
                null
        );
        // 获取具体UI组件
        final EditText oldPsw = (EditText) view.findViewById(R.id.oldPsw);
        final EditText newPsw = (EditText) view.findViewById(R.id.newPsw);
        final EditText newRePsw = (EditText) view.findViewById(R.id.newRePsw);
        TextView modify = (TextView) view.findViewById(R.id.modify);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(oldPsw.getText()))
                {
                    //请输入用户名
                    Bundle bundle = new Bundle();
                    bundle.putString("info", "请输入原密码");
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_MODIFY_ERROR, bundle));
                }
                else if(TextUtils.isEmpty(newPsw.getText()))
                {
                    //请输入密码
                    Bundle bundle = new Bundle();
                    bundle.putString("info", "请输入新密码");
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_MODIFY_ERROR, bundle));
                }
                else if(TextUtils.isEmpty(newRePsw.getText()))
                {
                    //请输入密码
                    Bundle bundle = new Bundle();
                    bundle.putString("info", "请确认新密码");
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_MODIFY_ERROR, bundle));
                }
                else if(!newRePsw.getText().toString().equals(newPsw.getText().toString()))
                {
                    //请输入密码
                    Bundle bundle = new Bundle();
                    bundle.putString("info", "两次新密码不一致");
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_MODIFY_ERROR, bundle));
                }
                else
                {
                    AccountBean account = CConfigure.obtainAccount(context);
                    List<AccountBean> accountList = AccountBean.find(AccountBean.class, "userId = ?", String.valueOf(account.userId));
                    if(null!=accountList&&!accountList.isEmpty())
                    {
                        AccountBean user = accountList.get(0);
                        if(!EncryptUtil.getInstance().encryptMd532(oldPsw.getText().toString()).equals(user
                                .userPsw))
                        {
                            Bundle bundle = new Bundle();
                            bundle.putString("info", "原密码输入有误");
                            eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_MODIFY_ERROR, bundle));
                        }
                        else
                        {
                            dismissView();
                            Bundle bundle = new Bundle();
                            bundle.putString("newPsw", newPsw.getText().toString());
                            bundle.putInt("userId", account.userId);
                            eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_MODIFY_SUCCESS, bundle));
                        }
                    }
                    else
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("info", "修改密码错误");
                        eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_MODIFY_ERROR, bundle));
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissView();
                eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_MODIFY_CANCEL));
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
        //设置窗口弹出动画
        this.setAnimationStyle(R.style.animationPop01);
    }

    public void dismissView()
    {
        dismiss ();
    }
}
