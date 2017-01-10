package com.example.lin.moblie_safe_01.engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lin.moblie_safe_01.database.BlackNumberOpenHelper;
import com.example.lin.moblie_safe_01.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */

public class BlackNumberDao {
    private static String TAB_NAME = "blacknumber";

    private final BlackNumberOpenHelper blackNumberOpenHelper;

    //1，私有化构造方法
    private BlackNumberDao(Context context) {
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    //2,声明一个当前类的对象
    private static BlackNumberDao blackNumberDao = null;

    //3，提供一个静态方法，如果当前类的对象为空，创建一个新的
    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 插入一行消息
     *
     * @param phone 插入信息的电话号码
     * @param mode  插入拦截类型，1：短信  2：电话  3：短信+电话
     */
    public void insert(String phone, String mode) {
        SQLiteDatabase dbwriter = blackNumberOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("phone", phone);
        cv.put("mode", mode);
        dbwriter.insert(TAB_NAME, null, cv);
        dbwriter.close();
    }

    /**
     * 删除一行消息
     *
     * @param phone 删除电话信息
     */
    public void delete(String phone) {
        SQLiteDatabase dbwriter = blackNumberOpenHelper.getWritableDatabase();
        dbwriter.delete(TAB_NAME, "phone = ?", new String[]{phone});
        dbwriter.close();
    }

    /**
     * 更新一行消息
     *
     * @param phone 对应查询条件
     * @param mode  修改的存储模式
     */
    public void updata(String phone, String mode) {
        SQLiteDatabase dbwriter = blackNumberOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mode", mode);
        dbwriter.update(TAB_NAME, cv, "phone = ?", new String[]{phone});
        dbwriter.close();
    }

    /**
     * 查询数据库中的所有数据
     *
     * @return 将数据封装在list集合里面，返回list集合
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase dbwriter = blackNumberOpenHelper.getWritableDatabase();

        List<BlackNumberInfo> list = new ArrayList<>();
        Cursor cursor = dbwriter.query(TAB_NAME, new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        while (cursor.moveToNext()) {
            BlackNumberInfo numberInfo = new BlackNumberInfo();
            numberInfo.phone = cursor.getString(0);
            numberInfo.mode = cursor.getString(1);
            list.add(numberInfo);
        }
        cursor.close();
        dbwriter.close();
        return list;
    }

    /**
     * @param index 输入需要查询的数据数量,实现分页查询功能
     * @return
     */
    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase dbwriter = blackNumberOpenHelper.getWritableDatabase();

        List<BlackNumberInfo> list = new ArrayList<>();
//        Cursor cursor = dbwriter.query(TAB_NAME, new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        Cursor cursor = dbwriter.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index + ""});
        while (cursor.moveToNext()) {
            BlackNumberInfo numberInfo = new BlackNumberInfo();
            numberInfo.phone = cursor.getString(0);
            numberInfo.mode = cursor.getString(1);
            list.add(numberInfo);
        }
        cursor.close();
        dbwriter.close();
        return list;
    }

    /**
     * @return 获取当前数据库内的数据条目总数
     */
    public int getCount() {
        SQLiteDatabase dbwriter = blackNumberOpenHelper.getWritableDatabase();
        int count = 0;
        Cursor cursor = dbwriter.rawQuery("select count(*) from blacknumber;", null);
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        dbwriter.close();
        return count;
    }
    public int getMode(String phone) {
        SQLiteDatabase dbwriter = blackNumberOpenHelper.getWritableDatabase();
        int mode = 0;
        Cursor cursor = dbwriter.query(TAB_NAME,new String[]{"mode"},"phone = ?",new String[]{phone},null,null,null);
        while (cursor.moveToNext()) {
           mode = cursor.getInt(0);
        }
        cursor.close();
        dbwriter.close();
        return mode;
    }
}
