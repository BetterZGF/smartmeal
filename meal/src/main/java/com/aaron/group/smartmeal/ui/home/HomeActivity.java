package com.aaron.group.smartmeal.ui.home;

import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.base.BaseActivity;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.DiningTableBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.bean.OrderDishesBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.SnackbarUi;
import com.aaron.group.smartmeal.widget.CarPopWindow;
import com.aaron.group.smartmeal.widget.LoginPopWindow;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 说明: 主界面，
 * 主界面包括三个界面
 * 1、首页
 * 2、分类界面
 * 3、我的界面

 */

public class HomeActivity extends BaseActivity {

    @Bind(R.id.toolBar)
    Toolbar toolBar;
    @Bind(R.id.leftBarIcon)
    ImageView leftBarIcon;
    @Bind(R.id.barTitle)
    TextView barTitle;
    @Bind(R.id.rightBarIcon)
    ImageView rightBarIcon;
    @Bind(R.id.leftBarText)
    TextView leftBarText;
    @Bind(R.id.rightBarText)
    TextView rightBarText;

    @Bind(android.R.id.tabhost)
    FragmentTabHost tabhost;
    @Bind(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @Bind(R.id.leftBarL)
    RelativeLayout leftBarL;

    public RelativeLayout homeBtn;
    private long exitTime = 0l;
    private Class[] tabFragments;
    private Integer[] tabIcons;
    private String[] tabTags;
    private LoginPopWindow loginPop;
    private CarPopWindow carPop;

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 80)
    public void onEvent(EventBusMsg msg)
    {
        //接收eventbus消息，并处理
        switch (msg.getType())
        {
            case INFO_LOGIN_CANCEL:
            {
                //取消登录
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(HomeActivity.this), resources.getString(R.string.info_login_cancel));
            }
            break;
            case TO_HOME:
            {
                //显示第一页
                tabhost.onTabChanged("首页");
                tabhost.getTabWidget().setCurrentTab(0);
            }
            break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 80)
    public void onEvent(EventBusBundleMsg msg)
    {
        switch (msg.getType())
        {
            case BUNDLE_LOGIN_ERROR:
            {
                //登录出错提醒
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(HomeActivity.this), msg.getBundle().getString("info"));
            }
            break;
            case BUNDLE_LOGIN_SUCCESS:
            {
                //登录成功提醒
                AccountBean account = (AccountBean) msg.getBundle().getSerializable("account");
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(HomeActivity.this), account.msg);
                //保存用户信息到本地
                CConfigure.saveAccount(HomeActivity.this, account);
            }
            break;
            default:
                break;
        }
    }

    @OnClick(R.id.leftBarL)
    void leftBarL()
    {
        if(!CConfigure.isLogin(HomeActivity.this))
        {
            if(null!=loginPop)
            {
                //未登录
                loginPop.showLogin();
                loginPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
            }
        }
    }

    @OnClick(R.id.rightBarL)
    void rightBarL()
    {
        if(!CConfigure.isLogin(HomeActivity.this))
        {
            if(null!=loginPop)
            {
                //未登录
                loginPop.showLogin();
                loginPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
            }
        }
        else
        {
            //点击查看购物车
            if(null!=carPop)
            {
                carPop.showCar();
                carPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_home);
        ButterKnife.bind(this);
        loginPop = new LoginPopWindow(HomeActivity.this, HomeActivity.this, eventBus);
        carPop = new CarPopWindow(HomeActivity.this, HomeActivity.this, mHandler);
        initTitle();
        initContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                SnackbarUi.showMsg(CommonUtils.getRootView(HomeActivity.this), resources.getString(R.string.exit_tips), SnackbarUi.ALERT);
                exitTime = System.currentTimeMillis();
            } else
            {
                ActivityUtils.getInstance().closeSelf(HomeActivity.this);
                CommonUtils.killAppDestory(HomeActivity.this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=loginPop)
        {
            loginPop.dismissView();
        }
        if(null!=carPop)
        {
            carPop.dismissView();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void initTitle() {
        //设置标题栏布局
        toolBar.setTitle("");
        barTitle.setText(resources.getString(R.string.home_tag));
        CommonUtils.loadBackground(leftBarIcon, resources.getDrawable(R.mipmap.home_icon));
        CommonUtils.loadBackground(rightBarIcon, resources.getDrawable(R.mipmap.tab_car));
        toolBar.setNavigationIcon(null);
    }

    @Override
    public void initContent() {
        homeBtn = leftBarL;
        //获取底部菜单标题
        tabTags = resources.getStringArray(R.array.tabTags);
        //获取底部菜单draw
        String[] bottomDraws = resources.getStringArray(R.array.tabIcons);
        List<Integer> rawIds = new ArrayList<Integer>();
        for(String drawStr:bottomDraws)
        {
            rawIds.add(CommonUtils.getDrawableId(HomeActivity.this, drawStr));
        }
        tabIcons = rawIds.toArray(new Integer[rawIds.size()]);
        //获取菜单显示的fragment
        List<Class> fragmentList = new ArrayList<Class>();
        String[]fragments = resources.getStringArray(R.array.fragmentArray);
        for(String framentName:fragments)
        {
            try {
                fragmentList.add(Class.forName(framentName));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        tabFragments = fragmentList.toArray(new Class[fragmentList.size()]);
        //实例化TabHost对象，得到TabHost
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabhost.getTabWidget().setDividerDrawable(null);
        //得到fragment的个数
        int count = tabFragments.length;
        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(tabTags[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            tabhost.addTab(tabSpec, tabFragments[i], null);
            //设置Tab按钮的背景
            tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_bg01);
            tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    barTitle.setText(tabId);
                }
            });
        }
        tabhost.getTabWidget().getChildTabViewAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CConfigure.isLogin(HomeActivity.this))
                {
                    //未登录
                    loginPop.showLogin();
                    loginPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
                }
                else
                {
                    //如果已经登录，执行默认点击操作
                    //由于已经覆写了点击方法，所以需要实现tab切换
                    tabhost.setCurrentTab(2);
                    tabhost.getTabWidget().requestFocus(View.FOCUS_FORWARD);
                }
            }
        });
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = inflater.inflate(R.layout.item_tab, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tabIcon);
        CommonUtils.loadBackground(imageView, resources.getDrawable(tabIcons[index]));
        TextView textView = (TextView) view.findViewById(R.id.tabTag);
        textView.setText(tabTags[index]);
        return view;
    }
}
