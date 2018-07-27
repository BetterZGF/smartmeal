package com.aaron.group.smartmeal.ui.auxiliary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.adapter.DiningTableAdapter;
import com.aaron.group.smartmeal.base.BaseActivity;
import com.aaron.group.smartmeal.base.BaseBean;
import com.aaron.group.smartmeal.bean.BusniessBean;
import com.aaron.group.smartmeal.bean.DiningTableBean;
import com.aaron.group.smartmeal.bean.DiningTableShowBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.retrofit.RetrofitUtil;
import com.aaron.group.smartmeal.retrofit.service.DiningTableService;
import com.aaron.group.smartmeal.ui.order.ViewOrderActivity;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.JSONUtils;
import com.aaron.group.smartmeal.utils.RecyclerDividerForOther;
import com.aaron.group.smartmeal.utils.SnackbarUi;
import com.aaron.group.smartmeal.widget.ConfirmPopWin;
import com.aaron.group.smartmeal.widget.ProgressPopupWindow;

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
 * 说明: 换桌申请界面

 */

public class ChangeDingTableActivity extends BaseActivity {

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
    @Bind(R.id.currentDingTable)
    TextView currentDingTable;
    @Bind(R.id.dingTableRecycler)
    RecyclerView dingTableRecycler;

    private OrderBean order;
    private List<DiningTableShowBean> datas;
    private DiningTableAdapter adapter;
    private View emptyView;
    private ConfirmPopWin confirmPop;
    private ProgressPopupWindow progressPop;
    private DiningTableShowBean diningTableShow;
    private DiningTableService service;
    private Call<BusniessBean> call;
    private List<DiningTableBean> diningTableAlls;
    private Call<BaseBean> saveCall;
    private JSONUtils<DiningTableBean> jsonUtils = new JSONUtils<DiningTableBean>();
    private List<DiningTableShowBean> diningTableShows = new ArrayList<DiningTableShowBean>();

