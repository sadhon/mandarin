package com.cnpinyin.lastchinese.singleton;

import android.app.Application;
import android.content.Context;

/**
 * Created by inspiron on 8/17/2017.
 */

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();

    }

}
