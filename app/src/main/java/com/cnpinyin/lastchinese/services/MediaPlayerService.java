package com.cnpinyin.lastchinese.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by User on 11/6/2017.
 */

public class MediaPlayerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        return 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
