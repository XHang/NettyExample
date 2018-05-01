package com.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

/**
 * 时间处理器，不接受任何信息，连接一旦建立，则发送信息
 */
@ChannelHandler.Sharable
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当通道建立，流量产生时，该方法将自动调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        //得到一个ByteBuff用于存储32位整数的数字，所以需要4个字节
        ByteBuf buf = ctx.alloc().buffer(4);
        //写入当前时间
        buf.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        //将信息写入通道，而该ChannelFuture通道表示尚未发生IO操作
        ChannelFuture channel = ctx.writeAndFlush(buf);
        //为ChannelFuture通道添加一个监听器
        addCloseListenerOfOld(channel,ctx);
    }

    /**
     * 为通道添加关闭监听器。比较老式的写法，其实Netty为我们提供了更简单的写法
     * @param channel
     * @param ctx
     */
    private void addCloseListenerOfOld(ChannelFuture channel,final ChannelHandlerContext ctx){
        channel.addListener(new ChannelFutureListener() {
            //当操作完成时，自动调用此方法
            public void operationComplete(ChannelFuture future) throws Exception {
                //操作完成后，关闭连接。未必完成，
                ctx.close();
            }
        });
    }
    /**
     * 为通道添加关闭监听器。比较新式的写法，其实只需要一句话就足够了
     * @param channel
     */
    private void addCloseListenerOfNew(ChannelFuture channel){
        channel.addListener(ChannelFutureListener.CLOSE);
    }

}
