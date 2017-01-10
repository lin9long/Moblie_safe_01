package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.AddressDao;

/**
 * Created by Administrator on 2016/12/22.
 */
public class QueryAddressActivity extends Activity {
    private Button bt_query;
    private EditText et_phone;
    private TextView tv_show_address;
    public String phone;
    public String mData;
    public Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println(mData);
            tv_show_address.setText(mData);
        }
    };
    private String tag = "QueryAddressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queryaddress);
        initUi();
    }


    private void initUi() {
        bt_query = (Button) findViewById(R.id.bt_query);
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_show_address = (TextView) findViewById(R.id.tv_show_address);
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //点击查询按钮时，如文本数据不为空，进行查询
                phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    System.out.println(phone);
                    queryData(phone);
                    //如果文本数据包为空，执行输入框抖动动画
                } else {
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    et_phone.startAnimation(shake);
                    //设置手机震动效果
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(new long[]{2000,1000,2000,1000},-1);
                }
            }
        });
        //实时监听输入框的输入文本，实时进行查询
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            //在输入内容发生变化后执行查询操作
            @Override
            public void afterTextChanged(Editable editable) {
                String phone = et_phone.getText().toString();
                queryData(phone);
            }
        });
    }

    //新建一个方法，传入要查询的号码
    protected void queryData(final String phonenum) {
        new Thread() {
            @Override
            public void run() {
                mData = AddressDao.getAddress(phonenum);
                mHander.sendEmptyMessage(0);
            }
        }.start();
    }
}
