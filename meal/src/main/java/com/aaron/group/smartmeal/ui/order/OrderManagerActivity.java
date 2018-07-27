package com.aaron.group.smartmeal.ui.order;

import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.adapter.OrderAdapter;
import com.aaron.group.smartmeal.adapter.OrderDishesAdapter;
import com.aaron.group.smartmeal.base.BaseActivity;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.bean.OrderDishesBean;
import com.aaron.group.smartmeal.bean.OrderedDishes;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.listener.OnItemClickListener;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.RecycleViewDividerForList;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * 说明:

 */

public class OrderManagerActivity extends BaseActivity {

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
    @Bind(R.id.orders)
    RecyclerView orders;

    private List<OrderBean> datas;
    private OrderAdapter adapter;
    private View emptyView;

    @OnClick(R.id.leftBarL)
    void leftBarL()
    {
        //关闭
        ActivityUtils.getInstance().closeSelf(OrderManagerActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 80)
    public void onEvent(EventBusMsg msg)
    {
        switch (msg.getType())
        {
            case ORDERS_REFLUSH:
            {
                mHandler.postDelayed(runnableLoadData, 500);
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_order_manager);
        ButterKnife.bind(this);
        datas = new ArrayList<OrderBean>();
        emptyView = LayoutInflater.from(OrderManagerActivity.this).inflate(R.layout.layout_common_empty, null);
        initTitle();
        initContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            //关闭
            ActivityUtils.getInstance().closeSelf(OrderManagerActivity.this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mHandler)
        {
            mHandler.removeCallbacks(runnableLoadData);
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void initTitle() {
        //设置标题栏布局
        toolBar.setTitle("");
        barTitle.setText("订单管理");
        CommonUtils.loadBackground(leftBarIcon, resources.getDrawable(R.mipmap.title_back));
        rightBarIcon.setVisibility(View.GONE);
        toolBar.setNavigationIcon(null);
    }

    @Override
    public void initContent() {
        // 错列网格布局
        orders.setLayoutManager(new LinearLayoutManager(OrderManagerActivity.this));
        adapter = new OrderAdapter(OrderManagerActivity.this, datas);
        orders.setAdapter(adapter);
        orders.setItemAnimator(new ScaleInAnimator()); // 默认动画
        orders.addItemDecoration(new RecycleViewDividerForList(OrderManagerActivity.this, LinearLayoutManager.HORIZONTAL));
        adapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OrderBean order = datas.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                ActivityUtils.getInstance().showActivity(OrderManagerActivity.this, ViewOrderActivity.class, bundle);
            }
        });
        mHandler.postDelayed(runnableLoadData, 500);
    }

    Runnable runnableLoadData = new Runnable() {
        @Override
        public void run() {
            AccountBean account = CConfigure.obtainAccount(OrderManagerActivity.this);
            List<OrderBean> orders = OrderBean.find(OrderBean.class, "userId = ?", String.valueOf(account.userId));
            if(null!=orders&&!orders.isEmpty())
            {
                datas.clear();
                datas.addAll(orders);
                adapter.notifyDataSetChanged();
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
