package com.netty.handler;

import com.netty.uitl.BufferUitl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

/**
 * 时间客户端处理器
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf byteBuf;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        dealMsgOfNew(ctx, msg);
    }

    /**
     * 当ChannelHandler(也就是当前类)被添加到实际的上下文时调用。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        byteBuf = ctx.alloc().buffer(4);
    }

    /**
     * 顾名思义
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        byteBuf.release();
        byteBuf = null;
    }

    /**
     *处理数据-老的方法，一旦数据按批次传来的话，就挂了
     * @param ctx
     * @param msg
     */
    public void dealMsgOfOld(ChannelHandlerContext ctx, Object msg){
        try {
            long currentTimeMillis = (((ByteBuf) msg).readUnsignedInt() - 2208988800L) * 1000L;
            //String message  = BufferUitl.getContentOfNew((ByteBuf) msg);
            System.out.println("from Time Server receive msg :"+new Date(currentTimeMillis));
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
    /**
     *处理数据-新的方法，可以处理分批传来的数据。
     * @param ctx
     * @param msg
     */
    public void dealMsgOfNew(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        //将通道里面的数据写入缓冲区里面
        byteBuf.writeBytes(m); // (2)
        ctx.close();
        ReferenceCountUtil.release(msg);
        //如果缓冲区里面可读的字节有4个及以上
        if (byteBuf.readableBytes() >= 4) { // (3)
            long currentTimeMillis = (byteBuf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
