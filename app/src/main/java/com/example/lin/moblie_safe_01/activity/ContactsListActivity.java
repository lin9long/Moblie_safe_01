package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/12/18.
 */
public class ContactsListActivity extends Activity {
    private ListView lv_contact;
    private String tag = "ContactsListActivity";
    private Myadapter myadapter;
    private List<HashMap<String, String>> contactList = new ArrayList<>();
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //根据list内容配置适配器
            myadapter = new Myadapter();
            lv_contact.setAdapter(myadapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initui();
        initdata();

    }

    private void initdata() {
        //读取联系人为耗时操作，需要将其放置在线程内执行
        new Thread() {

            @Override
            public void run() {
                //获取内容解析者
                ContentResolver contentresolver = getContentResolver();
                //查询联系人数据表
                Cursor cursor = contentresolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"}
                        , null, null, null, null);
                contactList.clear();
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    // Log.i(tag, "id=" + id);
                    Cursor indexcursor = contentresolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"}, "raw_contact_id = ?",
                            new String[]{id}, null);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while (indexcursor.moveToNext()) {
                        String data = indexcursor.getString(0);
                        String type = indexcursor.getString(1);
                        //  Log.i(type,data);
                        //判断条件，将姓名及电话号码数据添加到Hashmap当中
                        if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("phone", data);
                                Log.i(type, data);
                            }
                        } else if (type.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                                Log.i(type, data);
                            }
                        }
                    }
                    indexcursor.close();
                    contactList.add(hashMap);
                }
                //发送一个空的消息，通知主线程数据集合已准备完毕，可以进行主UI的更新操作
                cursor.close();
                mHandle.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initui() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        //响应点击时间
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (myadapter != null) {
                    //从myadapter中获得一个hashmap的对象
                    HashMap<String, String> hashMap = myadapter.getItem(i);
                    String phone = hashMap.get("phone");
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(1, intent);
                    finish();
                }
            }
        });
    }

    private class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int i) {
            return contactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_name = (TextView) view1.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view1.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(i).get("name"));
            tv_phone.setText(getItem(i).get("phone"));
            return view1;
        }
    }
}
