package com.example.lin.moblie_safe_01.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.lin.moblie_safe_01.service.UpdateWidgetService;

/**
 * 需要在manifest上添加相应的receiver，添加xml窗体布局管理文件，同时设定layout布局文件
 */

public class ProcessWidget extends AppWidgetProvider {
    private String tag = "ProcessWidget";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag,"onReceive=====-------========");
     //   context.startService(new Intent(context,UpdateWidgetService.class));
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        //创建第一个窗体小布局的方法
        Log.i(tag,"onEnabled=============");
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onEnabled(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        //当窗体小布局宽高发生改变时调用的方法
        Log.i(tag,"onAppWidgetOptionsChanged====**************=========");
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //创建多一个窗体小布局的方法
        Log.i(tag,"onUpdate====**************=========");
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(tag,"onDeleted====**************=========");
        //当删除一个窗体小布局调用的方法
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(tag,"onDisabled====**************=========");
        //删除所有窗体小布局调用的方法
        context.stopService(new Intent(context,UpdateWidgetService.class));
        super.onDisabled(context);
    }
}
