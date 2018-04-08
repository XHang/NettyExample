package com.netty.discard;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
/**
 * 小程序:实现一个接收数据的服务端,但是丢弃接收到的数据
 * 这个类继承了ChannelInboundHandlerAdapter,而这个父类又实现了ChannelInboundHandler
 * 它提供了很多事件方法,你可以覆盖实现它
 * @author cxh
 *
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter{
	
	/**
	 * 每当接收来自客户端的信息时,都会执行该方法
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	    ByteBuf in = (ByteBuf) msg;
	    try {
	    	String info = in.toString(Charset.forName("utf-8"));
	    	System.out.println(info);
	       /* while (in.isReadable()) { // (1)
	            System.out.print((char) in.readByte());
	            in.
	            System.out.flush();
	        }*/
	    } finally {
	    	//释放引用计数器的对象msg
	        ReferenceCountUtil.release(msg); // (2)
	    }
	}
	 @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
	        // 当异常发生时关闭链接
	        cause.printStackTrace();
	        ctx.close();
	    }
}
