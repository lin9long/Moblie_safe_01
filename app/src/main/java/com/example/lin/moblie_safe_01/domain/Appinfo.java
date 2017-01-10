package com.example.lin.moblie_safe_01.domain;

import android.graphics.drawable.Drawable;

/**
 *  获取所有app信息，包括名称、包名、图标、（系统或用户应用）、（系统内存或sd卡）
 * Created by Administrator on 2016/12/31.
 */

public class Appinfo {
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSdcard() {
        return isSdcard;
    }

    public void setSdcard(boolean sdcard) {
        isSdcard = sdcard;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }


    public String packageName;
    public String appName;
    public Drawable icon;
    public boolean isSdcard;
    public boolean isSystem;
}
