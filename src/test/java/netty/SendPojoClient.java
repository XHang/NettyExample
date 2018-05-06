package netty;

import com.netty.handler.DealPojoToByteBuffHandler;
import com.netty.handler.ReceiveMsgHandler;
import com.netty.pojo.User;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 发送Pojo对象到服务端
 * 简单来说，我们要做的，就是客户端推送一个对象过去，然后服务端接受
 * 这个过程要用到对象的序列化。以上
 *
 */
public class SendPojoClient {
    public static final String DESC_ADDRESS = "127.0.0.1";
    public static final int PORT = 8080;
    public static void main (String[] args) throws InterruptedException, IOException {
        Bootstrap start = new Bootstrap();
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        start.group(clientGroup);
        start.channel(NioSocketChannel.class);
        start.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ReceiveMsgHandler());
                ch.pipeline().addLast(new DealPojoToByteBuffHandler());
            }
        });
        Channel channel = start.connect(DESC_ADDRESS,PORT).sync().channel();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ChannelFuture channelFuture = null;
        for(;;){
            System.out.println("录入用户信息？不要的话，输入bye结束程序");
            String flag= reader.readLine();
            if("bye".equals(flag)){
                break;
            }
            User user = getUserByInput(reader);
            System.out.println("已确认新用户信息，正在推送中....");
            channelFuture = channel.writeAndFlush(user);
        }
        if (channelFuture!=null){
            channelFuture.sync();
        }
        channel.closeFuture().sync();
        //下面的代码理应包含在finally中，为了示例程序简洁，就不包含了。
        clientGroup.shutdownGracefully();
    }

    private static User getUserByInput(BufferedReader reader) throws IOException {

        User user  = new User();
        System.out.println("请输入用户的姓名");
        String userName =reader.readLine();
        user.setUserName(userName);
        System.out.println("请输入用户的密码");
        String password = reader.readLine();
        user.setPassword(password);
        System.out.println("请输入年龄");
        String age = reader.readLine();
        try{
            user.setAge(Integer.parseInt(age));
        }catch(Exception e){
            System.out.println("系统检测到你的年龄有误，将设置默认年龄1岁，惊不惊喜，意不意外");
            user.setAge(1);
        }
        return user;
    }

}
