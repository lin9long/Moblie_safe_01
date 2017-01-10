package com.example.lin.moblie_safe_01.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lin on 2016/12/7.
 */

public class ToastUtil {
    public static void show(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
