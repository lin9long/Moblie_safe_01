package com.example.lin.moblie_safe_01.service;

import android.app.AlarmManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.AddressDao;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;

/**
 * Created by Administrator on 2016/12/24.
 */
public class AddressService extends Service {
    private TelephonyManager mTM;
    private String tag = "AddressService";
    private MyPhoneStateListener mMyphonestatelistener;
    private WindowManager mWM;
    private View mViewToast;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private AlarmManager mAM;
    private TextView tv_toast;
    private String address;
    //在handler触发ui更新
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(address);
        }
    };
    private int startX;
    private int startY;
    private int mScreen_height;
    private int mScreen_width;
    private InnterOutCallReceiver mInnterOutCallReceiver;


    //开启服务时，需要对手机状态进行监听，管理土司显示
    @Override
    public void onCreate() {
        //获取电话工具类
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mMyphonestatelistener = new MyPhoneStateListener();
        mTM.listen(mMyphonestatelistener, PhoneStateListener.LISTEN_CALL_STATE);
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //获取当前屏幕的宽与高
        mScreen_height = mWM.getDefaultDisplay().getHeight();
        mScreen_width = mWM.getDefaultDisplay().getWidth();
        //设置意图过滤器，监听广播事件，显示播出电话归属地
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        mInnterOutCallReceiver = new InnterOutCallReceiver();
        registerReceiver(mInnterOutCallReceiver, intentFilter);
        super.onCreate();
    }

    class InnterOutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = getResultData();
            System.out.println(result + "-----------------");
            // showToast(result);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mTM.listen(mMyphonestatelistener, PhoneStateListener.LISTEN_NONE);
        if (mInnterOutCallReceiver != null) {
            unregisterReceiver(mInnterOutCallReceiver);
        }
        super.onDestroy();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        //手动重写，手机状态发生变化时出发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                //手机状态空闲时
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(tag, "挂断电话。。");
                    if (mWM != null && mViewToast != null) {
                        mWM.removeView(mViewToast);
                        mViewToast = null;
                    }
                    break;
                //手机状态挂断时
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(tag, "电话接通中。。");
                    break;
                //手机状态响铃时
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(tag, "电话响了。。。。");
                    System.out.println("-------------" + incomingNumber);
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    }

    public void showToast(String incomingNumber) {

        // Toast.makeText(getApplicationContext(), incomingNumber, Toast.LENGTH_LONG).show();
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //   params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        //此处不能使用TYPE_PHONE字段，可能系统对电话状态权限做设置
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //需要将源码中的NOT_TOUCHABLE状态删除，否则无法移动自定义土司位置
        // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        params.gravity = Gravity.LEFT + Gravity.TOP;
        mViewToast = View.inflate(getApplicationContext(), R.layout.toast_view, null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);
        //设置土司的触摸监听事件，使其可以被拖拽至任意位置
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
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

                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        //4.进行容错判断，防止控件跑出屏幕边界
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mScreen_width - mViewToast.getWidth()) {
                            params.x = mScreen_width - mViewToast.getWidth();
                        }
                        if (params.y > mScreen_height - mViewToast.getHeight() - 22) {
                            params.y = mScreen_height - mViewToast.getHeight() - 22;
                        }

                        mWM.updateViewLayout(mViewToast, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        //将移动后的空间位置，与LocationActivity内体现移动后的位置相对应修改
                        SpUtil.putInt(getApplicationContext(), ContastValue.LOCATION_X, params.x);
                        SpUtil.putInt(getApplicationContext(), ContastValue.LOCATION_Y, params.y);
                        break;
                }
                return true;
            }
        });


        //从SP中获取LoactionActivity内设置的土司位置
        params.x = SpUtil.getInt(getApplicationContext(), ContastValue.LOCATION_X, 0);
        params.y = SpUtil.getInt(getApplicationContext(), ContastValue.LOCATION_Y, 0);

        int[] toastStyleID = new int[]{R.drawable.toast_color1, R.drawable.toast_color2, R.drawable.toast_color3,
                R.drawable.toast_color1, R.drawable.toast_color2};
        int toastIndex = SpUtil.getInt(getApplicationContext(), ContastValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(toastStyleID[toastIndex]);
        mWM.addView(mViewToast, params);
        //查询电话归属地设置
        query(incomingNumber);
    }


    //开启一个线程,执行耗时操作的来电归属地显示
    private void query(final String incomingNumber) {
        new Thread() {
            @Override
            public void run() {
                address = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
