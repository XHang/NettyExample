package com.netty.uitl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 对象工具类
 */
public class InstanceUitl {

    /**
     * 将对象转成字节数组--要求对象必须实现序列化接口
     * @param obj
     * @return
     */
    public static byte[] instanceToByteArray(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
        out.writeObject(obj);
        out.flush();
        return byteArrayOutputStream.toByteArray();
    }
}
