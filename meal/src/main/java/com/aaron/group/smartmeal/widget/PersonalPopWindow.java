package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.ui.home.fragment.PersonalFragment;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.EncryptUtil;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 说明:

 */

public class PersonalPopWindow extends PopupWindow {

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
    PersonalPopWindow ( Context context, Activity aty, EventBus eventBus) {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public
    void showPersonal () {
        /**
         * 实例化下单界面组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_personal,
                null
        );
        AccountBean account = CConfigure.obtainAccount(context);
        // 获取具体UI组件
        final CircleImageView userLogo = (CircleImageView) view.findViewById(R.id.userLogo);
        userLogo.setBorderColorResource(R.color.colorPrimary);
        userLogo.setBorderWidth(3);
        Glide.with(context).load(account.userLogo).animate(android.R.anim.slide_in_left).placeholder(R.mipmap.user_logo_defluat).into(userLogo);
        final TextView useName = (TextView) view.findViewById(R.id.useName);
        useName.setText(account.userName);
        final TextView moblie = (TextView) view.findViewById(R.id.moblie);
        moblie.setText(account.moblie);
        final TextView realname = (TextView) view.findViewById(R.id.realname);
        realname.setText(account.realName);
        final TextView sex = (TextView) view.findViewById(R.id.sex);
        sex.setText(account.userSex);
        final TextView level = (TextView) view.findViewById(R.id.level);
        level.setText(account.userLevel);
        final TextView balance = (TextView) view.findViewById(R.id.balance);
        balance.setText(String.valueOf(account.balance)+"元");
        final RelativeLayout logoutL = (RelativeLayout) view.findViewById(R.id.logoutL);
        final ImageView closeBtn = (ImageView) view.findViewById(R.id.closeBtn);

        logoutL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CConfigure.logout(context);
                eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.LOGOUT));
                dismissView();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissView();
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
