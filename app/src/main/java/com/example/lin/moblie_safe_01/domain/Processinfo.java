package com.example.lin.moblie_safe_01.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/1/1.
 */

public class Processinfo {
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public long getMemsize() {
        return memsize;
    }

    public void setMemsize(long memsize) {
        this.memsize = memsize;
    }

    public String packagename;
    public Drawable icon;
    public boolean ischeck;
    public boolean isSystem;
    public long memsize;
}
