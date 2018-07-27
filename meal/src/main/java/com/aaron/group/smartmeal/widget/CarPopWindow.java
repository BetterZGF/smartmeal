package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.adapter.CarAdapter;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.bean.OrderDishesBean;
import com.aaron.group.smartmeal.bean.OrderedDishes;
import com.aaron.group.smartmeal.listener.OnCancelListener;
import com.aaron.group.smartmeal.listener.OnCarListener;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.ui.order.ViewOrderActivity;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.RecycleViewDividerForList;
import com.aaron.group.smartmeal.utils.SnackbarUi;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * 说明: 购物车弹出框

 */

public class CarPopWindow extends PopupWindow {
    /**
     * Context
     */
    private Context context;
    /**
     * Activity
     */
    private Activity aty;

    private Handler handler;
    private View emptyView;

    private List<OrderedDishes> datas;
    private CarAdapter adapter;
    /**
     * 确认按钮点击事件
     */
    private OnSureListener onSureListener;
    private OnCancelListener onCancelListener;

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnSureListener(OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }

    public
    CarPopWindow ( Context context, Activity aty, Handler handler) {
        this.context = context;
        this.aty = aty;
        this.handler = handler;
    }

    public
    void showCar () {

        /**
         * 实例化进度条组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_car,
                null
        );
        emptyView = LayoutInflater.from(context).inflate(R.layout.layout_common_empty, null);
        // 获取具体UI组件
        final RecyclerView cars = (RecyclerView) view.findViewById(R.id.cars);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.closeBtn);
        RelativeLayout btnsureL = (RelativeLayout) view.findViewById(R.id.btnsureL);
        //加载菜品清单
        datas = new ArrayList<OrderedDishes>();
        cars.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CarAdapter(context, datas);
        cars.setAdapter(adapter);
        adapter.setOnCarListener(new OnCarListener() {
            @Override
            public void plus(View view, int position) {
                //加
                OrderedDishes dishes = datas.get(position);
                dishes.num +=1;
                dishes.sunTotalMoney = dishes.dishesPrice*dishes.num;
                adapter.notifyDataSetChanged();
                //保存
                List<OrderDishesBean> orderDishesList = OrderDishesBean.find(OrderDishesBean.class, "orderId = ? and dishesId = ?", dishes.orderId, String.valueOf(dishes.dishesId));
                if(null!=orderDishesList&&!orderDishesList.isEmpty())
                {
                    OrderDishesBean orderDishes = orderDishesList.get(0);
                    orderDishes.dishesNum = dishes.num;
                    orderDishes.save();
                }
            }

            @Override
            public void less(View view, int position) {
                //减
                OrderedDishes dishes = datas.get(position);
                dishes.num -=1;
                dishes.sunTotalMoney = dishes.dishesPrice*dishes.num;
                adapter.notifyDataSetChanged();
                //保存
                List<OrderDishesBean> orderDishesList = OrderDishesBean.find(OrderDishesBean.class, "orderId = ? and dishesId = ?", dishes.orderId, String.valueOf(dishes.dishesId));
                if(null!=orderDishesList&&!orderDishesList.isEmpty())
                {
                    OrderDishesBean orderDishes = orderDishesList.get(0);
                    orderDishes.dishesNum = dishes.num;
                    orderDishes.save();
                }
            }

            @Override
            public void del(View view, int position) {
                //删除
                OrderedDishes dishes = datas.get(position);
                adapter.remove(position);
                //保存
                List<OrderDishesBean> orderDishesList = OrderDishesBean.find(OrderDishesBean.class, "orderId = ? and dishesId = ?", dishes.orderId, String.valueOf(dishes.dishesId));
                if(null!=orderDishesList&&!orderDishesList.isEmpty())
                {
                    OrderDishesBean orderDishes = orderDishesList.get(0);
                    orderDishes.delete();
                }
            }
        });
        cars.setItemAnimator(new ScaleInAnimator()); // 默认动画
        cars.addItemDecoration(new RecycleViewDividerForList(context, LinearLayoutManager.HORIZONTAL));
        handler.postDelayed(runnableLoadData, 500);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null==onCancelListener)
                {
                    dismissView();
                }
                else
                {
                    onCancelListener.onCancel();
                    dismissView();
                }
            }
        });

        btnsureL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去结算
                AccountBean account = CConfigure.obtainAccount(context);
                //获取订单
                List<OrderBean> orderList = OrderBean.find(OrderBean.class, "orderStatus = ? and userId = ?", "0", String.valueOf(account.userId));
                if(null!=orderList&&!orderList.isEmpty())
                {
                    if(null!=datas&&!datas.isEmpty())
                    {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("order", orderList.get(0));
                        ActivityUtils.getInstance().showActivity(aty, ViewOrderActivity.class, bundle);
                    }
                    else
                    {
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(aty), context.getResources().getString(R.string.info_order_null));
                    }
                }
                else
                {
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(aty), context.getResources().getString(R.string.info_order_null));
                }
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
        CommonUtils.backgroundAlpha(aty, 0.4f);
    }

    public void dismissView()
    {
        if(null!=handler)
        {
            handler.removeCallbacks(runnableLoadData);
        }
        if(null!=emptyView)
        {
            emptyView = null;
        }
        setOnDismissListener ( new PoponDismissListener( aty ) );
        dismiss ();
    }

    Runnable runnableLoadData = new Runnable() {
        @Override
        public void run() {
            AccountBean account = CConfigure.obtainAccount(context);
            //获取订单
            List<OrderBean> orderList = OrderBean.find(OrderBean.class, "orderStatus = ? and userId = ?", "0", String.valueOf(account.userId));
            if(null!=orderList&&!orderList.isEmpty())
            {
                //获取订单菜品数据
                List<OrderDishesBean> orderDishesList = OrderDishesBean.find(OrderDishesBean.class, "orderId = ?", orderList.get(0).orderId);
                if(null!=orderDishesList&&!orderDishesList.isEmpty())
                {
                    List<OrderedDishes> orderedDishesList = new ArrayList<OrderedDishes>();
                    for(OrderDishesBean orderDishes:orderDishesList)
                    {
                        OrderedDishes orderedDishes = new OrderedDishes();
                        orderedDishes.orderId = orderList.get(0).orderId;
                        orderedDishes.dishesId = orderDishes.dishesId;
                        orderedDishes.num = orderDishes.dishesNum;
                        //根据dishesId获取菜品信息
                        List<DishesBean> dishesList = DishesBean.find(DishesBean.class, "dishesId = ? and dishesStatus = ?", String.valueOf(orderDishes.dishesId), "0");
                        if(null!=dishesList&&!dishesList.isEmpty())
                        {
                            DishesBean dishes = dishesList.get(0);
                            orderedDishes.dishesName = dishes.dishesName;
                            orderedDishes.dishesPrice = Float.parseFloat(dishes.dishesPrice);
                            orderedDishes.sunTotalMoney = Float.parseFloat(dishes.dishesPrice) * orderDishes.dishesNum;
                        }
                        else
                        {
                            orderedDishes.dishesName = "未知";
                            orderedDishes.dishesPrice = 0.0f;
                            orderedDishes.sunTotalMoney = 0.0f;
                        }
                        orderedDishesList.add(orderedDishes);
                    }
                    datas.clear();
                    datas.addAll(orderedDishesList);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    datas.clear();
                    adapter.notifyDataSetChanged();
                    adapter.setEmptyView(emptyView);
                }
            }
            else
            {
                datas.clear();
                adapter.notifyDataSetChanged();
                adapter.setEmptyView(emptyView);
            }
        }
    };
}
