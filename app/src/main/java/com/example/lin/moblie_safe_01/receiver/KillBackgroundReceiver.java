package com.example.lin.moblie_safe_01.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lin.moblie_safe_01.engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2017/1/4.
 */

public class KillBackgroundReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessInfoProvider.killAllProcess(context);
    }
}
