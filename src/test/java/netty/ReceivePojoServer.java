package netty;

import java.nio.charset.Charset;

import com.netty.handler.DealPojoHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 接受来自客户端的对象，并返回成功信息
 */
public class ReceivePojoServer {
    public static final  int port = 8080;
	public static void main (String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss,worker);
        //突然有一种想法，唔~会不会服务端要设立父处理器和子处理器，正好对应两个EventLoopGroup呢？
        bootstrap.channel(NioServerSocketChannel.class);
        //那么，父类的Group，我没有什么要处理器处理的，不设置，可以的把？已验证，是可以的
        //这里给一个日志的处理器用于记录一些请求到来的日志
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.childHandler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new PojoDecoderHandler(),new StringEncoder(Charset.forName("utf-8")),new DealPojoHandler());
            }
        });
        bootstrap.bind(port).sync().channel().closeFuture().sync();
    }
}
