package com.example.lin.moblie_safe_01.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;

/**
 * Created by Administrator on 2016/12/16.
 */

public class SettingItemView extends RelativeLayout {
    private TextView tv_title;
    private TextView tv_content;
    private CheckBox tv_cb;
    private String tag = "SettingItemView";
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.lin.moblie_safe_01";
    private String mTitle;
    private String mDesoff;
    private String mDeson;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_view, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_cb = (CheckBox) findViewById(R.id.tv_cb);
        initAttrs(attrs);
        tv_title.setText(mTitle);
    }

    private void initAttrs(AttributeSet attrs) {
//        Log.i(tag, "自定义控件数量：" + attrs.getAttributeCount());
//        for (int i =0;i<attrs.getAttributeCount();i++){
//            Log.i(tag,"自定义控件名称："+attrs.getAttributeName(i));
//            Log.i(tag,"自定义控件值："+attrs.getAttributeValue(i));
//            Log.i(tag,"=====================");
//        }
        mTitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
    }

    public boolean isCheck() {
        return tv_cb.isChecked();
    }

    public void setCheck(boolean isCheck) {
        tv_cb.setChecked(isCheck);
        if (!isCheck) {
            tv_content.setText(mDesoff);
        } else {
            tv_content.setText(mDeson);
        }
    }
}
