package com.aaron.group.smartmeal.ui.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.adapter.DishesAdapter;
import com.aaron.group.smartmeal.adapter.NetworkImageHolderView;
import com.aaron.group.smartmeal.base.BaseBean;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.BusniessBean;
import com.aaron.group.smartmeal.bean.DiningTableBean;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.bean.OrderDishesBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.retrofit.RetrofitUtil;
import com.aaron.group.smartmeal.retrofit.service.DiningTableService;
import com.aaron.group.smartmeal.ui.home.HomeActivity;
import com.aaron.group.smartmeal.ui.order.ViewOrderActivity;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.JSONUtils;
import com.aaron.group.smartmeal.utils.RecyclerDividerForOther;
import com.aaron.group.smartmeal.utils.SnackbarUi;
import com.aaron.group.smartmeal.widget.LoginPopWindow;
import com.aaron.group.smartmeal.widget.MsgPopWindow;
import com.aaron.group.smartmeal.widget.OrderPopWindow;
import com.aaron.group.smartmeal.widget.RegisterPopWindow;
import com.aaron.group.smartmeal.widget.SettingPopWindow;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 说明:

 */

public class HomeFragment extends Fragment {

    @Bind(R.id.homeBanner)
    ConvenientBanner homeBanner;
    @Bind(R.id.days)
    TextView days;
    @Bind(R.id.weeks)
    TextView weeks;
    @Bind(R.id.news)
    TextView news;
    @Bind(R.id.homeDishesRecycler)
    RecyclerView homeDishesRecycler;

