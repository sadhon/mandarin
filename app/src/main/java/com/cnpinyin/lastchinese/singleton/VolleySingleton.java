package com.cnpinyin.lastchinese.singleton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by inspiron on 8/17/2017.
 */

public class VolleySingleton {

    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    private static VolleySingleton getsInstance(){
        if(sInstance == null){
            sInstance = new VolleySingleton();
        }

        return sInstance;
    }

    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }
}
