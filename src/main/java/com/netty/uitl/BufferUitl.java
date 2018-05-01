package com.netty.uitl;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class BufferUitl {

    /**
     * 新的，从ByteBuf中取出客户端发来的信息，并转成字符串
     * @param buf
     * @return
     */
    public static  String getContentOfNew(ByteBuf buf){
        return buf.toString(Charset.forName("utf-8"));
    }
    /**
     * 旧的，从ByteBuf中取出客户端发来的信息，并转成字符串
     * @param buf
     * @return
     */
    public static  String getContentOfOld(ByteBuf buf) {
        StringBuilder sb = new StringBuilder();
        while (buf.isReadable()) {
            sb.append((char)buf.readByte());
            System.out.flush();
        }
        return sb.toString();
    }
}
