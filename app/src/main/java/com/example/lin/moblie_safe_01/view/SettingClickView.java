package com.example.lin.moblie_safe_01.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;

/**
 * Created by Administrator on 2016/12/16.
 */

public class SettingClickView extends RelativeLayout {
    private TextView tv_title;
    private TextView tv_content;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_click_view, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
    }

    /**
     * @param title 设置控件标题
     */
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    /**
     * @param content 设置控件内容
     */
    public void setContent(String content) {
        tv_content.setText(content);
    }
}
