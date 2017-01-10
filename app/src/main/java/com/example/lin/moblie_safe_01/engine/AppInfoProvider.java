package com.example.lin.moblie_safe_01.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.lin.moblie_safe_01.domain.Appinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */

public class AppInfoProvider {

    private static Appinfo appinfo;

    /**
     * 返回当前应用所有app的名称、包名、图标、（系统或用户应用）、（系统内存或sd卡）
     *
     * @param ctx 传递上下文对象
     * @return 返回一个List<Appinfo> 集合，包含app信息
     */
    public static List<Appinfo> getAppInfoList(Context ctx) {
        List<Appinfo> appinfolist = new ArrayList<>();
        //获取包的管理对象
        PackageManager pm = ctx.getPackageManager();
        //获取安装在手机上所有应用信息的集合
        List<PackageInfo> Packagesinfolist = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : Packagesinfolist) {
            appinfo = new Appinfo();
            //获取集合的包名
            appinfo.packageName = packageInfo.packageName;
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取集合的软件名称,uid为对应流量信息放置的文件夹（proc/uid_stat/目录下）
            appinfo.appName = applicationInfo.loadLabel(pm).toString() + " "+applicationInfo.uid;
            //获取集合的图标
            appinfo.icon = applicationInfo.loadIcon(pm);
            //判断是否为系统应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                //为系统应用
                appinfo.isSystem = true;
            } else {
                //为非系统应用
                appinfo.isSystem = false;
            }
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                //为sd卡应用
                appinfo.isSdcard = true;
            } else {
                //为非sd卡应用
                appinfo.isSdcard = false;
            }
            appinfolist.add(appinfo);
        }
        return appinfolist;
    }
}
