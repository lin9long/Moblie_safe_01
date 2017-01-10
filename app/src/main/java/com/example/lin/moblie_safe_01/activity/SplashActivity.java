package com.example.lin.moblie_safe_01.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;
import com.example.lin.moblie_safe_01.util.SteamUtil;
import com.example.lin.moblie_safe_01.util.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.factory.BitmapFactory;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private int mLocalVersionCode;
    private static final int UPDATA_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;
    private String mDes;
    private String mDownload;
    private RelativeLayout rel_root;
    private String tag = "SplashActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATA_VERSION:
                    showUpdataDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(SplashActivity.this, "URL错误");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(SplashActivity.this, "IS错误");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(SplashActivity.this, "JSON错误");
                    enterHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initui();
        initdata();
        initDB();
        //进入欢迎界面时，生成桌面快捷方式
        boolean short_cut = SpUtil.getBoolean(this, ContastValue.SHORT_CUT, false);
        if (!short_cut) {
            initShortcut();
        }
        AdManager.getInstance(this).init("02173c01b886735e", "98e74d09e699db34",true,true);

    }

    private void initShortcut() {
        //设置意图，维护其生成快捷方式的action
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //为快捷方式定义名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机安全卫士");
        //为快捷方式定义图标,使用BitmapFactory.decodeResource方法，获取drawable内的图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.home_app2));
        //创建一个意图，指向点击快捷方式的跳转对象
        Intent shortcutintent = new Intent("android.intent.action.HOME");
        shortcutintent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutintent);
        //发送广播，广播接收者已在launcher系统应用中定义
        sendBroadcast(intent);
        SpUtil.putBoolean(this, ContastValue.SHORT_CUT, true);
    }

    //初始化数据库
    private void initDB() {
        initAddressDB("phoneaddress.db");
        //初始化拷贝数据库内容
        initAddressDB("commonnum.db");
        initAddressDB("antivirus.db");
    }

    //获取归属地数据库
    private void initAddressDB(String dbname) {
        File files = getFilesDir();
        File file = new File(files, dbname);
        //判断归属地数据库文件是否存在，如果存在不在生产
        if (file.exists()) {
            return;
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = getAssets().open(dbname);
            fos = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = is.read(bs)) != -1) {
                fos.write(bs, 0, temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null && fos != null) {
                try {
                    is.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initdata() {
        TextView textView = (TextView) findViewById(R.id.tv_version_name);
        //获取软件版本
        textView.setText("版本名称：" + getVersionName());
        //获取本地软件版本号与服务器端做比对
        mLocalVersionCode = getVersionCode();
        System.out.println("系统版本号；" + mLocalVersionCode);
        if (SpUtil.getBoolean(this, ContastValue.OPEN_UPDATA, false)) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }

    }

    private int getVersionCode() {
        //获取版本码
        PackageManager pm = getPackageManager();

        try {
            PackageInfo packageinfo = pm.getPackageInfo(getPackageName(), 0);
            return packageinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;

    }

    //检查服务器端更新信息
    public void checkVersion() {
        //开启线程读取网络数据，耗时操作不能再主ui进行
        new Thread() {
            Message msg = Message.obtain();
            long starttime = System.currentTimeMillis();

            @Override

            public void run() {
                try {
                    //获取服务器地址内对应的升级文件
                    URL url = new URL("http://10.0.2.2:8080/updata64.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(2000);
                    connection.setConnectTimeout(2000);
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String json = SteamUtil.steamtoString(is);
                        Log.i("tag", json);
                        JSONObject jsonObject = new JSONObject(json);
                        String versionname = jsonObject.getString("versionname");
                        String versioncode = jsonObject.getString("versioncode");
                        mDes = jsonObject.getString("des");
                        mDownload = jsonObject.getString("download");
                        Log.i("tag", versioncode);
                        Log.i("tag", versionname);
                        Log.i("tag", mDes);
                        Log.i("tag", mDownload);
                        if (mLocalVersionCode < Integer.parseInt(versioncode)) {
                            msg.what = UPDATA_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    long endtime = System.currentTimeMillis();
                    if ((endtime - starttime) < 4000) {
                        try {
                            Thread.sleep(4000 - (endtime - starttime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);

                }
            }
        }.start();
    }

    private void showUpdataDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("更新提示");
        builder.setMessage(mDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadapk();
            }


        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterHome();

            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    private void downloadapk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "heima67.apk";
            org.xutils.http.RequestParams params = new org.xutils.http.RequestParams(mDownload);
            params.setSaveFilePath(path);
            params.setAutoRename(true);
            x.http().post(params, new Callback.ProgressCallback<File>() {

                @Override
                public void onSuccess(File result) {
                    Log.i(tag, "下载成功！");
                    installApk(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(tag, "下载失败！");
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i(tag, "下载取消！");
                }

                @Override
                public void onFinished() {
                    Log.i(tag, "下载结束！");
                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {
                    Log.i(tag, "下载刚刚开始！");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    Log.i(tag, "下载中.....");
                    Log.i(tag, "total:" + total);
                    Log.i(tag, "current:" + current);
                    Log.i(tag, "isUploading:" + isDownloading);
                }
            });
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    //进入软件主界面
    private void enterHome() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //初始化ui界面
    private void initui() {
        rel_root = (RelativeLayout) findViewById(R.id.rel_root);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rel_root.setAnimation(alphaAnimation);

    }
}
