package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.AntVirusDao;
import com.example.lin.moblie_safe_01.util.MD5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/1/6.
 */
public class AntiVirusActivity extends Activity {

    private ImageView iv_scanning;
    private TextView tv_name;
    private LinearLayout ll_add_text;
    private ProgressBar pb_process;
    private int index = 0;
    private static final int SCANING = 100;
    private static final int FINISH = 101;
    private List<ScanInfo> mScanVirusList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANING:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    String packageName = scanInfo.packageName;
                    tv_name.setText(scanInfo.name);
                    TextView tv_list_view = new TextView(getApplicationContext());
                    //如果是病毒，LinearLayout内使用红色字体显示
                    if (scanInfo.isVirus) {
                        tv_list_view.setTextColor(Color.RED);
                        tv_list_view.setText("发现病毒：" + packageName);
                    } else {
                        //如果是正常应用，LinearLayout内使用黑色字体显示
                        tv_list_view.setTextColor(Color.BLACK);
                        tv_list_view.setText("扫描安全：" + packageName);
                    }
                    ll_add_text.addView(tv_list_view, 0);
                    break;
                case FINISH:
                    iv_scanning.clearAnimation();
                    tv_name.setText("扫描结束");
                    //扫描结束后，卸载病毒列表内的应用
                    UninstallVirus();
                    break;
            }
        }
    };

    private void UninstallVirus() {
        for (ScanInfo scanInfo : mScanVirusList) {
            String packageName = scanInfo.packageName;
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anit_virus);
        initUi();
        initAnimation();
        checkVirus();
    }

    /**
     * 执行查找安装文件md5码，与数据库进行比对
     */
    private void checkVirus() {
        new Thread() {
            @Override
            public void run() {
                List<String> virusList = AntVirusDao.getVirusList();
                PackageManager pm = getPackageManager();
                mScanVirusList = new ArrayList<ScanInfo>();
                List<ScanInfo> scanList = new ArrayList<ScanInfo>();
                //获取安装应用的key及卸载残留文件的信息
                List<PackageInfo> packagesInfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
                //集合的总个数为processbar的总进度
                pb_process.setMax(packagesInfoList.size());
                for (PackageInfo packageInfo : packagesInfoList) {
                    //循环遍历获取每一个应用的key
                    ScanInfo scanInfo = new ScanInfo();
                    Signature[] signatures = packageInfo.signatures;
                    Signature signature = signatures[0];
                    String key = signature.toCharsString();
                    //讲获取到的key转换为32位md5格式
                    String md5Key = MD5Util.encoder(key);
                    //进行md5码对比，如果匹配上了，即为病毒文件
                    if (virusList.contains(md5Key)) {
                        scanInfo.isVirus = true;
                        //获取病毒应用的信息集合
                        mScanVirusList.add(scanInfo);
                    } else {
                        scanInfo.isVirus = false;
                    }
                    //获取包名及应用名称
                    scanInfo.packageName = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    index++;
                    pb_process.setProgress(index);
                    //获取扫描所有应用的信息集合
                    scanList.add(scanInfo);
                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //获取信息后，向主线程发送消息，更新Ui
                    Message msg = Message.obtain();
                    msg.what = SCANING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);
                }
                //所有程序遍历完成后，也发送消息，通知主线程扫描已完成
                Message msg = Message.obtain();
                msg.what = FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    class ScanInfo {
        public String name;
        public String packageName;
        public boolean isVirus;
    }

    /**
     * 设置旋转动画模式为无限次
     */
    private void initAnimation() {
        RotateAnimation rotateanimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateanimation.setDuration(1000);
        rotateanimation.setRepeatCount(Animation.INFINITE);
        iv_scanning.startAnimation(rotateanimation);
    }

    private void initUi() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_name = (TextView) findViewById(R.id.tv_name);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
        pb_process = (ProgressBar) findViewById(R.id.pb_process);
    }
}
