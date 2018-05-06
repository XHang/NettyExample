package com.netty.handler;

import io.netty.channel.*;

import java.net.InetAddress;
import java.util.Date;

public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 当连接通路连接完毕时，向客户端发送问候
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive run!");
        ctx.write("welcome to "+InetAddress.getLocalHost().getHostName()+"!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("sdgfdhfg");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //TODO 通道读取完毕？什么时候调用呢？flush又做了什么？
        System.out.println("channelReadComplete run!");
        ctx.flush();
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String request =  msg;
        boolean isclose = false;
        ChannelFuture channelFuture = null;
        System.out.println("client say:"+request);
        String response = null;
        //如果客户端没有发送任何数据过来的话，返回提醒的信息
        if(request ==null || request.isEmpty()){
            response = "please type msg";
        }else if("bye".equals(request)){
            response = "have a good day!";
            isclose = true;
        }else{
            response = "Di you say:"+request+"?";
        }
        response+="\r\n";
        channelFuture =  ctx.writeAndFlush(response);
        if (isclose){
            if (channelFuture!=null){
                channelFuture.sync();
                //如果channelFuture任务都完成了，就会通知close的侦听器。
                channelFuture.addListener(ChannelFutureListener.CLOSE);
                //TODO 下面那个加了会怎么样？
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
