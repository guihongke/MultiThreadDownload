package com.kgh.app;

import android.app.Application;
import android.content.Context;

import com.kgh.utils.SharedPreferencesUtil;

/**
 * Created by keguihong on 2017/11/3.
 */

public class BaseApp extends Application {

    public static Context application;
    @Override
    public void onCreate() {
        super.onCreate();
        application = getApplicationContext();
        SharedPreferencesUtil.initSharedPreferencesUtil(this);
    }

    public static Context getApplication(){
        return application;
    }
}
