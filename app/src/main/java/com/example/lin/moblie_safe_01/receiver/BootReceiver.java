package com.example.lin.moblie_safe_01.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;


/**
 * Created by Administrator on 2016/12/20.
 */

public class BootReceiver extends BroadcastReceiver {
    private String tag = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //使用BroadcastReceiver发送手机重启请求，需要添加设备重启监听权限
        //获取当前手机的sim卡号
        TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String simserialnumber = manager.getSimSerialNumber()+"123";
        String defult_sim_num = SpUtil.getString(context, ContastValue.SIM_NUMBER,"");
        if (!simserialnumber.equals(defult_sim_num)){
            //新旧sim卡作比对比对如果不一致就发送信息，需要添加发送短信权限
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("15555215558",null,"sim is change,you phone maybe lost！！！",null,null);
        }
    }
}
