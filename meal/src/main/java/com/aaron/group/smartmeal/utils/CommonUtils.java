package com.aaron.group.smartmeal.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.aaron.group.smartmeal.bean.AccountBean;
import com.aaron.group.smartmeal.bean.DiningTableBean;
import com.aaron.group.smartmeal.bean.DiningTableShowBean;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.DishesCategoryBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 说明: 公共方法集合，封装了主要的工具类方法

 */

public class CommonUtils {

    /**
     * 判断网络是否连接
     */
    public static boolean checkNet(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null;// 网络是否连接
    }

    /**
     * 设置背景透明度
     * @param aty
     * @param alpha
     */
    public static void backgroundAlpha(Activity aty, float alpha)
    {
        WindowManager.LayoutParams lp = aty.getWindow ().getAttributes ();
        lp.alpha = alpha;
        aty.getWindow ().setAttributes ( lp );
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (0==dpValue)?0:(int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (0==pxValue)?0:(int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static int sp2px(Context context, float sp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (0==sp)?0:(int) (sp * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (0==px)?0:(int) Math.ceil(px / scale);
    }

    /**
     * 获取界面的根节点，配合SnackBar使用，snackbar是一个替代toast的组件，做消息提示用
     * @param activity
     * @return
     */
    public static View getRootView(Activity activity)
    {
        return ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 设置view的背景
     * @param view
     * @param drawable
     */
    @SuppressLint("NewApi")
    public static void loadBackground(View view, Drawable drawable)
    {
        //兼容低版本sdk
        if(getSDKVersion() >= 16)
        {
            view.setBackground(drawable);
        }
        else
        {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 获取手机系统SDK版本
     *
     * @return 如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 退出首页，关闭系统资源方法
     * @param context
     */
    public static void killAppDestory(Context context)
    {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.2之前操作
        if(8 < getSDKVersion())
        {
            am.restartPackage(context.getPackageName());
        }
        else if(8<=getSDKVersion())
        {
            am.killBackgroundProcesses(context.getPackageName());
        }
    }

    /**
     * 兼容android6以及以后的动态权限设置
     */
    public final static String[] PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 反射获取资源resId，根据图片名称获取到系统图片资源
     * @param paramContext
     * @param paramString
     * @return
     */
    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "mipmap", paramContext.getPackageName());
    }

    /**
     * 读取string.xml中的字符串
     * @param resources
     * @param stringIds
     * @return
     */
    public static String obtainString(Resources resources, int stringIds)
    {
        return resources.getString(stringIds);
    }

    /**
     * 初始化账户信息
     * 账户1：13588741789   123456
     * 账户2：18657118905    123456
     * 账户2：admin    admin
     */
    public static void initAccount()
    {
        AccountBean account = new AccountBean();
        account.userId = 1001;
        account.realName = "张光锋";
        account.userLevel = "系统管理员";
        account.userName = "18844549171";
        account.userPsw = EncryptUtil.getInstance().encryptMd532("123456");
        account.userSex = "男";
        account.userStatus = 0;
        account.userLogo = "";
        account.moblie = "18844549171";
        account.balance = 1000.0f;
        account.save();
    }

    /**
     * 读取配置文件，初始化菜品类别信息，数据源：
     * http://api.mob.com/#/apiwiki/cookmenu
     */
    public static void initDishesCategory(Resources resources)
    {
        JSONUtils<DishesCategoryBean> jsonUtils = new JSONUtils<DishesCategoryBean>();
        //获取菜品类别数据
        String dishesCategorys = obtainFileFromAssets("dishescategorys.json", resources);
        if(!TextUtils.isEmpty(dishesCategorys))
        {
            List<DishesCategoryBean> dishesCategoryList = jsonUtils.jsonToList(dishesCategorys, DishesCategoryBean.class);
            if(null!=dishesCategoryList&&!dishesCategoryList.isEmpty())
            {
                for(DishesCategoryBean dishesCategory:dishesCategoryList)
                {
                    if(null!=dishesCategory)
                    {
                        dishesCategory.save();
                    }
                }
            }
        }
    }

    /**
     * 读取配置文件，初始化菜品类别信息，数据源：
     * http://api.mob.com/#/apiwiki/cookmenu
     */
    public static void initDishes(Resources resources)
    {
        JSONUtils<DishesBean> jsonUtils = new JSONUtils<DishesBean>();
        //获取菜品类别数据
        String dishess = obtainFileFromAssets("dishes.json", resources);
        if(!TextUtils.isEmpty(dishess))
        {
            List<DishesBean> dishesList = jsonUtils.jsonToList(dishess, DishesBean.class);
            if(null!=dishesList&&!dishesList.isEmpty())
            {
                for(DishesBean dishes:dishesList)
                {
                    if(null!=dishes)
                    {
                        dishes.save();
                    }
                }
            }
        }
    }

    /**
     * 读取assets文件
     */
    public static String obtainFileFromAssets(String fileName,  Resources resources)
    {
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            is = resources.getAssets().open(fileName);
            reader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String str = null;
            while((str = bufferedReader.readLine())!=null){
                builder.append(str);
            }
            return builder.toString();
        } catch (Exception e) {
            return null;
        }
        finally {
            if(null!=bufferedReader)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null!=reader)
            {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null!=is)
            {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String obtainDay()
    {
        DateFormat format=new SimpleDateFormat("MM-dd");
        return format.format(new Date());
    }

    public static String obtainTime()
    {
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static String obtainWeek()
    {
        StringBuilder builder = new StringBuilder();
        Calendar c = Calendar.getInstance();
        switch (c.get(Calendar.DAY_OF_WEEK))
        {
            case 1:
            {
                builder.append("星期天");
            }
            break;
            case 2:
            {
                builder.append("星期一");
            }
            break;
            case 3:
            {
                builder.append("星期二");
            }
            break;
            case 4:
            {
                builder.append("星期三");
            }
            break;
            case 5:
            {
                builder.append("星期四");
            }
            break;
            case 6:
            {
                builder.append("星期五");
            }
            break;
            case 7:
            {
                builder.append("星期六");
            }
            break;
            default:
                break;
        }
        return builder.toString();
    }

    public static List<DiningTableBean> filterDiningTable(List<DiningTableBean> datas)
    {
        List<DiningTableBean> filters = new ArrayList<DiningTableBean>();
        for(DiningTableBean data:datas)
        {
            if(0==data.diningTableStatus)
            {
                filters.add(data);
            }
        }

        return filters;
    }

    public static List<DiningTableBean> fixedDiningTableStatus(List<DiningTableBean> datas, DiningTableBean data)
    {
        for(DiningTableBean table:datas)
        {
            if(data.diningTableId==table.diningTableId)
            {
                table.diningTableStatus = data.diningTableStatus;
            }
        }
        return datas;
    }

    public static DiningTableBean findCurrentTable(List<DiningTableBean> datas, int tableId)
    {
        DiningTableBean diningTable = new DiningTableBean();
        for(DiningTableBean table:datas)
        {
            if(tableId==table.diningTableId)
            {
                diningTable = table;
            }
        }
        return diningTable;
    }

    public static void changeTable(List<DiningTableShowBean> diningTableShows, DiningTableShowBean diningTableShow)
    {
        for(DiningTableShowBean tableShow:diningTableShows)
        {
            if(tableShow.diningTable.diningTableId==diningTableShow.diningTable.diningTableId)
            {
                tableShow.isChoice = true;
            }
            else
            {
                tableShow.isChoice = false;
            }
        }
    }
}
