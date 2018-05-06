package com.netty.handler;

import com.netty.pojo.User;
import com.netty.uitl.InstanceUitl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;

public class DealPojoToByteBuffHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
       System.out.println("正在编码成字节数组，发到服务端");
        User user = (User) msg;
        byte[] data = InstanceUitl.instanceToByteArray(user);
        ByteBuf buf = Unpooled.copiedBuffer(data);
        ctx.write(buf,promise);
        //这个flush操作不用完成，Netty已经帮我们自动处理了。
        //ctx.flush();
    }
}
