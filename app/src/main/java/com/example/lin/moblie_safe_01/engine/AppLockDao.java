package com.example.lin.moblie_safe_01.engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.lin.moblie_safe_01.database.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */

public class AppLockDao {
    private Context context;
    private static String TAB_NAME = "applock";

    private final AppLockOpenHelper appLockOpenHelper;

    //1，私有化构造方法
    private AppLockDao(Context context) {
        this.context = context;
        appLockOpenHelper = new AppLockOpenHelper(context);
    }

    //2,声明一个当前类的对象
    private static AppLockDao appLockDao = null;

    //3，提供一个静态方法，如果当前类的对象为空，创建一个新的
    public static AppLockDao getInstance(Context context) {
        if (appLockDao == null) {
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    /**
     * @param packagename 插入应用包名到数据库
     */
    public void insert(String packagename) {
        SQLiteDatabase dbwriter = appLockOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packagename);
        dbwriter.insert(TAB_NAME, null, values);
        dbwriter.close();
        //数据库发生改变时，给ContentObserver发送通知过程
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    /**
     * @param packagename 删除指定包名的数据
     */
    public void delete(String packagename) {
        SQLiteDatabase dbwriter = appLockOpenHelper.getWritableDatabase();

        dbwriter.delete(TAB_NAME, "packagename =?", new String[]{packagename});
        dbwriter.close();
        //数据库发生改变时，给ContentObserver发送通知过程
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    /**
     * 查询数据库方法
     *
     * @return 返回一个list的集合，里面为packagename
     */
    public List<String> findAll() {
        SQLiteDatabase dbwriter = appLockOpenHelper.getWritableDatabase();
        List<String> lockPackageList = new ArrayList<>();
        Cursor cursor = dbwriter.query(TAB_NAME, new String[]{"packagename"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            lockPackageList.add(cursor.getString(0));
        }
        dbwriter.close();
        cursor.close();
        return lockPackageList;

    }
}
