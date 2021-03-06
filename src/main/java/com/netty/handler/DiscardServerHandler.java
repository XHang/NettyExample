package com.netty.handler;

import java.nio.charset.Charset;

import com.netty.uitl.BufferUitl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
/**
 * 小程序:实现一个接收数据的服务端,但是丢弃接收到的数据
 * 这个类继承了ChannelInboundHandlerAdapter,而这个父类又实现了ChannelInboundHandler
 * 它提供了很多事件方法,你可以覆盖实现它
 * 测试方法，可以键入 telnet localhost 8080 尝试向这个服务器发送消息
 * @author cxh
 *
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter{
	
	/**
	 * 每当接收来自客户端的信息时,都会执行该方法
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	    ByteBuf buf = (ByteBuf) msg;
	    try {
	    	String message = BufferUitl.getContentOfOld(buf);
	    	String msgOfSend = "I already received you message："+message;
	    	System.out.println(msgOfSend);
	    	//下面两句可以合并为ctx.writeAndFlush(msg) ；
			ctx.write('a');
			ctx.flush();
	    } finally {
	    	//释放资源
	        ReferenceCountUtil.release(msg); // (2)
	    }
	}



	/**
	 * 由于IO错而导致异常或者channelRead方法抛出异常都会调用到此方法
	 * @param ctx
	 * @param cause
	 */
	 @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
	        // 当异常发生时关闭链接

	        cause.printStackTrace();
	        ctx.close();
	    }
}
