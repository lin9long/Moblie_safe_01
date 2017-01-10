package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.service.LockScreenService;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.ServiceUtil;
import com.example.lin.moblie_safe_01.util.SpUtil;

/**
 * Created by Administrator on 2017/1/2.
 */
public class ProcessSettingActivity extends Activity {

    private CheckBox cb_show_system;
    private CheckBox cb_lock_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_process_setting);
        showSystem();
        lockScreenClear();
    }
    //提供锁屏清理用户进程的功能
    private void lockScreenClear() {
        cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
        //使用ServiceUtil工具类对控件进行初始化操作
        boolean isrunning = ServiceUtil.isRuning(this, "com.example.lin.moblie_safe_01.service.LockScreenService");
        if (isrunning) {
            cb_lock_clear.setText("锁屏进程清理功能已开启");
        } else {
            cb_lock_clear.setText("锁屏进程清理功能已关闭");
        }
        cb_lock_clear.setChecked(isrunning);
        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_lock_clear.setText("锁屏进程清理功能已开启");
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                } else {
                    cb_lock_clear.setText("锁屏进程清理功能已关闭");
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }
        });
    }


    /**
     * 需要重新配置adapter内getcount的方法
     */
    private void showSystem() {
        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        //设置系统回显功能
        boolean ischeck = SpUtil.getBoolean(getApplicationContext(), ContastValue.SHOW_SYS_PROCESS, false);
        cb_show_system.setChecked(ischeck);
        if (ischeck) {
            cb_show_system.setText("显示系统进程");
        } else {
            cb_show_system.setText("隐藏系统进程");
        }
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_show_system.setText("显示系统进程");
                } else {
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(getApplicationContext(), ContastValue.SHOW_SYS_PROCESS, isChecked);
            }
        });
    }
}
