package com.aaron.group.smartmeal.ui.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.ui.auxiliary.ChangeDingTableActivity;
import com.aaron.group.smartmeal.ui.auxiliary.DishesManagerActivity;
import com.aaron.group.smartmeal.ui.home.HomeActivity;
import com.aaron.group.smartmeal.ui.order.OrderManagerActivity;
import com.aaron.group.smartmeal.ui.order.ViewOrderActivity;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.EncryptUtil;
import com.aaron.group.smartmeal.utils.SnackbarUi;
import com.aaron.group.smartmeal.widget.CircleImageView;
import com.aaron.group.smartmeal.widget.ModifyPswPopWindow;
import com.aaron.group.smartmeal.widget.PersonalPopWindow;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 说明:

 */

public class PersonalFragment extends Fragment {

    @Bind(R.id.userLogo)
    CircleImageView userLogo;
    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.userLevel)
    TextView userLevel;
    @Bind(R.id.dishesManagerL)
    RelativeLayout dishesManagerL;

    private HomeActivity rootAty;
    private View rootView;
    private ModifyPswPopWindow modifyPswPop;
    private EventBus eventBus;
    private PersonalPopWindow personalPop;

    @OnClick(R.id.dishesManagerL)
    void dishesManagerL()
    {
        //管理员管理菜品
        ActivityUtils.getInstance().showActivityAnima(rootAty, DishesManagerActivity.class, R.anim.zoomin, R.anim.zoomout);
    }

    @OnClick(R.id.personalViewOrderL)
    void personalViewOrderL()
    {
        /*AccountBean account = CConfigure.obtainAccount(rootAty);
        //获取订单
        List<OrderBean> orderList = OrderBean.find(OrderBean.class, "orderStatus = ? and userId = ?", "0", String.valueOf(account.userId));
        if(null!=orderList&&!orderList.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", orderList.get(0));
            ActivityUtils.getInstance().showActivity(rootAty, ViewOrderActivity.class, bundle);
        }
        else
        {
            SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.info_order_null));
        }*/
        ActivityUtils.getInstance().showActivityAnima(rootAty, OrderManagerActivity.class, R.anim.zoomin, R.anim.zoomout);
    }

    @OnClick(R.id.personalChangeDingTableL)
    void personalChangeDingTableL()
    {
        AccountBean account = CConfigure.obtainAccount(rootAty);
        //获取订单
        List<OrderBean> orderList = OrderBean.find(OrderBean.class, "orderStatus = ? and userId = ?", "0", String.valueOf(account.userId));
        if(null!=orderList&&!orderList.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", orderList.get(0));
            ActivityUtils.getInstance().showActivityAnima(rootAty, ChangeDingTableActivity.class, bundle, R.anim.zoomin, R.anim.zoomout);
        }
        else
        {
            SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.info_order_null));
        }
    }

    @OnClick(R.id.personalUseModifyPswL)
    void personalUseModifyPswL()
    {
        modifyPswPop.showModify();
        modifyPswPop.showAtLocation(userLogo, Gravity.CENTER, 0, 0);
    }

    @OnClick(R.id.personalUseMyL)
    void personalUseMyL()
    {
        personalPop.showPersonal();
        personalPop.showAtLocation(userLogo, Gravity.CENTER, 0, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 90)
    public void onEvent(EventBusMsg msg)
    {
        switch (msg.getType())
        {
            case INFO_MODIFY_CANCEL:
            {
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "你取消了本次修改密码");
            }
            break;
            case LOGOUT:
            {
                //登出，显示首页
                rootAty.eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.TO_HOME));
            }
            break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 90)
    public void onEvent(EventBusBundleMsg msg)
    {
        switch (msg.getType())
        {
            case BUNDLE_MODIFY_ERROR:
            {
                Bundle bundle = msg.getBundle();
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), bundle.getString("info"));
            }
            break;
            case BUNDLE_MODIFY_SUCCESS:
            {
                Bundle bundle = msg.getBundle();
                String newPsw = bundle.getString("newPsw");
                int userId = bundle.getInt("userId");
                List<AccountBean> accountList = AccountBean.find(AccountBean.class, "userId = ?", String.valueOf(userId));
                if(null!=accountList&&!accountList.isEmpty())
                {
                    AccountBean account = accountList.get(0);
                    account.userPsw = EncryptUtil.getInstance().encryptMd532(newPsw);
                    account.save();
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "修改密码成功");
                }
                else
                {
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "修改密码错误");
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        rootAty = (HomeActivity) this.getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, rootView);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        modifyPswPop = new ModifyPswPopWindow(rootAty, rootAty, eventBus);
        personalPop = new PersonalPopWindow(rootAty, rootAty, eventBus);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootAty.mHandler.post(runnableLoadData);
    }

    Runnable runnableLoadData = new Runnable() {
        @Override
        public void run() {
            AccountBean account = CConfigure.obtainAccount(rootAty);
            userLogo.setBorderColorResource(R.color.color_text_03);
            userLogo.setBorderWidth(3);
            Glide.with(PersonalFragment.this).load(account.userLogo).animate(android.R.anim.slide_in_left).placeholder(R.mipmap.user_logo_defluat).into(userLogo);
            userName.setText(account.realName);
            userLevel.setText(account.userLevel);
            if("系统管理员".equals(account.userLevel))
            {
                dishesManagerL.setVisibility(View.VISIBLE);
            }
            else
            {
                dishesManagerL.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=eventBus)
        {
            eventBus.unregister(this);
        }
        if(null!=personalPop)
        {
            personalPop.dismissView();
        }
        if(null!=rootAty.mHandler)
        {
            rootAty.mHandler.removeCallbacks(runnableLoadData);
        }
        if(null!=modifyPswPop)
        {
            modifyPswPop.dismissView();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootAty = null;
    }
}
