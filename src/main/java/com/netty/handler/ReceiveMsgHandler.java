package com.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.Charset;

public class ReceiveMsgHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端发来信息了诶");
        ByteBuf byteBuf = (ByteBuf) msg;
        String response = byteBuf.toString(Charset.forName("utf-8"));
        System.out.println("服务端发来反馈："+response);
    }
}
