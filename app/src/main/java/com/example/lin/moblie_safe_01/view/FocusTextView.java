package com.example.lin.moblie_safe_01.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/15.
 */

public class FocusTextView extends TextView {
    //java程序编程实现的构造方法
    public FocusTextView(Context context) {
        super(context);
    }

    //系统调用构造方法+带属性+布局文件中定义属性的文件构造方法
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //系统调用构造方法+属性
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
