package com.example.lin.moblie_safe_01.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.CommonNumberDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/2.
 */
public class CommonNumberQueryActivity extends Activity {

    private ExpandableListView elv_comment_number;
    private List<CommonNumberDao.Group> mNumList;
    private CommonNumberDao mNumDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_number);
        initUi();
        initData();
    }

    private void initData() {
        mNumDao = new CommonNumberDao();
        mNumList = mNumDao.getGroup();
        final myAdapter adapter = new myAdapter();
        elv_comment_number.setAdapter(adapter);
        //  elv_comment_number.setAdapter(a);
        //设置listview内group成员的child点击事件
        elv_comment_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //此处也可以使用 adapter.getChild(groupPosition,childPosition).number的方法获取号码

                startCall(mNumList.get(groupPosition).childList.get(childPosition).number);
                return false;
            }
        });
    }

    private void startCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    private void initUi() {
        elv_comment_number = (ExpandableListView) findViewById(R.id.elv_comment_number);
        mNumList = new ArrayList<>();
    }

    class myAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mNumList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mNumList.get(groupPosition).childList.size();
        }

        @Override
        public CommonNumberDao.Group getGroup(int groupPosition) {
            return mNumList.get(groupPosition);
        }

        @Override
        public CommonNumberDao.Child getChild(int groupPosition, int childPosition) {
            return mNumList.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("         " + getGroup(groupPosition).name);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            textView.setTextColor(Color.RED);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_common_child_item, null);

            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);

            tv_name.setText(getChild(groupPosition, childPosition).name);
            tv_number.setText(getChild(groupPosition, childPosition).number);

            return view;
        }
        //返回ture，说明listview里面的child能设置对应的点击事件
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
