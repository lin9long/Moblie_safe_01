package com.example.lin.moblie_safe_01.receiver;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.media.MediaPlayer;
import android.net.Uri;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.service.LocationService;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;

/**
 * Created by Administrator on 2016/12/21.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean open_security = SpUtil.getBoolean(context, ContastValue.OPEN_SECURITY, false);
        if (open_security) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri SMS_URI = Uri.parse("content://sms/");
            Cursor cursor = contentResolver.query(SMS_URI, new String[]{"body"}, null, null, null);
            while (cursor.moveToNext()) {
                String body = cursor.getString(0);
                if (body.contains("#*alarm*#")) {
                    //获取短信编码
//            Object[] objects = (Object[]) intent.getExtras().get("pdus");
//            for (Object object : objects) {
//                SmsMessage message = SmsMessage.createFromPdu((byte[]) object, null);
//                String address = message.getOriginatingAddress();
//                String msg = message.getMessageBody();
//                if (msg.contains("#*alarm*#")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.cele);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if (body.contains("#*location*#")){
                      context.startService(new Intent(context, LocationService.class));
                }
                if (body.contains("#*wipedata*#")){
                    //执行清除手机数据代码
                }
                if (body.contains("#*lockscreen*#")){
                    //执行锁定屏幕代码
                }
            }
        }
    }
}
