package com.example.lin.moblie_safe_01.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.lin.moblie_safe_01.engine.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/12/29.
 */
public class BlackNumberService extends Service {

    private BlackNumberService.mInnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener mMyphonestatelistener;
    private MyObserver observer;

    @Override
    public void onCreate() {

        mDao = BlackNumberDao.getInstance(getApplicationContext());
        //显示配置intentFilter，添加读取短信信息的action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //设置action的权限
        intentFilter.setPriority(10000);
        //配置一个BroadcastReceiver，注册并使用广播接收
        mInnerSmsReceiver = new mInnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver, intentFilter);

        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mMyphonestatelistener = new MyPhoneStateListener();
        mTM.listen(mMyphonestatelistener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        //手动重写，手机状态发生变化时出发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                //手机状态空闲时
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("tag", "手机状态空闲时");
                    break;
                //手机状态挂断时
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("tag", "手机状态挂断时");
                    break;
                //手机状态响铃时
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i("tag", "手机状态响铃时");
                    endcall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    }

    private void endcall(String incomingNumber) {
        int mode = mDao.getMode(incomingNumber);
        if (mode == 2 || mode == 3) {
            try {
                //ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                //获取对应ServiceManager的包名
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //获取方法
                Method method = clazz.getMethod("getService", String.class);
                //反射调用方法，获取iBinder参数
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //获取iTelephony，用来调用endcall方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                iTelephony.endCall();


            } catch (Exception e) {
                e.printStackTrace();
            }
            //清除通话记录内的系统日志
            //如果直接使用ContentResolver删除数据，会有bug出现，最后一条记录无法删除
            //解决方案：使用ContentObserver观察数据库变化，在发生变化后再执行删除操作
            observer = new MyObserver(new Handler(), incomingNumber);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, observer);
        }
    }
    //新建一个ContentObserver
    class MyObserver extends ContentObserver {
        private String phone;
        public MyObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }
        //监听数据库变化后，执行删除操作
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().
                    delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{phone});
            super.onChange(selfChange);

        }
    }

    class mInnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //拦截获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String phonenum = sms.getOriginatingAddress();


                int mode = mDao.getMode(phonenum);
                if (mode == 1 || mode == 3) {
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        mTM.listen(mMyphonestatelistener, PhoneStateListener.LISTEN_NONE);
        if (mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
