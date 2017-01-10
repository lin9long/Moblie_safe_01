package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TabHost;

import com.example.lin.moblie_safe_01.R;

/**
 * Created by Administrator on 2017/1/9.
 */
public class BaseCacheActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_cache);
        //生成选项卡1、2
        TabHost.TabSpec tabSpec1 = getTabHost().newTabSpec("cache_clear").setIndicator("缓存清理");
        TabHost.TabSpec tabSpec2 = getTabHost().newTabSpec("sd_cache_clear").setIndicator("sd卡清理");
        //选项卡被点击后执行的事件
        tabSpec1.setContent(new Intent(this, CacheClearActivity.class));
        tabSpec2.setContent(new Intent(this, SDCacheClearActivity.class));
        //将选项卡添加到tabhost中来
        getTabHost().addTab(tabSpec1);
        getTabHost().addTab(tabSpec2);

    }

}