    @OnClick(R.id.leftBarL)
    void leftBarL()
    {
        //关闭
        ActivityUtils.getInstance().closeSelf(ChangeDingTableActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_change_dingtable);
        ButterKnife.bind(this);
        if(null==retrofit)
        {
            retrofit = RetrofitUtil.obtainRetrofit();
        }
        Bundle bundle = this.getIntent().getExtras();
        order = (OrderBean) bundle.getSerializable("order");
        emptyView = LayoutInflater.from(ChangeDingTableActivity.this).inflate(R.layout.layout_common_empty, null);
        confirmPop = new ConfirmPopWin(ChangeDingTableActivity.this, ChangeDingTableActivity.this);
        progressPop = new ProgressPopupWindow(ChangeDingTableActivity.this, ChangeDingTableActivity.this);
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
            ActivityUtils.getInstance().closeSelf(ChangeDingTableActivity.this);
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
        if(null!=confirmPop)
        {
            confirmPop.dismissView();
        }
        if(null!=progressPop)
        {
            progressPop.dismissView();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void initTitle() {
        //设置标题栏布局
        toolBar.setTitle("");
        barTitle.setText(resources.getString(R.string.changedingtable_tag));
        CommonUtils.loadBackground(leftBarIcon, resources.getDrawable(R.mipmap.title_back));
        rightBarIcon.setVisibility(View.GONE);
        toolBar.setNavigationIcon(null);
    }

    @Override
    public void initContent() {

        datas = new ArrayList<DiningTableShowBean>();
        if(null!=order)
        {
            //查询当前的餐桌
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
                            DiningTableBean table = CommonUtils.findCurrentTable(diningTableAlls, order.diningTableId);
                            if(null!=table)
                            {
                                currentDingTable.append(table.diningTableNo+resources.getString(R.string.dingtable_unit));
                            }
                            else
                            {
                                currentDingTable.setText("");
                            }
                        }
                        else
                        {
                            currentDingTable.setText("");
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), "餐桌数据查询失败");
                        }
                    }
                    else
                    {
                        currentDingTable.setText("");
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), "餐桌数据查询失败");
                    }
                }

                @Override
                public void onFailure(Call<BusniessBean> call, Throwable t) {
                    if(null!=call)
                    {
                        call.cancel();
                    }
                    currentDingTable.setText("");
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), "餐桌数据查询失败");
                }
            });
        }
        // 错列网格布局
        dingTableRecycler.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        adapter = new DiningTableAdapter(ChangeDingTableActivity.this, datas);
        dingTableRecycler.setAdapter(adapter);
        dingTableRecycler.addItemDecoration(new RecyclerDividerForOther(ChangeDingTableActivity.this, 3));
        dingTableRecycler.setItemAnimator(new ScaleInAnimator()); // 默认动画
        adapter.setmOnItemClickListener(new com.aaron.group.smartmeal.listener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                //点击换桌
                if(null!=confirmPop)
                {
                    confirmPop.showUi(resources.getString(R.string.confirm_change_diningtable));
                    confirmPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
                    confirmPop.setOnSureListener(new OnSureListener() {
                        @Override
                        public void onSure() {
                            if(null!=progressPop)
                            {
                                progressPop.showProgress(null);
                                progressPop.showAtLocation(toolBar, Gravity.CENTER, 0, 0);
                            }
                            diningTableShow = datas.get(position);
                            mHandler.postDelayed(runnableChangeDiningtable, 3000);
                        }
                    });
                }
            }
        });
        mHandler.post(runnableLoadData);
    }

    Runnable runnableLoadData = new Runnable() {
        @Override
        public void run() {
            service = retrofit.create(DiningTableService.class);
            Map<String, String> map = new HashMap<String, String>();
            map.put("table", "dininingTable");
            map.put("k", Base64.encodeToString("shop01".getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
            call = service.obtainDiningTables(map);
            call.enqueue(new Callback<BusniessBean>() {
                @Override
                public void onResponse(Call<BusniessBean> call, Response<BusniessBean> response) {
                    if(null!=response.body())
                    {
                        BusniessBean busniess = response.body();
                        if("200".equals(busniess.retCode))
                        {
                            String diningTablejson = busniess.result.v;
                            diningTableAlls = jsonUtils.jsonToList(diningTablejson, DiningTableBean.class);
                            //获取空桌数据
                            if(null!=diningTableAlls&&!diningTableAlls.isEmpty())
                            {
                                for(DiningTableBean diningTable:diningTableAlls)
                                {
                                    if(0==diningTable.diningTableStatus)
                                    {
                                        DiningTableShowBean diningTableShow = new DiningTableShowBean();
                                        diningTableShow.diningTable = diningTable;
                                        diningTableShow.isChoice = false;
                                        diningTableShows.add(diningTableShow);
                                    }
                                }
                                datas.clear();
                                datas.addAll(diningTableShows);
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                datas.clear();
                                adapter.notifyDataSetChanged();
                                adapter.setEmptyView(emptyView);
                                SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), resources.getString(R.string.change_dingtable_no));
                            }
                        }
                        else
                        {
                            datas.clear();
                            adapter.notifyDataSetChanged();
                            adapter.setEmptyView(emptyView);
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), resources.getString(R.string.change_dingtable_no));
                        }
                    }
                    else
                    {
                        datas.clear();
                        adapter.notifyDataSetChanged();
                        adapter.setEmptyView(emptyView);
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), resources.getString(R.string.change_dingtable_no));
                    }
                }

                @Override
                public void onFailure(Call<BusniessBean> call, Throwable t) {
                    datas.clear();
                    adapter.notifyDataSetChanged();
                    adapter.setEmptyView(emptyView);
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), resources.getString(R.string.change_dingtable_no));
                }
            });
        }
    };

    Runnable runnableChangeDiningtable = new Runnable() {
        @Override
        public void run() {
            //选中餐桌状态设置为忙碌
            final int choiceDiningTableId = diningTableShow.diningTable.diningTableId;
            int oldDiningTableId = order.diningTableId;
            DiningTableBean choiceDiningTable = CommonUtils.findCurrentTable(diningTableAlls, choiceDiningTableId);
            choiceDiningTable.diningTableStatus = 1;
            diningTableAlls = CommonUtils.fixedDiningTableStatus(diningTableAlls, choiceDiningTable);
            DiningTableBean oldDiningTable = CommonUtils.findCurrentTable(diningTableAlls, oldDiningTableId);
            oldDiningTable.diningTableStatus = 0;
            diningTableAlls = CommonUtils.fixedDiningTableStatus(diningTableAlls, oldDiningTable);
            //保存餐座数据
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
                            //换桌
                            if(null!=order)
                            {
                                order.diningTableId = choiceDiningTableId;
                                order.save();
                            }
                            //刷新列表，提示已经选定换桌
                            CommonUtils.changeTable(diningTableShows, diningTableShow);
                            adapter.notifyDataSetChanged();
                            //界面当前桌面编号调整
                            currentDingTable.setText(resources.getString(R.string.current_dingtable)+diningTableShow.diningTable.diningTableNo+resources.getString(R.string.dingtable_unit));
                            if(null!=progressPop)
                            {
                                progressPop.dismissView();
                            }
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), resources.getString(R.string.change_dingtable_success));
                        }
                        else
                        {
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), "换桌失败");
                        }
                    }
                    else
                    {
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), "换桌失败");
                    }
                }

                @Override
                public void onFailure(Call<BaseBean> call, Throwable t) {
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(ChangeDingTableActivity.this), "换桌失败");
                }
            });
        }
    };
}
