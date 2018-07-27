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
import com.aaron.group.smartmeal.base.BaseBean;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.EncryptUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 说明:

 */

public class LoginPopWindow extends PopupWindow {

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
    LoginPopWindow ( Context context, Activity aty, EventBus eventBus) {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public
    void showLogin () {
        /**
         * 实例化下单界面组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_login,
                null
        );
        // 获取具体UI组件
        final EditText userName = (EditText) view.findViewById(R.id.userName);
        userName.setText("18844549171");
        final EditText userPsw = (EditText) view.findViewById(R.id.userPsw);
        userPsw.setText("123456");
        TextView login = (TextView) view.findViewById(R.id.login);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userName.getText()))
                {
                    //请输入用户名
                    Bundle bundle = new Bundle();
                    bundle.putString("info", context.getResources().getString(R.string.login_username_hit));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_LOGIN_ERROR, bundle));
                }
                else if(TextUtils.isEmpty(userPsw.getText()))
                {
                    //请输入密码
                    Bundle bundle = new Bundle();
                    bundle.putString("info", context.getResources().getString(R.string.login_usepsw_hit));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_LOGIN_ERROR, bundle));
                }
                else
                {
                    AccountBean account = doLogin(userName.getText().toString(), userPsw.getText().toString());
                    if(401==account.retCode)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("info", account.msg);
                        //用户不存在
                        eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_LOGIN_ERROR, bundle));
                    }
                    else if(501==account.retCode)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("info", account.msg);
                        //密码不正确
                        eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_LOGIN_ERROR, bundle));
                    }
                    else if(200==account.retCode)
                    {
                        dismissView();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("account", account);
                        //密码不正确
                        eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_LOGIN_SUCCESS, bundle));
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissView();
                eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_LOGIN_CANCEL));
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

    /**
     * 执行登录
     * @return
     */
    private AccountBean doLogin(String userName, String userPsw)
    {
        AccountBean result = new AccountBean();
        //根据用户名查询用户，
        List<AccountBean> accounts = AccountBean.find(AccountBean.class, "userName = ?", userName);
        if(null!=accounts&&!accounts.isEmpty())
        {
            AccountBean account = accounts.get(0);
            if(!EncryptUtil.getInstance().encryptMd532(userPsw).equals(account.userPsw))
            {
                result.retCode = 501;
                result.msg = context.getResources().getString(R.string.login_reslut_pswerror);
            }
            else
            {
                result = account;
                result.retCode = 200;
                result.msg = context.getResources().getString(R.string.login_reslut_success);
            }
        }
        else
        {
            result.retCode = 401;
            result.msg = context.getResources().getString(R.string.login_reslut_noname);
        }

        return result;
    }

    public void dismissView()
    {
        dismiss ();
    }
}
