package com.example.lin.moblie_safe_01.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取antivirus数据库内的病毒MD5码的listview
 */

public class AntVirusDao {
    public static String PATH = "data/data/com.example.lin.moblie_safe_01/files/antivirus.db";

    public static List<String> getVirusList() {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> viruslist = new ArrayList<>();
        while (cursor.moveToNext()) {
            viruslist.add(cursor.getString(0));
        }
        return viruslist;
    }


}
