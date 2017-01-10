package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/1/9.
 */
public class SDCacheClearActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(getApplicationContext());
        textView.setText("SDCacheClearActivity");
        textView.setTextColor(Color.BLACK);
        setContentView(textView);
    }
}
