package com.aaron.group.smartmeal.ui.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.adapter.OrderDishesAdapter;
import com.aaron.group.smartmeal.base.BaseActivity;
import com.aaron.group.smartmeal.base.BaseBean;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.BusniessBean;
import com.aaron.group.smartmeal.bean.DiningTableBean;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.bean.OrderDishesBean;
import com.aaron.group.smartmeal.bean.OrderedDishes;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.retrofit.RetrofitUtil;
import com.aaron.group.smartmeal.retrofit.service.DiningTableService;
import com.aaron.group.smartmeal.ui.home.HomeActivity;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.JSONUtils;
import com.aaron.group.smartmeal.utils.RecycleViewDividerForList;
import com.aaron.group.smartmeal.utils.SnackbarUi;
import com.aaron.group.smartmeal.widget.CommentPopWindow;
import com.aaron.group.smartmeal.widget.ConfirmPopWin;
import com.aaron.group.smartmeal.widget.PayPopWindow;
import com.aaron.group.smartmeal.widget.ProgressPopupWindow;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 说明:

 */

public class ViewOrderActivity extends BaseActivity {

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
    @Bind(R.id.orderNo)
    TextView orderNo;
    @Bind(R.id.orderDingTableNo)
    TextView orderDingTableNo;
    @Bind(R.id.orderStatus)
    TextView orderStatus;
    @Bind(R.id.orderPeoples)
    TextView orderPeoples;
    @Bind(R.id.orderUser)
    TextView orderUser;
    @Bind(R.id.orderTime)
    TextView orderTime;
    @Bind(R.id.dishesRecycler)
    RecyclerView dishesRecycler;
    @Bind(R.id.orderTotalMoney)
    TextView orderTotalMoney;
    @Bind(R.id.pay)
    TextView pay;
    @Bind(R.id.commentL)
    LinearLayout commentL;
    @Bind(R.id.commentRating)
    RatingBar commentRating;
    @Bind(R.id.comment)
    TextView comment;

    private OrderBean order;
    private List<OrderedDishes> datas;
    private OrderDishesAdapter adapter;
    private View emptyView;
    private View headerView;
    private PayPopWindow payPop;
    private ProgressPopupWindow progressPop;
    private AccountBean account;
    private float totalMoney;
    private int payType;
    private String payAmount;
    private DiningTableService service;
    private Call<BusniessBean> call;
    private List<DiningTableBean> diningTableAlls;
    private Call<BaseBean> saveCall;
    private JSONUtils<DiningTableBean> jsonUtils = new JSONUtils<DiningTableBean>();
    private DiningTableBean table;
    private CommentPopWindow commentPop;

    @OnClick(R.id.leftBarL)
    void leftBarL()
    {
        //关闭
        ActivityUtils.getInstance().closeSelf(ViewOrderActivity.this);
    }

