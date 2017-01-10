package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;

/**
 * Created by Administrator on 2016/12/26.
 */
public class LocationActivity extends Activity {

    private Button btn_top;
    private Button btn_bottom;
    private ImageView iv_drag;
    private int startX;
    private int startY;
    private WindowManager mWM;
    private long[] mHits = new long[2];
    private int mScreen_height;
    private int mScreen_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        initUi();
    }

    private void initUi() {
        btn_top = (Button) findViewById(R.id.btn_top);
        btn_bottom = (Button) findViewById(R.id.btn_bottom);
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        //获取屏幕的实际宽度及高度
        mScreen_height = mWM.getDefaultDisplay().getHeight();
        mScreen_width = mWM.getDefaultDisplay().getWidth();

        //初始化设置界面的土司位置显示
        int locationX = SpUtil.getInt(getApplicationContext(), ContastValue.LOCATION_X, 0);
        int locationY = SpUtil.getInt(getApplicationContext(), ContastValue.LOCATION_Y, 0);
        if (locationX > mScreen_height / 2) {
            btn_top.setVisibility(View.VISIBLE);
            btn_bottom.setVisibility(View.INVISIBLE);
        } else {
            btn_top.setVisibility(View.INVISIBLE);
            btn_bottom.setVisibility(View.VISIBLE);
        }
        //因为空间在RelativeLayout内，所以相对位置由RelativeLayout提供
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;

        iv_drag.setLayoutParams(layoutParams);
        //设置空间的触摸移动事件监听
        iv_drag.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //1.获取当前控件的x，y坐标值
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //2.获取控件移动后的x，y坐标值
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        //3.计算移动后的x，y距离值
                        int disX = moveX - startX;
                        int disY = moveY - startY;
                        //4.将移动后的x，y值分别复制给空间的上下左右边界
                        int left = iv_drag.getLeft() + disX;
                        int top = iv_drag.getTop() + disY;
                        int right = iv_drag.getRight() + disX;
                        int bottom = iv_drag.getBottom() + disY;
                        //5.设置对应空间移动后的所在位置
                        //容错判断，如空间超过屏幕的边界，返回true结束移动
                        if (left < 0) {
                            return true;
                        }
                        if (right > mScreen_width) {
                            return true;
                        }
                        if (top < 0) {
                            return true;
                        }
                        if (bottom > mScreen_height - 22) {
                            return true;
                        }
                        iv_drag.layout(left, top, right, bottom);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        //顶部、底部按钮显示隐藏判断，当控件移动超过屏幕高度一半时，
                        //顶部的按钮隐藏，底部按钮显示，反之
                        if (top > mScreen_height / 2) {
                            btn_top.setVisibility(View.VISIBLE);
                            btn_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            btn_top.setVisibility(View.INVISIBLE);
                            btn_bottom.setVisibility(View.VISIBLE);
                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        //手势抬起后将相应的x，y值保存在sp中，再次进入设置时调用
                        SpUtil.putInt(getApplicationContext(), ContastValue.LOCATION_X, iv_drag.getLeft());
                        SpUtil.putInt(getApplicationContext(), ContastValue.LOCATION_Y, iv_drag.getTop());
                        break;
                }
                //1.当前情况下返回false不响应事件，返回true才会响应，因为后续无onclick点击事件
                //2.在后续存在onclick点击事件，需要设置为false，才能执行后续的点击事件，
                //点击事件执行完成后，会返回ture，ontouch代码也会执行
                return false;
            }
        });
        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 500) {
                    //满足双击事件
                    int left = mScreen_width / 2 - iv_drag.getWidth() / 2;
                    int top = mScreen_height / 2 - iv_drag.getHeight() / 2;
                    int right = mScreen_width / 2 + iv_drag.getWidth() / 2;
                    int bottom = mScreen_height / 2 + iv_drag.getHeight() / 2;
                    iv_drag.layout(left,top,right,bottom);
                    SpUtil.putInt(getApplicationContext(), ContastValue.LOCATION_X, iv_drag.getLeft());
                    SpUtil.putInt(getApplicationContext(), ContastValue.LOCATION_Y, iv_drag.getTop());
                }
            }
        });
    }

}
