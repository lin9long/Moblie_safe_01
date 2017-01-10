package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;
import com.example.lin.moblie_safe_01.util.ToastUtil;
import com.example.lin.moblie_safe_01.view.SettingItemView;

import java.util.AbstractCollection;

/**
 * Created by Administrator on 2016/12/18.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView sv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initUi();
    }

    @Override
    public void showNextPage() {
        String sim_number = SpUtil.getString(getApplicationContext(), ContastValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            ToastUtil.show(getApplicationContext(), "为了您的手机安全，请绑定SIM卡！！");
        } else {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUi() {
        sv_sim_bound = (SettingItemView) findViewById(R.id.sv_sim_bound);
        String sim_number = SpUtil.getString(getApplicationContext(), ContastValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            sv_sim_bound.setCheck(false);
        } else {
            sv_sim_bound.setCheck(true);
        }
        sv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ischeck = sv_sim_bound.isCheck();
                sv_sim_bound.setCheck(!ischeck);
                if (!ischeck) {
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String key = manager.getSimSerialNumber();
                    SpUtil.putString(getApplicationContext(), ContastValue.SIM_NUMBER, key);
                } else {
                    SpUtil.remove(getApplicationContext(), ContastValue.SIM_NUMBER);
                }
            }
        });
    }

}
