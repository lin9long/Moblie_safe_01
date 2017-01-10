package com.example.lin.moblie_safe_01.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lin on 2016/12/6.
 */
public class SteamUtil {
    public static String steamtoString(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;
        try {
            while ((temp = is.read(buffer)) != -1) {
                bos.write(buffer, 0, temp);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
