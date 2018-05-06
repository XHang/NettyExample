package com.netty.handler;

import com.netty.pojo.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 接受来自客户端的信息，并反馈信息回去
 */
public class DealPojoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        User user = (User) msg;
        System.out.println("已从客户端确认收到用户信息："+user);
        ctx.write("用户已经注册成功");
        ctx.flush();
    }
}
