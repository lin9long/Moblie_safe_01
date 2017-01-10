package com.example.lin.moblie_safe_01.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Created by Administrator on 2016/12/17.
 */

public class SpUtil {
    public static SharedPreferences sp;

    //写入一个boolean类型的值
    public static void putBoolean(Context ctx, String name, boolean value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(name, value).commit();
    }

    //读取一个boolean类型的值
    public static boolean getBoolean(Context ctx, String name, boolean defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(name, defValue);
    }

    public static void putString(Context ctx, String name, String value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(name, value).commit();
    }

    //读取一个boolean类型的值
    public static String getString(Context ctx, String name, String defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(name, defValue);
    }

    public static void remove(Context ctx, String value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(value).commit();
    }

    public static int getInt(Context ctx, String name, int defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(name, defValue);
    }

    public static void putInt(Context ctx, String name, int value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(name, value).commit();
    }
}
