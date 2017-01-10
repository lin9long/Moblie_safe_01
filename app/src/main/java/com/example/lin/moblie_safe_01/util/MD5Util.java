package com.example.lin.moblie_safe_01.util;

import org.xutils.common.util.MD5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/12/17.
 */

public class MD5Util {

    public static String encoder(String psd) {
        // TODO Auto-generated method stub
        try {
            String password = psd + "mobliesafe";
            //1.指定加密算法
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2.将需要加密的字符串转换为byte类型数组，进行随机哈希过程
            byte[] bs = digest.digest(password.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            //3.遍历bs，生产32位字符串
            for (byte b : bs) {
                int i = b & 0xff;
                //	System.out.println(i);
                String hexString = Integer.toHexString(i);
                //System.out.println(hexString);
                //4.给单个字符前加上0,拼凑字符串
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }

                stringBuffer.append(hexString);
            }
            //System.out.println(stringBuffer.toString());
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

}
