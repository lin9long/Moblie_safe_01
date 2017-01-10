package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;
import com.example.lin.moblie_safe_01.util.ToastUtil;

/**
 * Created by Administrator on 2016/12/18.
 */
public class Setup3Activity extends BaseSetupActivity {
    private EditText et_phone_number;
    private Button btn_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUi();
        initdata();
    }

    @Override
    public void showNextPage() {
        //从通讯录内获取的联系人
        String sp_contact_phone = SpUtil.getString(getApplicationContext(), ContastValue.CONTACT_PHONE, "");
        //通过输入框输入的联系人
        String et_contact_phone = et_phone_number.getText().toString();
        SpUtil.putString(getApplicationContext(), ContastValue.CONTACT_PHONE, et_contact_phone);
        if (!TextUtils.isEmpty(sp_contact_phone) || !TextUtils.isEmpty(et_contact_phone)) {
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);

            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(getApplicationContext(), "请输入安全号码！！");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initdata() {
        btn_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactsListActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //加入判断条件，确保contactlistactivity返回的数据不为空，程序返回setup3不报错
        if (data != null) {
            String phone = data.getExtras().getString("phone");
            //替换掉数据里面的空格及“-”字符
            phone = phone.replace("-", "").replace(" ", "").trim();
            et_phone_number.setText(phone);
            SpUtil.putString(getApplicationContext(), ContastValue.CONTACT_PHONE, phone);
        }

    }

    private void initUi() {
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        btn_select_number = (Button) findViewById(R.id.btn_select_number);
        //实现返回界面时候的数据回显
        String phone = SpUtil.getString(getApplicationContext(), ContastValue.CONTACT_PHONE, "");
        et_phone_number.setText(phone);
    }
}
