package com.netty.uitl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty开启服务器的一个简单工具线程
 */
public class ServerThread implements Runnable {
    private int port;
    private ChannelInboundHandlerAdapter header;

    /**
     * 创建一个服务器线程
     * @param port 端口
     * @param header 通道处理器
     */
    public ServerThread(int port,ChannelInboundHandlerAdapter header){
        this.header = header;
        this.port = port;
    }
    public void run() {
        //作用：接受传入的链接，并将连接扔给workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理连接的流量
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //建立服务器的助手类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置父级和子级的EventLoopGroup
            bootstrap.group(bossGroup, workerGroup)
                    //当新的连接进来时，将new一个NioServerSocketChannel接受传入的连接。所以下面的代码是一段配置
                    .channel(NioServerSocketChannel.class)
                    //设置下服务端请求的通道处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        //这个方法将在通道注册后调用
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //在这个通道的最后插入一段处理器
                            ch.pipeline().addLast(header);
                        }
                    })
                    //一旦创建了Channel，可以指定它的ChannelOption
                    .option(ChannelOption.SO_BACKLOG,128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // 绑定端口并开始接收链接
            ChannelFuture f = bootstrap.bind(port).sync(); // (7)

            //等待Server Socket 关闭,在这个例子中,这不会发生,但是你可以做到这一点
            //关闭你的服务器
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //简单处理，事实上，这个异常要好好处理下
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
