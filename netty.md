# netty 类库使用笔记
# Netty是一个什么类库
它是一个NIO工具类框架
# 相关类的介绍
1. EventLoopGroup 这是一个处理IO操作的多线程事件循环
        针对不同的数据类型传输，有不同的EventLoopGroup实现

   一般来说，你可以new 两个EventLoopGroup 。

   一个作为`Boos EventLoopGroup` ，接受传入的连接

   一个作为`Worker EventLoopGroup`，负责对传入连接的流量进行处理。

   怎么关联起来呢？

   使用这个代码`bootstrap.group(bossGroup, workerGroup)`

2. ChannelFuture 是一个通道，但这个通道表示尚未进行任何IO操作。  
  这意味着所有请求的操作都可能尚未执行。

  所以即使你创建出这个对象，并写入了一些数据，然后关闭该通道。

  这些数据就永远传不到对方那里去了。

  因为所以操作在Netty都是异步的，你以为写入数据就立即发送，想多了。

3. `Bootstarp`与`ServerBootstrap`相似，但前者专门为非服务器通道而建立。比如说客户端或者无连接通道。

4. `NioSocketChannel`和`NioServerSocketChannel`不同，前者适用于创建客户端通道，后者适用于创建服务端通道

5. 如果你只指定一个`EventLoopGroup`,即`bootstrap.group(group)`

   则这个EventLoopGroup既作为 boss group ,也作为worker group

   虽然客户端并没有`boss group` 和`worker group`

6. 服务端创建过程设置`bootstrap.childOption()`，但是客户端不用，因为客户端没有父节点