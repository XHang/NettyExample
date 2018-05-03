package com.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

import java.net.InetAddress;
import java.util.Date;

public class TelnetServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("welcome to "+InetAddress.getLocalHost().getHostName()+"!");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String request = (String) msg;
        boolean isclose = false;
        if(request !=null || request.isEmpty()){
            ctx.write("please type msg");
        }else if("bye".equals(request)){
            ctx.write("have a good day!");
            return;
        }else{

        }
    }
}
