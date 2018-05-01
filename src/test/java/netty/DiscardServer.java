package netty;

import com.netty.handler.DiscardServerHandler;
import com.netty.uitl.ServerThread;


/**
 * 丢弃服务端:丢弃使所有收到的数据
 */
public class DiscardServer {
    

    /**
     * 主方法，运行它，Get一个服务器
     * @param args
     * @throws Exception
     * Ps:运行该方法后，使用telnet localhost 8080 来向这个服务端发送信息
     */
    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new ServerThread(port,new DiscardServerHandler()).run();
    }
}