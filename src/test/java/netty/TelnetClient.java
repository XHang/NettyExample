package netty;

import com.netty.handler.TelnetClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TelnetClient {
    public static final  String DESC_ADRESS="127.0.0.1";

    public static  final int DESC_PORT = 8080;

    public static void main (String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup clientEvent = new NioEventLoopGroup();
        Channel channel = null;
        try {
            bootstrap.group(clientEvent);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer(){
                protected void initChannel(Channel ch) throws Exception {
                    ChannelHandler frameDecoder = new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter());
                    ChannelHandler decoder = new StringDecoder();
                    ChannelHandler encoder = new StringEncoder();
                    ChannelHandler telnetHandler = new TelnetClientHandler();
                    ch.pipeline().addLast(frameDecoder,decoder,encoder,telnetHandler);
                }
            });
            channel = bootstrap.connect(DESC_ADRESS,DESC_PORT).sync().channel();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            ChannelFuture lastWriteFuture = null;
            //没有条件的无限循环
            for (;;){
                String line = reader.readLine();
                if(line == null){
                    break;
                }
                //写入的同时也将发送到服务端。。虽然不是立即发送，因为是异步的
                lastWriteFuture  = channel.writeAndFlush(line);
                if("bye".equals(line)){
                    break;
                }
            }
            //如果跳出循环后发现lastWriteFuture不为空，则同步一下，确保没有滞留的信息未处理
            if(lastWriteFuture !=null) {
                lastWriteFuture.sync();
            }
        } finally {
            if (channel!= null){
                //被注释的代码是请求关闭通道，并在通道关闭之后，通知 ChannelFuture
                /*channel.close();*/
                //当关闭通道的时候通知ChannelFuture，并返回它。
                channel.closeFuture().sync();
            }
            clientEvent.shutdownGracefully();
        }
    }

}
