package com.example.lin.moblie_safe_01.engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CheckBox;

import com.example.lin.moblie_safe_01.database.BlackNumberOpenHelper;
import com.example.lin.moblie_safe_01.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */

public class CommonNumberDao {
    public static String PATH = "data/data/com.example.lin.moblie_safe_01/files/commonnum.db";

    /**
     * @return 返回分组名称及对应的idx码
     */
    public List<Group> getGroup() {
        List<Group> groupList = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            //对象要放置在循环内部，因为每次执行都要生成一个新的对象存储数据
            Group group = new Group();
            //将childList作为类里面的成员，直接调用方法封装数据
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.childList = getChild(group.idx);
            groupList.add(group);
        }
        cursor.close();
        db.close();
        return groupList;
    }

    /**
     * @param idx getGroup方法中查询到的idx码
     * @return 根据idx码查询具体表格
     */
    public List<Child> getChild(String idx) {
        List<Child> childList = new ArrayList<>();
        //查询当前表格，根据穿入的索引值定位所查询的表格
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);

        while (cursor.moveToNext()) {
            Child child = new Child();
            child._id = cursor.getString(0);
            child.number = cursor.getString(1);
            child.name = cursor.getString(2);
            childList.add(child);
        }
        cursor.close();
        db.close();
        return childList;
    }

    public class Group {
        public String name;
        public String idx;
        public List<Child> childList;
    }

    public class Child {
        public String _id;
        public String number;
        public String name;
    }
}