    @OnClick(R.id.pay)
    void pay()
    {
        if(null!=payPop)
        {
            if(null!=datas&&!datas.isEmpty())
            {
                //获取用户信息
                account = CConfigure.obtainAccount(ViewOrderActivity.this);
                payPop.showPay(totalMoney, account.balance);
                payPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
            }
            else
            {
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "当前订单信息不完整，无法支付！请联系服务员。");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 80)
    public void onEvent(EventBusMsg msg)
    {
        switch (msg.getType())
        {
            case PAY_BALANCE_ERROR:
            {
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "您的余额不足，请选择其他方式支付");
            }
            break;
            case INFO_COMMENT_CANCEL:
            {
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "你取消了评论");
            }
            break;
            case INFO_COMMENT_SUCCESS:
            {
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "感谢你的评论");
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
            case BUNDLE_PAY:
            {
                Bundle bundle = msg.getBundle();
                payType = bundle.getInt("payType");
                payAmount = bundle.getString("payAmount");
                progressPop.showProgress(null);
                progressPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
                mHandler.postDelayed(runnablePay, 1000);
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_view_order);
        ButterKnife.bind(this);
        if(null==retrofit)
        {
            retrofit = RetrofitUtil.obtainRetrofit();
        }
        datas = new ArrayList<OrderedDishes>();
        emptyView = LayoutInflater.from(ViewOrderActivity.this).inflate(R.layout.layout_common_empty, null);
        headerView = LayoutInflater.from(ViewOrderActivity.this).inflate(R.layout.layout_common_header, null);
        payPop = new PayPopWindow(ViewOrderActivity.this, ViewOrderActivity.this, eventBus);
        progressPop = new ProgressPopupWindow(ViewOrderActivity.this, ViewOrderActivity.this);
        commentPop = new CommentPopWindow(ViewOrderActivity.this, ViewOrderActivity.this, eventBus);
        initTitle();
        initContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //检测手机底部返回菜单，关闭界面
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            //关闭
            ActivityUtils.getInstance().closeSelf(ViewOrderActivity.this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mHandler)
        {
            mHandler.removeCallbacks(runnableLoadData);
            mHandler.removeCallbacks(runnablePay);
        }
        if(null!=call)
        {
            call.cancel();
        }
        if(null!=saveCall)
        {
            saveCall.cancel();
        }
        if(null!=emptyView)
        {
            emptyView = null;
        }
        if(null!=headerView)
        {
            headerView = null;
        }
        if(null!=payPop)
        {
            payPop.dismissView();
        }
        if(null!=progressPop)
        {
            progressPop.dismissView();
        }
        if(null!=commentPop)
        {
            commentPop.dismissView();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void initTitle() {
        //设置标题栏布局
        toolBar.setTitle("");
        barTitle.setText(resources.getString(R.string.order_tag));
        CommonUtils.loadBackground(leftBarIcon, resources.getDrawable(R.mipmap.title_back));
        rightBarIcon.setVisibility(View.GONE);
        toolBar.setNavigationIcon(null);
    }

    @Override
    public void initContent() {
        Bundle bundle = this.getIntent().getExtras();
        order = (OrderBean) bundle.getSerializable("order");
        if(null!=order)
        {
            if(1==order.orderStatus)
            {
                commentL.setVisibility(View.VISIBLE);
                commentRating.setRating(order.rating);
                commentRating.setIsIndicator(true);
                comment.setText(order.comment);
            }
            else if(0==order.orderStatus)
            {
                commentL.setVisibility(View.GONE);
            }
            orderNo.append(order.orderId);
            service = retrofit.create(DiningTableService.class);
            Map<String, String> map = new HashMap<String, String>();
            map.put("table", "dininingTable");
            map.put("k", Base64.encodeToString("shop01".getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
            call = service.obtainDiningTables(map);
            call.enqueue(new Callback<BusniessBean>() {
                @Override
                public void onResponse(Call<BusniessBean> call, Response<BusniessBean> response) {
                    if(null!=call)
                    {
                        call.cancel();
                    }
                    if(null!=response.body())
                    {
                        BusniessBean busniess = response.body();
                        if("200".equals(busniess.retCode))
                        {
                            String diningTablejson = busniess.result.v;
                            diningTableAlls = jsonUtils.jsonToList(diningTablejson, DiningTableBean.class);
                            table = CommonUtils.findCurrentTable(diningTableAlls, order.diningTableId);
                            if(null!=table)
                            {
                                orderDingTableNo.append(table.diningTableNo+resources.getString(R.string.dingtable_unit));
                            }
                            else
                            {
                                orderDingTableNo.setText("");
                                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "餐桌数据查询失败");
                            }
                        }
                        else
                        {
                            orderDingTableNo.setText("");
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "餐桌数据查询失败");
                        }
                    }
                    else
                    {
                        orderDingTableNo.setText("");
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "餐桌数据查询失败");
                    }
                }

                @Override
                public void onFailure(Call<BusniessBean> call, Throwable t) {
                    if(null!=call)
                    {
                        call.cancel();
                    }
                    if(null!=orderDingTableNo)
                    {
                        orderDingTableNo.setText("");
                    }
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "餐桌数据查询失败");
                }
            });
            orderStatus.append((0==order.orderStatus)?resources.getString(R.string.order_status_nopay):resources.getString(R.string.order_status_pay));
            if(0==order.orderStatus){
                pay.setVisibility(View.VISIBLE);
            }
            else if(1==order.orderStatus)
            {
                pay.setVisibility(View.GONE);
            }
            orderPeoples.append(String.valueOf(order.peoples)+resources.getString(R.string.prople_unit));
            List<AccountBean> accountList = AccountBean.find(AccountBean.class, "userId = ?", String.valueOf(order.userId));
            if(null!=accountList&&!accountList.isEmpty())
            {
                orderUser.append(accountList.get(0).realName);
            }
            else
            {
                orderUser.setText("");
            }
            orderTime.append(order.orderTime);
            // 错列网格布局
            dishesRecycler.setLayoutManager(new LinearLayoutManager(ViewOrderActivity.this));
            adapter = new OrderDishesAdapter(ViewOrderActivity.this, datas);
            dishesRecycler.setAdapter(adapter);
            dishesRecycler.setItemAnimator(new ScaleInAnimator()); // 默认动画
            dishesRecycler.addItemDecoration(new RecycleViewDividerForList(ViewOrderActivity.this, LinearLayoutManager.HORIZONTAL));
            mHandler.postDelayed(runnableLoadData, 500);
        }
    }

