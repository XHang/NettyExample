package com.netty.handler;

import java.io.UnsupportedEncodingException;

import com.netty.pojo.User;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 接受来自客户端的信息，并反馈信息回去
 */
public class DealPojoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException, UnsupportedEncodingException  {
    	
        User user = (User) msg;
        System.out.println("已从客户端确认收到用户信息："+user);
        ChannelFuture channelFuture = ctx.write("用户注册成功");
        ctx.flush();
        //堵塞线程，等服务端发给客户端的信息确认无误之后，再放行，这里不想堵塞
     /*   channelFuture.sync();*/
    }
}
