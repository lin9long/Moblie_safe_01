package com.example.lin.moblie_safe_01.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.ProcessInfoProvider;
import com.example.lin.moblie_safe_01.receiver.ProcessWidget;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/1/3.
 */
public class UpdateWidgetService extends Service {

    private InnerReceiver innerReceiver;
    private String tag = "UpdateWidgetService";
    private Timer mTimer;

    @Override
    public void onCreate() {
        startTimer();
        IntentFilter intentFilter = new IntentFilter();
        //开锁action
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        //解锁action
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, intentFilter);
        super.onCreate();
    }

    class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startTimer();
            } else {
                cancelTimer();
            }
        }
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateWidget();
           //     Log.i(tag, "5秒更新程序正在运行中----------------");
            }
        }, 0, 5000);
    }

    private void updateWidget() {
        //获取AppWidgetManager的实例
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        //获取远程view控件，管理appwidget的控件布局
        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.process_widget);
        //分别给两个Textview设置对应的进程总数及可用内存数

        remoteView.setTextViewText(R.id.tv_process_count, "进程总数：" + ProcessInfoProvider.getProcessCount(this));
        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteView.setTextViewText(R.id.tv_process_memory, "可用内存：" + strAvailSpace);

        //1.更新功能，点击窗体小部件打开应用
        //配置homeaty的action及Category，点击窗体后打开homeaty
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        //配置延迟意图，出发点击事件后跳转界面
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        //2.点击小部件上的按钮，清理后台程序
        //设置intent的action，同时在清单文件内配置接收此action的receiver，生成广播接受者，执行清理后台程序
        Intent broadCastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        //PendingIntent的方法getBroadcast，将配置好的inten添加到里面
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.btn_clear, broadcast);
        //窗体小部件点击事件必须要放在updateAppWidget前

        //获取componentName实例，讲appwidget的类加载上
        ComponentName componentName = new ComponentName(this, ProcessWidget.class);
        //调用appwidgetmanager实例，更新小控件显示
        aWM.updateAppWidget(componentName, remoteView);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
        }
        cancelTimer();
        super.onDestroy();
    }
}
