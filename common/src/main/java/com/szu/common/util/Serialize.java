package com.szu.common.util;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:42
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Serialize {


    public static byte[] serialize(Object object) {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutStream = null;
        byte[] byteBody = null;
        try {
            objectOutStream = new ObjectOutputStream(byteOutStream);
            objectOutStream.writeObject(object);
            byteBody = byteOutStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutStream != null)
                    objectOutStream.close();
                byteOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println(byteBody.length);
        return byteBody;
    }

}
