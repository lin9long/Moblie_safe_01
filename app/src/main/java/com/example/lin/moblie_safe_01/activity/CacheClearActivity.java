package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/1/7.
 */
public class CacheClearActivity extends Activity implements View.OnClickListener {

    private Button btn_clear;
    private ProgressBar pb_bar;
    private TextView tv_name;
    private LinearLayout ll_add_text;
    private PackageManager mPm;
    private ImageView iv_app_icon;
    private TextView tv_app_name;
    private TextView tv_cache_info;
    private static final int UPDATA_CACHE_APP = 100;
    private ImageView iv_delete;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //更新包名、图标、缓存大小到LinearLayout的view上
                case UPDATA_CACHE_APP:
                    View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);
                    iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                    iv_delete = (ImageView)view.findViewById(R.id.iv_delete);
                    tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                    tv_cache_info = (TextView) view.findViewById(R.id.tv_cache_info);
                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_app_icon.setBackgroundDrawable(cacheInfo.icon);
                    tv_app_name.setText(cacheInfo.name);
                    tv_cache_info.setText(cacheInfo.cacheSize);
                    ll_add_text.addView(view, 0);

                    //打开logcat，观察打开系统设置界面时，打开缓存清理应用界面时启动aty，参照代码打开此aty
                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:"+cacheInfo.packageName));
                            startActivity(intent);
                        }
                    });

                    break;
                //显示每一个被扫描的App名字
                case CLEAR_CACHE_APP:
                    String name = (String) msg.obj;
                    tv_name.setText(name);
                    break;
                //显示扫描已完成的结果
                case FINIFSH:
                    tv_name.setText("扫描完成");
                    break;
                case CLEAR_CACHE:
                    ll_add_text.removeAllViews();
                    break;
            }
        }
    };
    private int index = 0;
    private static final int CLEAR_CACHE_APP = 101;
    private static final int FINIFSH = 102;
    private static final int CLEAR_CACHE = 103;
    private static final int REQUEST = 104;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initUi();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mPm = getPackageManager();
                List<PackageInfo> packageInfo = mPm.getInstalledPackages(0);
                pb_bar.setMax(packageInfo.size());
                for (PackageInfo info : packageInfo) {
                    String packageName = info.packageName;
                    getCache(packageName);
                    index++;
                    pb_bar.setProgress(index);
                    try {
                        //随机睡眠时间，用户体验更好
                        Thread.sleep(100 + new Random().nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //将清理过程中的软件名称，通过消息发送出去，更新主UI，并显示在控件上
                    Message msg = Message.obtain();
                    msg.what = CLEAR_CACHE_APP;
                    try {
                        String name = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
                        msg.obj = name;
                        mHandler.sendMessage(msg);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                //搜索完成后，通知主UI更新空间，显示扫描已完成
                Message msg = Message.obtain();
                msg.what = FINIFSH;
                mHandler.sendMessage(msg);


            }
        }.start();
    }

    private void initUi() {
        btn_clear = (Button) findViewById(R.id.btn_clear);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
        //使用反射调用方法，清理缓存
        btn_clear.setOnClickListener(this);
    }


    /**
     * 获取应用的缓存信息，同时将软件名称，图标，包名一起打包到message，发送到主线程用作主ui的更新
     *
     * @param packageName 传入包名
     */
    public void getCache(final String packageName) {
        IPackageStatsObserver.Stub observer = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                CacheInfo cacheInfo = new CacheInfo();
                long cacheSize = pStats.cacheSize;
                if (cacheSize > 0) {
                    Message msg = Message.obtain();
                    msg.what = UPDATA_CACHE_APP;
                    cacheInfo.packageName = pStats.packageName;
                    try {
                        cacheInfo.name = mPm.getApplicationInfo(pStats.packageName, 0).loadLabel(mPm).toString();
                        cacheInfo.icon = mPm.getApplicationInfo(pStats.packageName, 0).loadIcon(mPm);
                        String size = Formatter.formatFileSize(getApplicationContext(), cacheSize);
                        cacheInfo.cacheSize = size;
                        msg.obj = cacheInfo;
                        mHandler.sendMessage(msg);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        //   mPm.getPackageSizeInfo(mCurComputingSizePkg, mStatsObserver);
        try {
            //找到方法所在字节码文件
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //找到对应方法所在的包名
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //获取调用对象的方法，mPm内调用方法，穿入参数为包名及对象
            method.invoke(mPm, packageName, observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:

//                if (Build.VERSION.SDK_INT >= 23) {
//                    int checkCacheCleraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CLEAR_APP_CACHE);
//                    if (checkCacheCleraPermission != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(CacheClearActivity.this, new String[]{Manifest.permission.CLEAR_APP_CACHE}, REQUEST);
//                        return;
//                    } else {
                        try {

                            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                            //找到对应方法所在的包名
                            final Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                            //获取调用对象的方法，mPm内调用方法，穿入参数为包名及对象
                            method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                                @Override
                                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                                    Message msg = Message.obtain();
                                    msg.what = CLEAR_CACHE;
                                    mHandler.sendMessage(msg);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
//                }
        }
  //  }

    class CacheInfo {
        public String name;
        public String packageName;
        public String cacheSize;
        public Drawable icon;
    }
}
