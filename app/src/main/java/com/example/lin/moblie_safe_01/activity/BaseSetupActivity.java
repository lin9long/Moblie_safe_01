package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * Activity父类，实现软件内activity的触摸滑动事件，上下页按键的跳转逻辑，
 * 因为跳转界面未知，定义两个抽象类，由子类实现跳转到哪一个界面。
 *
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //从右向左滑动，进入下一个页面
                if (e1.getX() - e2.getX() > 0) {
                    showNextPage();
                }
                //从右向左滑动，进入上一个页面
                if (e1.getX() - e2.getX() < 0) {
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //下一页的抽象方法，由子类决定跳转到哪一个界面
    public abstract void showNextPage();

    //上一页的抽象方法，由子类决定跳转到哪一个界面
    public abstract void showPrePage();

    public void nextPage(View view) {
        showNextPage();
    }

    public void prePage(View view) {
        showPrePage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //通过手势处理类，接受多种类型的事件，并用作处理
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
