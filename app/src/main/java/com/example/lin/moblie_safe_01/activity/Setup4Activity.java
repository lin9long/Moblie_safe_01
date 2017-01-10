package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;
import com.example.lin.moblie_safe_01.util.ToastUtil;

/**
 * Created by Administrator on 2016/12/18.
 */
public class Setup4Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUi();
    }

    @Override
    public void showNextPage() {
        boolean open_security = SpUtil.getBoolean(getApplicationContext(), ContastValue.OPEN_SECURITY, false);
        if (open_security) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
            SpUtil.putBoolean(getApplicationContext(), ContastValue.SETUP_OVER, true);
        } else {
            ToastUtil.show(getApplicationContext(), "请打开手机防盗功能！！");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUi() {
        //判断是都已经打开安全防盗功能
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
        boolean open_security = SpUtil.getBoolean(getApplicationContext(), ContastValue.OPEN_SECURITY, false);
        checkbox.setChecked(open_security);
        if (open_security) {
            checkbox.setText("您已经开启防盗功能");
        } else {
            checkbox.setText("您还没有开启防盗功能");
        }
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                if (value) {
                    checkbox.setText("您已经开启防盗功能");
                    SpUtil.putBoolean(getApplicationContext(), ContastValue.OPEN_SECURITY, value);
                } else {
                    checkbox.setText("您还没有开启防盗功能");
                    SpUtil.putBoolean(getApplicationContext(), ContastValue.OPEN_SECURITY, value);
                }
            }
        });
    }

}
