package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.AppLockDao;
import com.example.lin.moblie_safe_01.engine.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/12/17.
 */
public class TestActivity extends Activity{
    private Button bt_insert;
    private boolean iswatch;
    private AppLockDao mDao;
    private List<String> mLockAppList;
    private String packname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mDao = AppLockDao.getInstance(getApplicationContext());
        iswatch = true;
        bt_insert = (Button) findViewById(R.id.bt_insert);
        bt_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watch();
            }

        });
    }

    private void watch() {

        // List<Appinfo> appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
        mLockAppList = mDao.findAll();
        while (iswatch) {
            //5.0后安全机制改动，不能使用ActivityManager获取应用包名
//                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                    //获取第一个运行任务栈内的运行程序列表
//                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
//                    //取第一个任务栈内运行栈信息
//                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
//                    //获取该运行任务栈第一个aty的包名
//                    String packname = runningTaskInfo.topActivity.getPackageName();
//                    //如果改包名存在与锁定应用数据库内，跳转到输入密码aty

            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);

            long time = System.currentTimeMillis();
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> queryUsageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 20000, time);
            if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                return;
            }
            UsageStats recentStats = null;
            for (UsageStats usageStats : queryUsageStats) {
                if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    recentStats =  usageStats;
                    packname = recentStats.getPackageName();
                    System.out.println("---------------"+packname);
                }
            }


            if (mLockAppList.contains(packname)) {
                Intent intent1 = new Intent(getApplicationContext(), EnterPsdActivity.class);
                //在服务内开启aty，需要给aty开启一个新的任务栈
                //将包名作为信息传递到下一个EnterPsdActivity
                intent1.putExtra("packagename", packname);
                startActivity(intent1);
            }

        }
    }
}
