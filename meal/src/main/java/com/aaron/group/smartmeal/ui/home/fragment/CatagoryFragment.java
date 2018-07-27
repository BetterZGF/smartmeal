package com.aaron.group.smartmeal.ui.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.adapter.TabPagerStateAdapter;
import com.aaron.group.smartmeal.bean.DishesCategoryBean;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.ui.home.HomeActivity;
import com.aaron.group.smartmeal.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

/**
 * 说明:

 */

public class CatagoryFragment extends Fragment {

    @Bind(R.id.catagoryTabs)
    VerticalTabLayout catagoryTabs;
    @Bind(R.id.catagoryViewpager)
    ViewPager catagoryViewpager;

    private HomeActivity rootAty;
    private View rootView;
    private TabPagerStateAdapter adapter;
    private List<Fragment> fragments;
    private List<String> vTabs;
    private CatagoryInnerFragment innerFragment;

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
        rootView = inflater.inflate(R.layout.fragment_catagory, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //获取菜品类型数据
        vTabs = new ArrayList<String>();
        fragments = new ArrayList<Fragment>();
        Iterator<DishesCategoryBean> itDishesCategory = DishesCategoryBean.findAll(DishesCategoryBean.class);
        if(itDishesCategory.hasNext())
        {
            while (itDishesCategory.hasNext())
            {
                DishesCategoryBean dishesCategory = itDishesCategory.next();
                innerFragment = new CatagoryInnerFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("catagoryId", dishesCategory.categoryId);
                innerFragment.setArguments(bundle1);
                fragments.add(innerFragment);
                vTabs.add(dishesCategory.categoryName);
            }
        }
        adapter = new TabPagerStateAdapter(this.getChildFragmentManager(), fragments);
        catagoryViewpager.setAdapter(adapter);
        catagoryTabs.setupWithViewPager(catagoryViewpager);
        catagoryViewpager.setOffscreenPageLimit(1);
        catagoryTabs.setTabMode(VerticalTabLayout.TAB_MODE_SCROLLABLE);
        if(null!=vTabs&&!vTabs.isEmpty())
        {
            int size = vTabs.size();
            for(int i=0; i<size; i++)
            {
                //初始化tabs
                TabView tabView = catagoryTabs.getTabAt(i);
                tabView.setTitle(new TabView.TabTitle.Builder().setContent(vTabs.get(i)).setTextColor(0xff2995fa, 0xff0b5499).setTextSize(CommonUtils.px2dp(rootAty, rootAty.getResources().getDimension(R.dimen.text_size_14))).build());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootAty = null;
    }
}
