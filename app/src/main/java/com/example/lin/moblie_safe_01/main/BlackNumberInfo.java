package com.example.lin.moblie_safe_01.main;

/**
 * Created by Administrator on 2016/12/28.
 */

public class BlackNumberInfo {

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "phone='" + phone + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }

    public String phone;
    public String mode;
}
