package com.example.lin.moblie_safe_01.service;

import android.app.ActivityManager;
import android.app.Service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import android.support.annotation.Nullable;

import com.example.lin.moblie_safe_01.activity.EnterPsdActivity;
import com.example.lin.moblie_safe_01.domain.Appinfo;
import com.example.lin.moblie_safe_01.engine.AppInfoProvider;
import com.example.lin.moblie_safe_01.engine.AppLockDao;

import java.util.List;
import java.util.zip.Inflater;

/**
 * 看门狗程序，不断读取第一个任务栈内打开Activity的包名，当包名与锁定应用数据库列表一致时，跳转到输入密码界面
 */
public class WatchDogService extends Service {

    private boolean iswatch;
    private AppLockDao mDao;
    private List<String> mLockAppList;
    private String packname;
    private InnerReceiver mInnerReceiver;
    private String mSkipPackgaeName;
    private MyContentObserver mMyContentObserver;

    @Override
    public void onCreate() {
        mDao = AppLockDao.getInstance(getApplicationContext());
        iswatch = true;
        watch();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);
        //使用内容观察者，当程序锁数据库发生变化时，需要重新获取程序锁数据列表
        mMyContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mMyContentObserver);
        super.onCreate();
    }

    class MyContentObserver extends ContentObserver {


        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * @param selfChange 当数据库发生增加或删除时，重新调用一次查找mLockAppList列表方法
         */
        @Override
        public void onChange(boolean selfChange) {
            new Thread() {
                @Override
                public void run() {
                    mLockAppList = mDao.findAll();
                }
            }.start();


            super.onChange(selfChange);
        }
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackgaeName = intent.getStringExtra("packagename");
        }
    }

    private void watch() {
        new Thread() {
            @Override
            public void run() {

                List<Appinfo> appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mLockAppList = mDao.findAll();
                while (iswatch) {
//                    //5.0后安全机制改动，不能使用ActivityManager获取应用包名
//                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                    //获取第一个运行任务栈内的运行程序列表
//                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
//                    //取第一个任务栈内运行栈信息
//                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
//                    //获取该运行任务栈第一个aty的包名
//                    packname = runningTaskInfo.topActivity.getPackageName();
//                    //如果改包名存在与锁定应用数据库内，跳转到输入密码aty

                    //使用UsageStatsManager获取运行的应用
//            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//            long time = System.currentTimeMillis();
//            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
//            List<UsageStats> queryUsageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 20000, time);
//            if (queryUsageStats == null || queryUsageStats.isEmpty()) {
//                return;
//            }
//            UsageStats recentStats = null;
//            for (UsageStats usageStats : queryUsageStats) {
//                if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
//                    recentStats =  usageStats;
//                    packname = recentStats.getPackageName();
//                    System.out.println("---------------"+packname);
//                }
//            }
                    packname = getAppPackageName() + "";
                    System.out.println("当前应用包名为" + packname);
                    if (mLockAppList.contains(packname)) {
                        //对输入密码界面的事件做监听，如果循环到当前应用包名，则不打开输入密码界面，直接进入应用
                        if (!packname.equals(mSkipPackgaeName)) {
                            Intent intent = new Intent(getApplicationContext(), EnterPsdActivity.class);
                            //在服务内开启aty，需要给aty开启一个新的任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //将包名作为信息传递到下一个EnterPsdActivity
                            intent.putExtra("packagename", packname);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    //使用UsageStatsManager获取运行的应用
    private String getAppPackageName() {
        //第一次使用时需要打开权限
//        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        UsageStatsManager usm = (UsageStatsManager) getSystemService(this.USAGE_STATS_SERVICE);
        long ts = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 60000, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                recentStats = usageStats;
            }
        }
        return recentStats.getPackageName();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mInnerReceiver != null) {
            unregisterReceiver(mInnerReceiver);
        }
        iswatch = false;
        if (mMyContentObserver != null) {
            getContentResolver().unregisterContentObserver(mMyContentObserver);
        }
        super.onDestroy();
    }
}
