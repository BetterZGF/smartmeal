package com.aaron.group.smartmeal.ui.auxiliary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.print.PageRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.base.BaseActivity;
import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.BusniessBean;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.DishesCategoryBean;
import com.aaron.group.smartmeal.retrofit.RetrofitUtil;
import com.aaron.group.smartmeal.retrofit.service.LicenceService;
import com.aaron.group.smartmeal.ui.home.HomeActivity;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.aaron.group.smartmeal.utils.SnackbarUi;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 说明: 系统启动界面，在该界面中初始化一些资源

 */

public class SplashActivity extends BaseActivity {

    /**
     * 获取startPageL组件
     */
    @Bind(R.id.startPageL)
    LinearLayout startPageL;

    /**
     *  背景资源图片
     */
    private Drawable bgDraw;
    private Snackbar snackbar;
    private Iterator<AccountBean> itAccount;
    private Iterator<DishesCategoryBean> itDishesCategory;
    private Iterator<DishesBean> itDishes;
    private LicenceService service;
    private Call<BusniessBean> call;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //设置全屏，去掉系统标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //设置布局文件
        this.setContentView(R.layout.layout_splash);
        //采用ButterKnife组件，以注解方式替代findViewById获取view
        ButterKnife.bind(this);
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
            ActivityUtils.getInstance().closeSelf(SplashActivity.this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=call)
        {
            call.cancel();
        }
        if(null!=mHandler)
        {
            //销毁子线程
            mHandler.removeCallbacks(runnableAnimation);
            mHandler.removeCallbacks(runnableSkip);
        }
        if(null!=bgDraw)
        {
            //销毁背景
            bgDraw.setCallback(null);
        }
        //回收ButterKnife注解资源
        ButterKnife.unbind(this);
    }

    @Override
    public void initTitle() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initContent() {
        bgDraw = resources.getDrawable(R.mipmap.splash_bg);
        CommonUtils.loadBackground(startPageL, bgDraw);
        itAccount = AccountBean.findAll(AccountBean.class);
        itDishesCategory = DishesCategoryBean.findAll(DishesCategoryBean.class);
        itDishes = DishesBean.findAll(DishesBean.class);
        //判断手机是否有网络连接
        if(CommonUtils.checkNet(SplashActivity.this))
        {
            //请求软件序列号是否可用
            mHandler.post(runnableCheckLicence);
        }
    }

    Runnable runnableCheckLicence = new Runnable() {
        @Override
        public void run() {
            if(null==retrofit)
            {
                retrofit = RetrofitUtil.obtainRetrofit();
            }
            service = retrofit.create(LicenceService.class);
            Map<String, String> map = new HashMap<String, String>();
            map.put("table", "licence");
            map.put("k", Base64.encodeToString("male".getBytes(Charset.forName("utf-8")), Base64.NO_PADDING|Base64.NO_WRAP|Base64.URL_SAFE));
            call = service.obtainLicence(map);
            call.enqueue(new Callback<BusniessBean>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<BusniessBean> call, Response<BusniessBean> response) {
                    call.cancel();
                    if(null!=response.body())
                    {
                        BusniessBean busniess = response.body();
                        if(!"200".equals(busniess.retCode))
                        {
                            SnackbarUi.showMsgInfo(CommonUtils.getRootView(SplashActivity.this), "您的序列号已过期，请续费");
                        }
                        else
                        {
                            if(!itAccount.hasNext()||!itDishesCategory.hasNext()||!itDishes.hasNext())
                            {
                                snackbar = SnackbarUi.showAlertMsg(CommonUtils.getRootView(SplashActivity.this), resources.getString(R.string.init_data_tips));
                            }
                            //动态注册权限
                            if(23<=CommonUtils.getSDKVersion())
                            {
                                int camera = checkSelfPermission(Manifest.permission.CAMERA);
                                int fineLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                                int coarseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                                int phoneState = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                                int callPhone = checkSelfPermission(Manifest.permission.CALL_PHONE);
                                int readFile = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                                int wirteFile = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                int vibrator = checkSelfPermission(Manifest.permission.VIBRATE);
                                if(camera != PackageManager.PERMISSION_GRANTED ||
                                        fineLocation != PackageManager.PERMISSION_GRANTED ||
                                        coarseLocation != PackageManager.PERMISSION_GRANTED ||
                                        phoneState != PackageManager.PERMISSION_GRANTED ||
                                        callPhone != PackageManager.PERMISSION_GRANTED ||
                                        readFile != PackageManager.PERMISSION_GRANTED ||
                                        wirteFile != PackageManager.PERMISSION_GRANTED ||
                                        vibrator != PackageManager.PERMISSION_GRANTED )
                                {
                                    requestPermissions(CommonUtils.PERMISSION, 300);
                                }
                                else
                                {
                                    SnackbarUi.showAlertMsg(CommonUtils.getRootView(SplashActivity.this), resources.getString(R.string.permission_tips));
                                    //等待3秒后跳转到首页
                                    mHandler.postDelayed(runnableAnimation, 2000);
                                }
                            }
                            else
                            {
                                //等待3秒后跳转到首页
                                mHandler.postDelayed(runnableAnimation, 2000);
                            }
                        }
                    }
                    else
                    {
                        SnackbarUi.showMsgInfo(CommonUtils.getRootView(SplashActivity.this), "您的序列号已过期，请续费");
                    }
                }

                @Override
                public void onFailure(Call<BusniessBean> call, Throwable t) {
                    call.cancel();
                    SnackbarUi.showMsgInfo(CommonUtils.getRootView(SplashActivity.this), "您的序列号已过期，请续费");
                }
            });
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //等待3秒后跳转到首页
        mHandler.postDelayed(runnableAnimation, 2000);
    }

    Runnable runnableAnimation = new Runnable() {
        @Override
        public void run() {
            //判断用户信息是否初始化
            if(!itAccount.hasNext())
            {
                //初始化用户信息
                CommonUtils.initAccount();
            }
            if(!itDishesCategory.hasNext())
            {
                // 初始化菜品类别信息
                CommonUtils.initDishesCategory(resources);
            }

            if(!itDishes.hasNext())
            {
                // 初始化菜品类别信息
                CommonUtils.initDishes(resources);
            }

            mHandler.postDelayed(runnableSkip, 1000);
        }
    };

    Runnable runnableSkip = new Runnable() {
        @Override
        public void run() {
            if(null!=snackbar)
            {
                snackbar.dismiss();
            }
            //跳转到首页
            ActivityUtils.getInstance().skipActivity(SplashActivity.this, HomeActivity.class);
        }
    };
}
