package com.example.lin.moblie_safe_01.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.activity.ProcessManagerActivity;
import com.example.lin.moblie_safe_01.domain.Processinfo;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/1.
 */

public class ProcessInfoProvider {

    private static FileReader fileReader;
    private static BufferedReader bufferedReader;

    /**
     * @param ctx 上下文对象
     * @return 所有进程的总数
     */
    //获取进程总数
    public static int getProcessCount(Context ctx) {
        //获取ActivityManager对象，调用方法获取集合
        //ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //导入一个显示安卓所有进程的jar包，使用ProcessManager.getRunningAppProcessInfo(context)获取进程集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = ProcessManager.getRunningAppProcessInfo(ctx);
        return runningAppProcessInfo.size();
    }

    /**
     * @param ctx 上下文对象
     * @return 返回可用内存大小
     */
    public static long getAvailSpace(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //构建可用存储内存对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //给可用内存getMemoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * @return 返回总的内存大小
     */
    public static long getTotalSpace() {
        try {
            //使用fileReader找到对应的文件
            fileReader = new FileReader("proc/meminfo");
            //使用bufferedReader读取文件
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            //将字符串转换为字符的数组
            char[] chars = line.toCharArray();
            StringBuffer stringBuffer = new StringBuffer();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (fileReader != null && bufferedReader != null) {
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * @param ctx 上下文环境
     * @return 进程信息集合
     */
    public static List<Processinfo> getProcessInfo(Context ctx) {
        //新建一个list用来存放数据集合
        List<Processinfo> list = new ArrayList<>();
        //获取am，用以获取运行中app进程信息，包括包名，占用内存大小
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = ProcessManager.getRunningAppProcessInfo(ctx);

        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfo) {
            Processinfo processinfo = new Processinfo();
            processinfo.packagename = info.processName;
            //获取运行中进程的pid号
            int pid = info.pid;
            //通过am获取对于pid进程的占用内存大小集合，因为pid为数组，所以读取出来的内存大小也为数组
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{pid});
            //获取数组中的第一位数据，因为只传递了一个pid
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //使用getTotalPrivateDirty的API，获取相应的内存大小，统一byte单位乘以1024
            processinfo.memsize = memoryInfo.getTotalPrivateDirty() * 1024;
            try {
                //使用pm获取ApplicationInfo，穿入参数为包名
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processinfo.packagename, 0);
                //获取应用的名称
                processinfo.name = applicationInfo.loadLabel(pm).toString();
                //获取应用的图标
                processinfo.icon = applicationInfo.loadIcon(pm);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processinfo.isSystem = true;
                } else {
                    processinfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //如果不能找到相应的包名，则默认为系统应用，使用包名作为应用名，获取系统默认图标
                processinfo.name = info.processName;
                processinfo.icon = ctx.getResources().getDrawable(R.mipmap.ic_launcher);
                processinfo.isSystem = true;
                e.printStackTrace();
            }
            list.add(processinfo);
        }
        return list;
    }

    public static void killProcess(Context ctx, Processinfo processinfo) {
        //需要添加权限，杀死后台进程权限
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(processinfo.packagename);
    }

    public static void killAllProcess(Context ctx) {
        //获取ActivityManager，拿到其结束进程的方法
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = ProcessManager.getRunningAppProcessInfo(ctx);
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfo) {
            if (info.processName.equals(ctx.getPackageName())) {
                continue;
            }
            //判断如果为用户应用进程时，结束进程
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(info.processName, 0);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    //如果当前进程为系统进程，则跳出循环
                   continue;
                } else {
                    am.killBackgroundProcesses(info.processName);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
           //删除所有进程会导致系统无法开机，另外编写上述步骤，只删除用户应用
           // am.killBackgroundProcesses(info.processName);
        }
    }
}
