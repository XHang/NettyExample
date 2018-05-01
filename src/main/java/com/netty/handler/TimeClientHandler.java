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
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       /* try {
            long currentTimeMillis = (((ByteBuf) msg).readUnsignedInt() - 2208988800L) * 1000L;
            //String message  = BufferUitl.getContentOfNew((ByteBuf) msg);
            System.out.println("from Time Server receive msg :"+new Date(currentTimeMillis));
            super.channelRead(ctx, msg);
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }*/
       ByteBuf buf = ctx.alloc().buffer(4);
        ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m); // (2)
        m.release();

        if (buf.readableBytes() >= 4) { // (3)
            long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
