package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.EncryptUtil;
import com.bigkoo.pickerview.OptionsPickerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 说明:

 */

public class RegisterPopWindow extends PopupWindow {

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
    private String sexTag;

    public
    RegisterPopWindow ( Context context, Activity aty, EventBus eventBus) {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public
    void showRegister () {
        /**
         * 实例化下单界面组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_register,
                null
        );
        // 获取具体UI组件
        final EditText userName = (EditText) view.findViewById(R.id.userName);
        final EditText userPsw = (EditText) view.findViewById(R.id.userPsw);
        final EditText userRePsw = (EditText) view.findViewById(R.id.userRePsw);
        final EditText realName = (EditText) view.findViewById(R.id.realName);
        final EditText phone = (EditText) view.findViewById(R.id.phone);
        final EditText qq = (EditText) view.findViewById(R.id.qq);
        final RadioGroup sex = (RadioGroup) view.findViewById(R.id.sex);

        TextView register = (TextView) view.findViewById(R.id.register);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);

        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(R.id.male == checkedId){
                    sexTag = context.getResources().getString(R.string.sex_man);
                } else if(R.id.female == checkedId){
                    sexTag = context.getResources().getString(R.string.sex_woman);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userName.getText()))
                {
                    //请输入用户名
                    Bundle bundle = new Bundle();
                    bundle.putString("info", context.getResources().getString(R.string.login_username_hit));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                }
                else if(TextUtils.isEmpty(userPsw.getText()))
                {
                    //请输入密码
                    Bundle bundle = new Bundle();
                    bundle.putString("info", context.getResources().getString(R.string.login_usepsw_hit));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                }
                else if(TextUtils.isEmpty(userRePsw.getText()))
                {
                    //请确认密码
                    Bundle bundle = new Bundle();
                    bundle.putString("info", context.getResources().getString(R.string.login_userepsw_hit));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                }
                else if(!userRePsw.getText().toString().equals(userRePsw.getText().toString()))
                {
                    //两次密码不一致
                    Bundle bundle = new Bundle();
                    bundle.putString("info", context.getResources().getString(R.string.login_pswnoe_hit));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                }
                else if(TextUtils.isEmpty(realName.getText()))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("info", context.getResources().getString(R.string.register_realname_hit));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                }
                else if(TextUtils.isEmpty(phone.getText()))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("info", "请输入手机号");
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                }
                else
                {
                    List<AccountBean> accountList = AccountBean.find(AccountBean.class, "userName = ?", userName.getText().toString());
                    if(null!=accountList&&!accountList.isEmpty())
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("info", context.getResources().getString(R.string.register_exist_it));
                        eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                    }
                    else
                    {
                        List<AccountBean> accountss = AccountBean.find(AccountBean.class, "userPhone = ?", phone.getText().toString());
                        if(null!=accountss&&!accountss.isEmpty())
                        {
                            Bundle bundle = new Bundle();
                            bundle.putString("info", "该手机已被注册，请重新填写");
                            eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_ERROR, bundle));
                        }
                        else
                        {
                            AccountBean oldAccount = AccountBean.last(AccountBean.class);
                            AccountBean account = new AccountBean();
                            if(null!=oldAccount)
                            {
                                account.userId = oldAccount.userId+1;
                            }
                            else
                            {
                                account.userId = 1001;
                            }
                            account.userPsw = EncryptUtil.getInstance().encryptMd532(userPsw.getText().toString());
                            account.realName = realName.getText().toString();
                            account.userLogo = "";
                            account.userLevel = "普通用户";
                            account.userSex = TextUtils.isEmpty(sexTag)?context.getResources().getString(R.string.sex_man):sexTag;
                            account.userStatus = 0;
                            account.moblie = phone.getText().toString();
                            account.userName = userName.getText().toString();
                            //初始化账户余额1000
                            account.balance = 1000f;
                            account.save();
                            dismissView();
                            Bundle bundle = new Bundle();
                            bundle.putString("info", context.getResources().getString(R.string.register_success_hit));
                            eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_REGISTER_SUCCESS, bundle));
                        }
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissView();
                eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_REGISTER_CANCEL));
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
