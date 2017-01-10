package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ToastUtil;

/**
 * Created by Administrator on 2017/1/5.
 */
public class EnterPsdActivity extends Activity {

    private ImageView iv_app_icon;
    private TextView tv_app_name;
    private Button btn_confirm;
    private Button btn_cancel;
    private EditText et_psd;
    private String packagename;


    /**
     * 在弹出输入密码aty时，点击回退按键，直接跳转回桌面
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterpsd);
        //获取传递过来的包名
        packagename = getIntent().getStringExtra("packagename");
        initUi();
        initData();

    }

    private void initData() {
        PackageManager pm = getPackageManager();
        try {
            //获取对应包名的信息：程序名称及图表
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
            Drawable icon = applicationInfo.loadIcon(pm);
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
            iv_app_icon.setImageDrawable(icon);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_psd.getText().toString();
                if (!TextUtils.isEmpty(psd)) {
                    if (psd.equals("123")) {
                        //创建一个action
                        Intent intent = new Intent("android.intent.action.SKIP");
                        //讲当前应用作为包名传递进去
                        intent.putExtra("packagename", packagename);
                        //发送广播，由看门狗程序负责接收
                        sendBroadcast(intent);
                        //结束输入密码aty
                        finish();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误请重新输入");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });
    }

    private void initUi() {
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        et_psd = (EditText) findViewById(R.id.et_psd);
    }
}
