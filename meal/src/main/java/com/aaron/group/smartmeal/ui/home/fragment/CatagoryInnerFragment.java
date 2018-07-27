package com.aaron.group.smartmeal.ui.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.adapter.DishesAdapter;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.DiningTableBean;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.bean.OrderDishesBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.ui.home.HomeActivity;
import com.aaron.group.smartmeal.utils.CConfigure;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.RecyclerDividerForOther;
import com.aaron.group.smartmeal.utils.SnackbarUi;
import com.aaron.group.smartmeal.widget.LoginPopWindow;
import com.aaron.group.smartmeal.widget.MsgPopWindow;
import com.aaron.group.smartmeal.widget.OrderInfoPopWindow;
import com.aaron.group.smartmeal.widget.OrderPopWindow;
import com.aaron.group.smartmeal.widget.SettingPopWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * 说明:

 */

public class CatagoryInnerFragment extends Fragment {

    @Bind(R.id.catagoryRecyclerView)
    RecyclerView catagoryRecyclerView;

    private HomeActivity rootAty;
    private View rootView;
    private List<DishesBean> datas;
    private DishesAdapter adapter;
    private int dishesCatagoryId;
    private ViewGroup parentV;
    private EventBus eventBus;
    private OrderInfoPopWindow orderPop;

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 80)
    public void onEvent(EventBusMsg msg)
    {
        switch (msg.getType())
        {
            case POP_ORDERINFO_CANCEL:
            {
                //关闭菜品详情界面
                SnackbarUi.showMsgInfo(CommonUtils.getRootView(rootAty), "菜品下单，请去首页");
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
        rootView = inflater.inflate(R.layout.frgment_inner_catagory, container, false);
        ButterKnife.bind(this, rootView);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        orderPop = new OrderInfoPopWindow(rootAty, rootAty, eventBus);
        parentV = container;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            dishesCatagoryId = bundle.getInt("catagoryId");
        }
        // 错列网格布局
        catagoryRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        datas = new ArrayList<DishesBean>();
        adapter = new DishesAdapter(rootAty, datas);
        catagoryRecyclerView.setAdapter(adapter);
        catagoryRecyclerView.addItemDecoration(new RecyclerDividerForOther(rootAty, 2));
        catagoryRecyclerView.setItemAnimator(new ScaleInAnimator()); // 默认动画
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
            List<DishesBean> dishesList = DishesBean.find(DishesBean.class, "categoryId = ? and dishesStatus = ?", String.valueOf(dishesCatagoryId), "0");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=eventBus)
        {
            eventBus.unregister(this);
        }
        if(null!=orderPop)
        {
            orderPop.dismissView();
        }
        if(null!=rootAty.mHandler)
        {
            rootAty.mHandler.removeCallbacks(runnableLoadData);
        }
        if(null!=parentV)
        {
            parentV = null;
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootAty = null;
    }
}
