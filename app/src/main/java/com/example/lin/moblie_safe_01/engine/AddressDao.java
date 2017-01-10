package com.example.lin.moblie_safe_01.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/23.
 */

public class AddressDao {
    public static String PATH = "data/data/com.example.lin.moblie_safe_01/files/phoneaddress.db";
    private static String tag = "AddressDao";
    public static String carr;
    public static String phone3;
    public static String mLocation = "未知号码";

    /**
     * @param phone 查询的手机号码
     * @return String 返回电话归属地
     */
    public static String getAddress(String phone) {
        mLocation = "未知号码";
        String reguar = "^1[3-8]\\d{9}";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(reguar)) {
            phone = phone.substring(0, 7);
            phone3 = phone.substring(0, 3);
            //查询地址码
            Cursor callerLoc = database.query("CallerLoc", new String[]{"location"}, "number = ?", new String[]{phone}, null, null, null);
            //查询运营商
            Cursor caller_carrier = database.query("CarrierInfo", new String[]{"Carrier"}, "prefix = ?", new String[]{phone3}, null, null, null);
            if (caller_carrier.moveToNext()) {
                carr = caller_carrier.getString(0);
                caller_carrier.close();
            }
            if (callerLoc.moveToNext()) {
                //根据前面查询到的地址码，查询出对应的电话归属地
                String id = callerLoc.getString(0);
                Cursor cursor = database.query("LocationInfo", new String[]{"location"}, "_id = ?", new String[]{id}, null, null, null);
                if (cursor.moveToNext()) {
                    mLocation = carr + " " + cursor.getString(0);

                    Log.i(tag, "location = " + mLocation + carr);
                } else {
                    mLocation = "未知号码";
                }
            }
            //如果内容不满足正则表达式，执行下述条件判断
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mLocation = "报警电话";
                    break;
                case 4:
                    mLocation = "模拟器";
                    break;
                case 5:
                    mLocation = "服务电话";
                    break;
                case 7:
                    mLocation = "本地电话";
                    break;
                case 8:
                    mLocation = "本地电话";
                    break;
                case 11:
                    String code = phone.substring(1, 3);
                    System.out.println(code);
                    Cursor cursor = database.query("LocationInfo", new String[]{"location"}, "code=?", new String[]{code}, null, null, null);
                    if (cursor.moveToNext()) {
                        mLocation = cursor.getString(0);
                    } else {
                        mLocation = "未知号码";
                    }
                    break;
                case 12:
                    String code1 = phone.substring(1, 4);
                    Cursor cursor1 = database.query("LocationInfo", new String[]{"location"}, "code=？", new String[]{code1}, null, null, null);
                    if (cursor1.moveToNext()) {
                        mLocation = cursor1.getString(0);
                    } else {
                        mLocation = "未知号码";
                    }
                    break;
            }
        }
        return mLocation;
    }
}
