package netty;

import com.netty.handler.DealPojoHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
        //那么，父类的Group，我没有什么要处理器处理的，不设置，可以的把？
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new PojoDecoderHandler(),new DealPojoHandler());
            }
        });
        bootstrap.bind(port).sync().channel().closeFuture();

    }
}
