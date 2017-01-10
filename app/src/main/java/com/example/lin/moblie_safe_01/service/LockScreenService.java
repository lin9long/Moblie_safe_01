package com.example.lin.moblie_safe_01.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.lin.moblie_safe_01.engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2017/1/2.
 */
public class LockScreenService extends Service {

    private IntentFilter intentFilter;
    private LockScreenService.innerReceiver innerReceiver;

    @Override
    public void onCreate() {
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerReceiver = new innerReceiver();
        registerReceiver(innerReceiver, intentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class innerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessInfoProvider.killAllProcess(context);
        }
    }
}