    private HomeActivity rootAty;
    private View rootView;
    private List<DishesBean> datas;
    private DishesAdapter adapter;
    private List<String> bannerUrls;
    private ViewGroup parentV;
    private EventBus eventBus;
    private OrderPopWindow orderPop;
    private LoginPopWindow loginPop;
    private RegisterPopWindow registerPop;
    private MsgPopWindow msgPop;
    private SettingPopWindow settingPop;
    private DiningTableService service;
    private Call<BusniessBean> call;
    private List<DiningTableBean> diningTableAlls;
    private Call<BaseBean> saveCall;
    private boolean isPositive = true;

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 80)
    public void onEvent(EventBusMsg msg)
    {
        //接收eventbus消息，并处理
        switch (msg.getType())
        {
            case POP_ORDER_NODATA:
            {
                //提示菜品信息未获取，请重试
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.home_order_pop_nodata));
            }
            break;
            case POP_ORDER_CANCEL:
            {
                //提示菜品信息未获取，请重试
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.home_order_pop_cancel));
            }
            break;
            case INFO_TO_LOGIN:
            {
                //弹出登录界面
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.info_order_tologin));
                loginPop.showLogin();
                loginPop.showAtLocation(parentV, Gravity.CENTER, 0, 0);
            }
            break;
            case INFO_LOGIN_CANCEL:
            {
                //去注册
                registerPop.showRegister();
                registerPop.showAtLocation(parentV, Gravity.CENTER, 0, 0);
            }
            break;
            case INFO_SETTING_PEOPLE_NULL:
            {
                //设置是用餐人数，数据为空
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.info_setting_people_error));
            }
            break;
            case INFO_REGISTER_CANCEL:
            {
                //设置是用餐人数，数据为空
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.info_register_cancel));
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
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), msg.getBundle().getString("info"));
            }
            break;
            case BUNDLE_LOGIN_SUCCESS:
            {
                //登录成功提醒
                AccountBean account = (AccountBean) msg.getBundle().getSerializable("account");
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), account.msg);
                //保存用户信息到本地
                CConfigure.saveAccount(rootAty, account);
            }
            break;
            case BUNDLE_TO_ORDER:
            {

                //去下单
                //查询未结单的订单
                AccountBean account = CConfigure.obtainAccount(rootAty);
                //获取订单
                List<OrderBean> orders = OrderBean.find(OrderBean.class, "orderStatus = ? and userId = ?", "0", String.valueOf(account.userId));
                Bundle bundle = msg.getBundle();
                int num = bundle.getInt("num");
                int dishesId = bundle.getInt("dishesId");
                if(null!=orders&&!orders.isEmpty())
                {
                    OrderBean order = orders.get(0);
                    List<OrderDishesBean> orderDishesList = OrderDishesBean.find(OrderDishesBean.class, "orderId = ? and dishesId = ?", order.orderId, String.valueOf(dishesId));
                    if(null!=orderDishesList&&!orderDishesList.isEmpty())
                    {
                        //存在未结单的订单，在原有订单中追加菜品
                        //添加订单菜品数据
                        OrderDishesBean orderDishes = orderDishesList.get(0);
                        orderDishes.dishesNum+= num;
                        orderDishes.save();
                    }
                    else
                    {
                        //存在未结单的订单，在原有订单中追加菜品
                        //添加订单菜品数据
                        OrderDishesBean orderDishes = new OrderDishesBean();
                        orderDishes.orderId = order.orderId;
                        orderDishes.dishesId = dishesId;
                        orderDishes.dishesNum = num;
                        orderDishes.save();
                    }
                    //订单追加成功，提醒
                    msgPop.showMessage(rootAty.resources.getString(R.string.dishes_append));
                    msgPop.showAtLocation(parentV, Gravity.CENTER, 0, 0);
                }
                else
                {
                    //设置用餐人数
                    settingPop.showSetting(R.string.setting_people_hit, num, dishesId);
                    settingPop.showAtLocation(parentV, Gravity.CENTER, 0, 0);
                }
            }
            break;
            case BUNDLE_SETTING_PEOPLE:
            {
                Bundle bundle = msg.getBundle();
                final int num = bundle.getInt("num");
                final int dishesId = bundle.getInt("dishesId");
                final int people = bundle.getInt("people");
                //新建订单
                //1、查询出可用餐桌
                service = rootAty.retrofit.create(DiningTableService.class);
                Map<String, String> map = new HashMap<String, String>();
                map.put("table", "dininingTable");
                map.put("k", Base64.encodeToString("shop01".getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
                call = service.obtainDiningTables(map);
                call.enqueue(new Callback<BusniessBean>() {
                    @Override
                    public void onResponse(Call<BusniessBean> call, Response<BusniessBean> response) {
                        call.cancel();
                        if(null!=response.body())
                        {
                            BusniessBean busniess = response.body();
                            if("200".equals(busniess.retCode))
                            {
                                JSONUtils<DiningTableBean> jsonUtils = new JSONUtils<DiningTableBean>();
                                String diningTablejson = busniess.result.v;
                                diningTableAlls = jsonUtils.jsonToList(diningTablejson, DiningTableBean.class);
                                List<DiningTableBean> diningTables = CommonUtils.filterDiningTable(diningTableAlls);
                                if(null!=diningTables&&!diningTables.isEmpty())
                                {
                                    final DiningTableBean diningTable = diningTables.get(0);
                                    //设置就餐餐桌不可用
                                    diningTable.diningTableStatus = 1;
                                    //修改餐座状态
                                    diningTableAlls = CommonUtils.fixedDiningTableStatus(diningTableAlls, diningTable);
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("table", "dininingTable");
                                    map.put("k", Base64.encodeToString("shop01".getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
                                    map.put("v", Base64.encodeToString(jsonUtils.listToJson(diningTableAlls).getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
                                    saveCall = service.commitTables(map);
                                    saveCall.enqueue(new Callback<BaseBean>() {
                                        @Override
                                        public void onResponse(Call<BaseBean> call, Response<BaseBean> response) {
                                            if(null!=response.body())
                                            {
                                                BaseBean base = response.body();
                                                if("200".equals(base.retCode))
                                                {
                                                    //保存新订单
                                                    AccountBean account = CConfigure.obtainAccount(rootAty);
                                                    OrderBean order = new OrderBean();
                                                    order.orderId = String.valueOf(System.currentTimeMillis());
                                                    order.orderStatus = 0;
                                                    order.orderTime = CommonUtils.obtainTime();
                                                    order.diningTableId = diningTable.diningTableId;
                                                    order.userId = account.userId;
                                                    order.peoples = people;
                                                    order.save();
                                                    //添加订单菜品数据
                                                    OrderDishesBean orderDishes = new OrderDishesBean();
                                                    orderDishes.orderId = order.orderId;
                                                    orderDishes.dishesId = dishesId;
                                                    orderDishes.dishesNum = num;
                                                    orderDishes.save();
                                                    //订单保存成功，提醒餐桌编号
                                                    msgPop.showMessage(rootAty.resources.getString(R.string.info_order_tablesno)+
                                                            diningTable.diningTableNo+
                                                            rootAty.resources.getString(R.string.info_tablesno));
                                                    msgPop.showAtLocation(parentV, Gravity.CENTER, 0, 0);
                                                }
                                                else
                                                {
                                                    //无可用桌面
                                                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "生成订单失败，请重试");
                                                }
                                            }
                                            else
                                            {
                                                //无可用桌面
                                                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "生成订单失败，请重试");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<BaseBean> call, Throwable t) {
                                            //无可用桌面
                                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "生成订单失败，请重试");
                                        }
                                    });
                                }
                                else
                                {
                                    call.cancel();
                                    //无可用桌面
                                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.info_order_notables));
                                }
                            }
                            else
                            {
                                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "桌面数据查询失败，请重试");
                            }
                        }
                        else
                        {
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "桌面数据查询失败，请重试");
                        }
                    }

                    @Override
                    public void onFailure(Call<BusniessBean> call, Throwable t) {
                        //无可用桌面
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "桌面数据查询失败，请重试");
                    }
                });

            }
            break;
            case BUNDLE_REGISTER_ERROR:
            {
                //注册错误
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), msg.getBundle().getString("info"));
            }
            break;
            case BUNDLE_REGISTER_SUCCESS:
            {
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), msg.getBundle().getString("info"));
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
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        if(null==rootAty.retrofit)
        {
            rootAty.retrofit = RetrofitUtil.obtainRetrofit();
        }
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        orderPop = new OrderPopWindow(rootAty, rootAty, eventBus);
        loginPop = new LoginPopWindow(rootAty, rootAty, eventBus);
        registerPop = new RegisterPopWindow(rootAty, rootAty, eventBus);
        settingPop = new SettingPopWindow(rootAty, rootAty, eventBus);
        msgPop = new MsgPopWindow(rootAty, rootAty);
        parentV = container;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootAty.mHandler.post(runnableLoadUi);
        if(null!=rootAty.homeBtn)
        {
            rootAty.homeBtn.setTag(0);
            rootAty.homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if("0".equals(String.valueOf(rootAty.homeBtn.getTag())))
                    {
                        rootAty.homeBtn.setTag(1);
                        //切换倒序显示菜品
                        isPositive = false;
                        rootAty.mHandler.post(runnableLoadData);

                    }
                    else if("1".equals(String.valueOf(rootAty.homeBtn.getTag())))
                    {
                        rootAty.homeBtn.setTag(0);
                        //切换正序显示菜品
                        isPositive = true;
                        rootAty.mHandler.post(runnableLoadData);
                    }
                }
            });
        }
    }

    Runnable runnableLoadUi = new Runnable() {
        @Override
        public void run() {
            //初始化banner
            initBanner();
            initNews();
            initRecycler();
        }
    };

    private void initRecycler()
    {
        // 错列网格布局
        homeDishesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        datas = new ArrayList<DishesBean>();
        adapter = new DishesAdapter(rootAty, datas);
        homeDishesRecycler.setAdapter(adapter);
        homeDishesRecycler.addItemDecoration(new RecyclerDividerForOther(rootAty, 2));
        homeDishesRecycler.setItemAnimator(new ScaleInLeftAnimator()); // 默认动画
        rootAty.mHandler.post(runnableLoadData);
        adapter.setmOnItemClickListener(new com.aaron.group.smartmeal.listener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //首页餐品列表点击事件
                orderPop.showOrder(datas.get(position).dishesId);
                orderPop.showAtLocation(parentV, Gravity.CENTER, 0, 0);
            }
        });
    }

    Runnable runnableLoadData = new Runnable() {
        @Override
        public void run() {
            List<DishesBean> dishesList = DishesBean.find(DishesBean.class, "dishesStatus = ?", "0");
            if(!isPositive)
            {
                Collections.reverse(dishesList);
            }
            if(null!=dishesList&&!dishesList.isEmpty())
            {
                datas.clear();
                datas.addAll(dishesList);
                adapter.notifyDataSetChanged();
            }
            else
            {
                datas.clear();
                adapter.notifyDataSetChanged();
                adapter.setEmptyView(R.layout.layout_common_empty, parentV);
            }
        }
    };

    private void initBanner()
    {
        bannerUrls = new ArrayList<String>();
        bannerUrls.add("http://i3.meishichina.com/attachment/magic/2017/04/13/20170413149205653720913.jpg");
        bannerUrls.add("http://i3.meishichina.com/attachment/magic/2017/04/19/20170419149256969654413.jpg");
        bannerUrls.add("http://i3.meishichina.com/attachment/magic/2017/04/17/20170417149240321535113.jpg");
        homeBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, bannerUrls).setPointViewVisible(true)    //设置指示器是否可见
                .setPageIndicator(new int[]{R.drawable.banner_unchoice, R.drawable.banner_choice})//设置指示器圆点
                .startTurning(5000)     //设置自动切换（同时设置了切换时间间隔）
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT) //设置指示器位置（左、中、右）
                .setManualPageable(true); //设置点击监听事件;
        homeBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                //banner图点击事件，当前的跳转界面未设置。
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), rootAty.resources.getString(R.string.home_banner_tips));
            }
        });
    }

    private void initNews()
    {
        days.setText(CommonUtils.obtainDay());
        weeks.setText(CommonUtils.obtainWeek());
        news.setText(rootAty.resources.getString(R.string.home_news));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=eventBus)
        {
            eventBus.unregister(this);
        }
        if(null!=call)
        {
            call.cancel();
        }
        if(null!=saveCall)
        {
            saveCall.cancel();
        }
        if(null!=homeBanner)
        {
            homeBanner.stopTurning();
        }
        if(null!=rootAty.mHandler)
        {
            rootAty.mHandler.removeCallbacks(runnableLoadUi);
            rootAty.mHandler.removeCallbacks(runnableLoadData);
        }
        if(null!=orderPop)
        {
            orderPop.dismissView();
        }
        if(null!=loginPop)
        {
            loginPop.dismissView();
        }
        if(null!=registerPop)
        {
            registerPop.dismissView();
        }
        if(null!=msgPop)
        {
            msgPop.dismissView();
        }
        if(null!=settingPop)
        {
            settingPop.dismissView();
        }
        if(null!=parentV)
        {
            parentV.removeAllViews();
        }

        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootAty = null;
    }
}
