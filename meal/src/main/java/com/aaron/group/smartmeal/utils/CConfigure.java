package com.aaron.group.smartmeal.utils;

import android.content.Context;
import android.text.TextUtils;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.AccountBean;

/**
 * 说明: 采用SharedPreferences保存业务数据

 */

public class CConfigure {

    /**
     * 登录后，保存用户信息
     * @param context
     * @param account
     */
    public static void saveAccount(Context context, AccountBean account)
    {
        PreferenceHelper.writeInt(context, "accountInfo", "userId", account.userId);
        PreferenceHelper.writeString(context, "accountInfo", "userName", account.userName);
        PreferenceHelper.writeString(context, "accountInfo", "realName", account.realName);
        PreferenceHelper.writeString(context, "accountInfo", "userSex", account.userSex);
        PreferenceHelper.writeString(context, "accountInfo", "userLevel", account.userLevel);
        PreferenceHelper.writeString(context, "accountInfo", "userLogo", account.userLogo);
        PreferenceHelper.writeFloat(context, "accountInfo", "balance", account.balance);
        PreferenceHelper.writeString(context, "accountInfo", "moblie", account.moblie);
        PreferenceHelper.writeString(context, "accountInfo", "token", context.getResources().getString(R.string.token_str));
    }

    /**
     * 判断用户是否登录
     * @param context
     * @return
     */
    public static boolean isLogin(Context context)
    {
        String token = PreferenceHelper.readString(context, "accountInfo", "token");
        return !TextUtils.isEmpty(token);
    }

    /**
     * 退出
     * @param context
     * @return
     */
    public static void logout(Context context)
    {
        PreferenceHelper.clean(context, "accountInfo");
    }

    /**
     * 获取本地保存的用户信息
     * @param context
     * @return
     */
    public static AccountBean obtainAccount(Context context)
    {
        AccountBean account = new AccountBean();
        account.userId = PreferenceHelper.readInt(context, "accountInfo", "userId");
        account.userName = PreferenceHelper.readString(context, "accountInfo", "userName");
        account.realName = PreferenceHelper.readString(context, "accountInfo", "realName");
        account.userSex = PreferenceHelper.readString(context, "accountInfo", "userSex");
        account.userLevel = PreferenceHelper.readString(context, "accountInfo", "userLevel");
        account.userLogo = PreferenceHelper.readString(context, "accountInfo", "userLogo");
        account.balance = PreferenceHelper.readFloat(context, "accountInfo", "balance");
        account.moblie = PreferenceHelper.readString(context, "accountInfo", "moblie");
        return account;
    }
}
