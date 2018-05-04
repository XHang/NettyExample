package netty;

import com.netty.handler.TelnetServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * Telent服务端，负责接收来自客户端的信息，包装起来，然后发回去
 */
public class TelnetServer {

    public static final int PORT = 8080;

    public static void main (String[] args) throws InterruptedException {
        ServerBootstrap serverbootStart = new ServerBootstrap();
        EventLoopGroup boss  = new NioEventLoopGroup(1);
        EventLoopGroup worker  = new NioEventLoopGroup();
        try {
            serverbootStart.group(boss,worker);
            serverbootStart.channel(NioServerSocketChannel.class);
            //子处理器和父处理器有什么区别？
            serverbootStart.handler(new LoggingHandler(LogLevel.INFO));
            serverbootStart.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("初始化通道");
                    ChannelPipeline pipeline  =ch.pipeline();
                    //通道初始化时，添加这四个处理器
                    ChannelHandler frameDecoder = new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter());
                    ChannelHandler decoder = new StringDecoder();
                    ChannelHandler encoder = new StringEncoder();
                    ChannelHandler telnetHandler = new TelnetServerHandler();
                    pipeline.addLast(frameDecoder);
                    pipeline.addLast(decoder);
                    pipeline.addLast(encoder);
                    pipeline.addLast(telnetHandler);
                }
            });
            //这么写的话，不就是会关闭服务器吗？
            serverbootStart.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
