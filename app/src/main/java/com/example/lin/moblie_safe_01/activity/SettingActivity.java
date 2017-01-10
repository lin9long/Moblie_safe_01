package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.service.AddressService;
import com.example.lin.moblie_safe_01.service.BlackNumberService;
import com.example.lin.moblie_safe_01.service.WatchDogService;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.ServiceUtil;
import com.example.lin.moblie_safe_01.util.SpUtil;
import com.example.lin.moblie_safe_01.view.SettingClickView;
import com.example.lin.moblie_safe_01.view.SettingItemView;

/**
 * Created by Administrator on 2016/12/16.
 */
public class SettingActivity extends Activity {

    private String[] mTosatStyle;
    private int mStyle;
    private SettingClickView scv_toast_style;
    private SettingClickView scv_toast_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdata();
        initAddress();
        initToastStyle();
        initToastLocation();
        initBlacknumber();
        initAppLock();
    }

    /**
     * 开启程序锁服务
     */
    private void initAppLock() {
        final SettingItemView sv_applock = (SettingItemView) findViewById(R.id.sv_applock);
        final boolean isRuning = ServiceUtil.isRuning(this, "com.example.lin.moblie_safe_01.service.WatchDogService");
        sv_applock.setCheck(isRuning);
        sv_applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischeck = sv_applock.isCheck();
                sv_applock.setCheck(!ischeck);
                if (!ischeck) {
                    startService(new Intent(getApplicationContext(), WatchDogService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), WatchDogService.class));
                }
            }
        });
    }


    /**
     * 初始化归属地土司对话框显示位置
     */
    private void initToastLocation() {
        scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
        scv_toast_location.setTitle("归属地提示框位置");
        scv_toast_location.setContent("设置归属地提示框位置");
        scv_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动一个aty，显示拖拽布局显示，为了达到半透明效果，
                // 需要在Manifest文件内添加主题效果，XML布局内添加背景信息
                startActivity(new Intent(SettingActivity.this, LocationActivity.class));
            }
        });
    }

    /**
     * 初始化黑名单过滤服务开启
     */
    private void initBlacknumber() {
        final SettingItemView sv_blacknumber = (SettingItemView) findViewById(R.id.sv_blacknumber);
        boolean isRunning = ServiceUtil.isRuning(getApplicationContext(), "com.example.lin.moblie_safe_01.service.BlackNumberService");
        sv_blacknumber.setCheck(isRunning);
        sv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischeck = sv_blacknumber.isCheck();
                sv_blacknumber.setCheck(!ischeck);
                if (!ischeck) {
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });
    }

    private void initAddress() {
        final SettingItemView sv_address = (SettingItemView) findViewById(R.id.sv_address);
        //使用工具类获取当前服务是否处于开启状态
        boolean isRunning = ServiceUtil.isRuning(getApplicationContext(), "com.example.lin.moblie_safe_01.service.AddressService");
        sv_address.setCheck(isRunning);
        //设置归属地的选择情况，进行开启、关闭服务端的操作
        sv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ischeck = sv_address.isCheck();
                sv_address.setCheck(!ischeck);
                if (!ischeck) {
                    startService(new Intent(getApplicationContext(), AddressService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }

    private void initToastStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
        scv_toast_style.setTitle("设置归属地显示风格");
        mTosatStyle = new String[]{"金色", "绿色", "黄色", "透明", "白色"};
        //将对应选择的数据放入sp中存储
        mStyle = SpUtil.getInt(getApplicationContext(), ContastValue.TOAST_STYLE, 0);
//        //因为sp中没存储数据，所以默认颜色为绿色
        scv_toast_style.setContent(mTosatStyle[mStyle]);
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });
    }

    /**
     * 显示选择归属地土司样式的对话框
     */
    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("请选择归属地显示样式");
        //设置一个单项选择控件
        builder.setSingleChoiceItems(mTosatStyle, mStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpUtil.putInt(getApplicationContext(), ContastValue.TOAST_STYLE, which);
                dialog.dismiss();
                scv_toast_style.setContent(mTosatStyle[which]);
            }
        });
        //设置一个取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initUpdata() {
        final SettingItemView sv_updata = (SettingItemView) findViewById(R.id.sv_updata);
        boolean open_updata = SpUtil.getBoolean(this, ContastValue.OPEN_UPDATA, false);
        sv_updata.setCheck(open_updata);
        //设置升级按钮的选择情况
        sv_updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ischeck = sv_updata.isCheck();
                sv_updata.setCheck(!ischeck);
                SpUtil.putBoolean(getApplicationContext(), ContastValue.OPEN_UPDATA, !ischeck);
            }
        });
    }
}
