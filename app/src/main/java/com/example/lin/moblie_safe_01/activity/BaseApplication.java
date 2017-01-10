package com.example.lin.moblie_safe_01.activity;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2016/12/14.
 */

public class BaseApplication extends Application {
    private String tag = "Myapplication";

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
                Log.i(tag, "捕获到了一个异常，请检查程序");
                //将异常信息写入到error_mobile.log文件内，方便后续的bug处理
                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "error77.log";
                File file = new File(path);
                try {
                    //新建一个输出流，写入信息
                    PrintWriter printWriter = new PrintWriter(file);
                    ex.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //出现异常后退出程序，但不报错，改善用户体验
                System.exit(0);
            }
        });
    }

}
