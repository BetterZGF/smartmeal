package com.aaron.group.smartmeal.ui.auxiliary;

import android.os.Bundle;
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
import com.aaron.group.smartmeal.adapter.DishesAdapter;
import com.aaron.group.smartmeal.adapter.DishesManagerAdapter;
import com.aaron.group.smartmeal.base.BaseActivity;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.listener.OnDeleteClickListener;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.RecycleViewDividerForList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;

/**
 * 说明:

 */

public class DishesManagerActivity extends BaseActivity {

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
    @Bind(R.id.dishesManagerRecycler)
    RecyclerView dishesManagerRecycler;

    private List<DishesBean> datas;
    private DishesManagerAdapter adapter;
    private View emptyView;

    @OnClick(R.id.leftBarL)
    void leftBarL()
    {
        //关闭
        ActivityUtils.getInstance().closeSelf(DishesManagerActivity.this);
    }

    @OnClick(R.id.rightBarL)
    void rightBarL()
    {
        //添加菜品
        ActivityUtils.getInstance().showActivityAnima(DishesManagerActivity.this, DishesAddModifyActivity.class, R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_dishes_manager);
        ButterKnife.bind(this);
        emptyView = LayoutInflater.from(DishesManagerActivity.this).inflate(R.layout.layout_common_empty, null);
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
            ActivityUtils.getInstance().closeSelf(DishesManagerActivity.this);
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
        barTitle.setText("菜品管理");
        CommonUtils.loadBackground(leftBarIcon, resources.getDrawable(R.mipmap.title_back));
        CommonUtils.loadBackground(rightBarIcon, resources.getDrawable(R.mipmap.add));
        toolBar.setNavigationIcon(null);
    }

    @Override
    public void initContent() {
        // 错列网格布局
        dishesManagerRecycler.setLayoutManager(new LinearLayoutManager(DishesManagerActivity.this));
        datas = new ArrayList<DishesBean>();
        adapter = new DishesManagerAdapter(DishesManagerActivity.this, datas);
        dishesManagerRecycler.setAdapter(adapter);
        dishesManagerRecycler.addItemDecoration(new RecycleViewDividerForList(DishesManagerActivity.this, LinearLayoutManager.HORIZONTAL));
        dishesManagerRecycler.setItemAnimator(new ScaleInLeftAnimator()); // 默认动画
        mHandler.post(runnableLoadData);
        adapter.setmOnItemClickListener(new com.aaron.group.smartmeal.listener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //修改菜品
                Bundle bundle = new Bundle();
                bundle.putSerializable("dishes", datas.get(position));
                ActivityUtils.getInstance().showActivityAnima(DishesManagerActivity.this, DishesAddModifyActivity.class, bundle, R.anim.zoomin, R.anim.zoomout);
            }
        });

        adapter.setmIDeleteBtnClickListener(new OnDeleteClickListener() {
            @Override
            public void onDeleteBtnCilck(View view, int position) {

                DishesBean dishes = datas.get(position);
                dishes.dishesStatus = 1;
                dishes.save();
                mHandler.post(runnableLoadData);
            }
        });
    }

    Runnable runnableLoadData = new Runnable() {
        @Override
        public void run() {
            List<DishesBean> dishesList = DishesBean.listAll(DishesBean.class, "dishesStatus ASC");
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
                adapter.setEmptyView(emptyView);
            }
        }
    };
}
