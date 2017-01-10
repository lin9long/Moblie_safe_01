package com.example.lin.moblie_safe_01.engine;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/30.
 */

public class SmsBackup {

    private static XmlSerializer xmlSerializer;
    private static int count;
    private static Cursor cursor = null;
    private static FileOutputStream fos = null;
    private static int index = 0;

    public static void backup(Context ctx, String path, Callback callback) {
        try {
            File file = new File(path);
            //使用内容解析器查询短信内容
            ContentResolver resolver = ctx.getContentResolver();
            cursor = resolver.query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);

            fos = new FileOutputStream(file);
            //序列化数据库中读取数据，放置在xml上
            xmlSerializer = Xml.newSerializer();
            //给xml进行相应的配置
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "smss");
            //获取总的短信数量，给进度条赋值
            count = cursor.getCount();
            if (callback != null) {
                callback.setMax(count);
            }
            while (cursor.moveToNext()) {
                //序列化xml
                xmlSerializer.startTag(null, "sms");

                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");

                xmlSerializer.endTag(null, "sms");

                //设置进度条的进度过程
                index++;
                Thread.sleep(100);
                //callback是一个对象，对象调用的方法在AToolsActivity中的new Callback中实现
                // ，callback的作用仅仅是调用一下你已经实现的方法
                if (callback != null) {
                    callback.setProgress(index);
                }

            }

            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null && cursor != null) {
                try {
                    cursor.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //回调方法步骤
    // 1.自定义一个接口
    // 2.接口内定义未实现的业务逻辑方法
    // 3.传入一个实现上述未实现方法的类的对象，实现上述两个未实现的构造方法（注意使用回调前，必须要进行非空判断，防止穿入空对象）
    // 4.传递进来的对象，在合适的地方做方法调用
    public interface Callback {
        public void setMax(int max);

        public void setProgress(int index);
    }
}
