package netty;

import com.netty.handler.TimeClientHandler;
import com.netty.handler.TimeServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
    public static final int DESC_PORT = 8080;
    public static final String DESC_HOST = "localhost";
    public static void main (String[] args) throws InterruptedException {
        //建立一个工作组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            //将工作组和启动器关联起来
            bootstrap.group(workerGroup);
            //该启动器使用的通道类型是NioSocketChannel
            bootstrap.channel(NioSocketChannel.class);
            //设置启动器
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            //为该启动器设定处理器
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());

                }
            });
            ChannelFuture channelFuture = bootstrap.connect(DESC_HOST,DESC_PORT);
            //等待直到连接关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
