package com.example.lin.moblie_safe_01.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ServiceInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/12/24.
 */

public class ServiceUtil {

    /**
     * @param ctx         上下文环境
     * @param serviceName 获取的服务名称
     * @return
     */
    public static boolean isRuning(Context ctx, String serviceName) {
        //获取activity的管理类
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //将所有运行的service放置在集合中
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(1000);
        //对集合进行遍历，获取其中的所有对象进行比对
        for (ActivityManager.RunningServiceInfo runningserviceinfo : runningServices) {
            if (serviceName.equals(runningserviceinfo.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
