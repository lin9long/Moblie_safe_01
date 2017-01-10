package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.SmsBackup;

import java.io.File;

/**
 * Created by Administrator on 2016/12/22.
 */
public class AToolsActivity extends Activity {
    private TextView tv_phone;
    private TextView tv_sms_backup;
    private TextView tv_comment_number;
    private ProgressDialog mProgressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        initPhoneAddress();
        initSmsBackup();
        initCommentNumberQuery();
        initAppLock();
    }
    //应用加锁功能
    private void initAppLock() {
        TextView tv_lock_app = (TextView) findViewById(R.id.tv_lock_app);
        tv_lock_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }

    //查询常用号码
    private void initCommentNumberQuery() {
        tv_comment_number = (TextView) findViewById(R.id.tv_comment_number);
        tv_comment_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
            }
        });
    }

    //设置短信备份功能
    private void initSmsBackup() {
        tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBaukupDialog();
            }
        });
    }

    //显示一个短信备份的对话框
    private void showSmsBaukupDialog() {
        mProgressdialog = new ProgressDialog(this);
        //设置进度条标题、图标及显示方式
        mProgressdialog.setTitle("短信备份进度");
        mProgressdialog.setIcon(R.mipmap.ic_launcher);
        mProgressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressdialog.show();
        new Thread() {
            @Override
            public void run() {
                //获取文件存储的绝对路径
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms74.xml";
                //备份短信
                SmsBackup.backup(getApplicationContext(), path, new SmsBackup.Callback() {
                    @Override
                    public void setMax(int max) {
                        mProgressdialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        mProgressdialog.setProgress(index);
                    }
                });
                mProgressdialog.dismiss();
            }
        }.start();
    }

    private void initPhoneAddress() {
        tv_phone = (TextView) findViewById(R.id.tv_query_phone_address);
        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), QueryAddressActivity.class);
                startActivity(i);
            }
        });
    }
}
