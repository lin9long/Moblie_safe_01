package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;

/**
 * Created by Administrator on 2016/12/17.
 */
public class SetupOverActivity extends Activity {
    private TextView tv_phone;
    private TextView tv_reset_setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setup_over = SpUtil.getBoolean(this, ContastValue.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activity_setup_over);
            initUi();
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initUi() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        String phone = SpUtil.getString(getApplicationContext(), ContastValue.CONTACT_PHONE, "");
        tv_phone.setText(phone);
        tv_reset_setup = (TextView) findViewById(R.id.tv_reser_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetupOverActivity.this, Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
