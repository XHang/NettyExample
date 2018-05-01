package netty;

import com.netty.handler.TimeServerHandler;
import com.netty.uitl.ServerThread;

/**
 * 开启一个时间服务器
 */
public class TimeServer {

    public static void main (String[] args){
        int port = 8080;
        new ServerThread(port,new TimeServerHandler()).run();
    }
}
