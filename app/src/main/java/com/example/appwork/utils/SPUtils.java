package com.example.appwork.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.appwork.model.UserInfo;
import com.google.gson.Gson;

public class SPUtils {
    private static final String SP_NAME = "app_config";
    private static final String KEY_LOGIN_STATUS = "login_status";
    private static final String KEY_USER_INFO = "user_info";

    private static final String TAG = "SPUtils";





    public static UserInfo getUserInfo(Context context) {
        String userJson = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USER_INFO, null);
        if (TextUtils.isEmpty(userJson)) {
            return null;
        }
        return new Gson().fromJson(userJson, UserInfo.class);
    }

    public static void clearUserInfo(Context context) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_LOGIN_STATUS)
                .remove(KEY_USER_INFO)
                .apply();
    }

    public static void saveLoginStatus(Context context, boolean isLogin) {
        Log.d(TAG, "Saving login status: " + isLogin);
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_LOGIN_STATUS, isLogin)
                .apply();
    }

    public static boolean isLogin(Context context) {
        boolean isLogin = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_LOGIN_STATUS, false);
        Log.d(TAG, "Checking login status: " + isLogin);
        return isLogin;
    }

    public static void saveUserInfo(Context context, UserInfo userInfo) {
        Log.d(TAG, "Saving user info: " + (userInfo != null ? userInfo.getUsername() : "null"));
        String userJson = new Gson().toJson(userInfo);
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_USER_INFO, userJson)
                .apply();
    }
}