    Runnable runnablePay = new Runnable() {
        @Override
        public void run() {

            //获取订单
            List<OrderBean> orderList = OrderBean.find(OrderBean.class, "orderStatus = ? and userId = ?", "0", String.valueOf(account.userId));
            if(null!=orderList&&!orderList.isEmpty())
            {
                final OrderBean order = orderList.get(0);
                //更改餐桌状态
                table.diningTableStatus = 0;
                diningTableAlls = CommonUtils.fixedDiningTableStatus(diningTableAlls, table);
                //保存餐桌
                Map<String, String> map = new HashMap<String, String>();
                map.put("table", "dininingTable");
                map.put("k", Base64.encodeToString("shop01".getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
                map.put("v", Base64.encodeToString(jsonUtils.listToJson(diningTableAlls).getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
                saveCall = service.commitTables(map);
                saveCall.enqueue(new Callback<BaseBean>() {
                    @Override
                    public void onResponse(Call<BaseBean> call, Response<BaseBean> response) {
                        if(null!=saveCall)
                        {
                            saveCall.cancel();
                        }
                        if(null!=response.body())
                        {
                            BaseBean base = response.body();
                            if("200".equals(base.retCode))
                            {
                                //设置订单总价
                                order.orderPrice = orderTotalMoney.getText().toString();
                                //设置结算时间
                                order.payTime = CommonUtils.obtainTime();
                                //设置订单状态
                                order.orderStatus = 1;
                                order.save();
                                //页面更改状态
                                orderStatus.setText(resources.getString(R.string.order_status)+resources.getString(R.string.order_status_pay));
                                orderStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark));
                                pay.setVisibility(View.GONE);
                                if(null!=progressPop)
                                {
                                    progressPop.dismissView();
                                }
                                switch (payType)
                                {
                                    case 0:
                                    {
                                        //扣除余额
                                        AccountBean account = CConfigure.obtainAccount(ViewOrderActivity.this);
                                        List<AccountBean> accountList = AccountBean.find(AccountBean.class, "userId = ?", String.valueOf(account.userId));
                                        if(null!=accountList&&!accountList.isEmpty())
                                        {
                                            AccountBean user = accountList.get(0);
                                            user.balance = user.balance - Float.parseFloat(payAmount);
                                            account.save();
                                            account.balance = account.balance - Float.parseFloat(payAmount);
                                            CConfigure.saveAccount(ViewOrderActivity.this, account);
                                            //余额支付
                                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "您使用余额，支付了"+payAmount+"元");
                                            eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.ORDERS_REFLUSH));
                                        }
                                        else
                                        {
                                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "余额支付失败");
                                        }
                                    }
                                    break;
                                    case 1:
                                    {
                                        //微信支付
                                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "您使用微信，支付了"+payAmount+"元");
                                        eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.ORDERS_REFLUSH));
                                    }
                                    break;
                                    case 2:
                                    {
                                        //支付宝支付
                                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), "您使用支付宝，支付了"+payAmount+"元");
                                        eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.ORDERS_REFLUSH));
                                    }
                                    break;
                                    default:
                                        break;
                                }
                                //弹出评论信息框
                                if(null!=commentPop)
                                {
                                    commentPop.showComment(order);
                                    commentPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
                                }
                            }
                            else
                            {
                                if(null!=progressPop)
                                {
                                    progressPop.dismissView();
                                }
                                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), resources.getString(R.string.order_pay_error));
                            }
                        }
                        else
                        {
                            if(null!=progressPop)
                            {
                                progressPop.dismissView();
                            }
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), resources.getString(R.string.order_pay_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseBean> call, Throwable t) {
                        if(null!=saveCall)
                        {
                            saveCall.cancel();
                        }
                        if(null!=progressPop)
                        {
                            progressPop.dismissView();
                        }
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), resources.getString(R.string.order_pay_error));
                    }
                });
            }
            else
            {
                if(null!=progressPop)
                {
                    progressPop.dismissView();
                }
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ViewOrderActivity.this), resources.getString(R.string.order_pay_error));
            }
        }
    };

    Runnable runnableLoadData = new Runnable() {
        @Override
        public void run() {

            //获取订单菜品数据
            List<OrderDishesBean> orderDishesList = OrderDishesBean.find(OrderDishesBean.class, "orderId = ?", order.orderId);
            if(null!=orderDishesList&&!orderDishesList.isEmpty())
            {
               List<OrderedDishes> orderedDishesList = new ArrayList<OrderedDishes>();
                for(OrderDishesBean orderDishes:orderDishesList)
                {
                    OrderedDishes orderedDishes = new OrderedDishes();
                    orderedDishes.dishesId = orderDishes.dishesId;
                    orderedDishes.num = orderDishes.dishesNum;
                    //根据dishesId获取菜品信息
                    List<DishesBean> dishesList = DishesBean.find(DishesBean.class, "dishesId = ? and dishesStatus = ?", String.valueOf(orderDishes.dishesId), "0");
                    if(null!=dishesList&&!dishesList.isEmpty())
                    {
                        DishesBean dishes = dishesList.get(0);
                        orderedDishes.dishesName = dishes.dishesName;
                        orderedDishes.sunTotalMoney = Float.parseFloat(dishes.dishesPrice) * orderDishes.dishesNum;
                    }
                    else
                    {
                        orderedDishes.dishesName = "未知";
                        orderedDishes.sunTotalMoney = 0.0f;
                    }
                    orderedDishesList.add(orderedDishes);
                }
                adapter.setHeaderView(headerView);
                datas.clear();
                datas.addAll(orderedDishesList);
                adapter.notifyDataSetChanged();
                //计算总价
                totalMoney = 0.00f;
                for(OrderedDishes orderedDishes:orderedDishesList)
                {
                    totalMoney += orderedDishes.sunTotalMoney;
                }
                orderTotalMoney.append(totalMoney+resources.getString(R.string.money_unit));
            }
            else
            {
                datas.clear();
                adapter.notifyDataSetChanged();
                adapter.setEmptyView(emptyView);
                //计算总价
                orderTotalMoney.append(0.00+resources.getString(R.string.money_unit));
            }
        }
    };
}
