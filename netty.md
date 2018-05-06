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

7. `ReferenceCountUtil.release(msg);`和`byteBuf.release();`起的作用其实都是一样。

   如果调用该方法时报`io.netty.util.IllegalReferenceCountException: refCnt: 0, increment: 1`

   代表引用计数为0，其实调用`release()`是没有任何用处的，所以这是一句警告。

   因为调用`release()`时其实就会将`byteBuf`的`refCnt`减1，既然已经是0了，再减一也没有用。

   出现这种现象的可能性是，你调用了不止一次的`release()`方法。

   我的教训是`super.channelRead(ctx, msg);`这个方法也会调用`release()`.

   所以你再往下调用`release()`就显得有点多余了。

8. 在传统的Tcp/ICP协议中，基于流的传输虽然可以保证数据的顺序性，因为传输的是字节流的序列。

   但是无法保证信息的批次性，比如说，你发了三段信息，接受方不一定按顺序把这三段信息分段接受。

   给个实际例子？

   you said :·`hello`   `I am`   `a`    `cxh`

   he receive:·`hello I `   `am  a `    `cxh`

   顺序都对不上了。

   原始代码是这么写的

   ```java
    try {
        //发送方传来的是32位无符号整数的数据
        long currentTimeMillis = (((ByteBuf) msg).readUnsignedInt() - 2208988800L) * 1000L;
        //String message  = BufferUitl.getContentOfNew((ByteBuf) msg);
        System.out.println("from Time Server receive msg :"+new Date(currentTimeMillis));
        ctx.close();
   } finally {
        ReferenceCountUtil.release(msg);
       }
       										--- from channelRead of TimeClientHandler
   ```

   ​

   解决办法？其实有的

   第一种解决办法：

   发送方按几个字节分为几个批次，你也按几个字节来一个一个接受。

   这样双方就得规定好每一段的字节数，而且程序不干净。

   代码实现

   第二种解决办法：

   ```
   ByteBuf m = (ByteBuf) msg;
   //将通道里面的数据写入缓冲区里面
   byteBuf.writeBytes(m); // (2)
   ctx.close();
   ReferenceCountUtil.release(msg);
   //如果缓冲区里面可读的字节有4个及以上
   if (byteBuf.readableBytes() >= 4) { // (3)
   long currentTimeMillis = (byteBuf.readUnsignedInt() - 2208988800L) * 1000L;
   System.out.println(new Date(currentTimeMillis));
   }
       										--- from channelRead of TimeClientHandler
   ```

   其中`byteBuf`在方法`handlerAdded`执行时创建，在`handlerRemoved`方法执行时释放。

   ```
   @Override
       public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
           byteBuf = ctx.alloc().buffer(4);
       }
   ```

   ```
    @Override
       public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
           byteBuf.release();
           byteBuf = null;
       }
   ```

   ​

   当channelRead第一次执行时读取不足4个字节，下次发送端传来数据就可以接着填充直至4个字段填满。

   第三种解决方案：

   其实呢，就是把分批次接受的处理和数据处理分成两部分。也就是两个`ChannelHandler`

   然后添加的客户端管道的末尾。

   对了，关于分批次接受的处理器,Netty提供了一个类方便你编写这个分批次处理的处理器`ByteToMessageDecoder `

   这个类实现了`ChannelInboundHandler` 可以很方便的处理这种碎片问题。





2018/5/4 晚上收获

还是完不成Telnet客户端和服务端通讯：

现在的情况是，客户端能接受服务端发来的欢迎信息，但是客户端发过去的信息服务端没有走到read方法那里。

应该是接受到了，因为客户端每发送一次信息，服务端都会执行`channelReadComplete`方法

该方法理应是执行完Read客户端发来的信息后，再执行它，不知为何，就是没执行。

今晚追踪了源码，发现的情况如下。

1. 通道只处理一个不知道什么鬼的处理器和`DelimiterBasedFrameDecoder`后，就没有执行下面的处理器了。

2. Netty注册的处理器，其实内部使用链表把他们存起来的。

3. 处理器是递归执行的，具体递归调用的方法是`AbstractChannelHandlerContext`和`invokeChannelRead`

   里面递归退出的条件需要再看下。

   以上

2018/5/6 新的知识如下

1：其实处理器并非真的递归执行吧，跟拦截器一样，都是责任链模式。

2：想看源码来知道自己为什么程序有问题的我，实在是太笨了，对于一些简单的源代码还可以，涉及到各种涉及模式和数据的结构源码，还是算了吧，这种源码只有在得知代码的作用下看才能看出端倪。

总之外事问谷歌，内事问Stack Overflow。

另：以上问题已经解决。

其原因是客户端发给服务端的信息是不带分行符号的，但是服务端有一个信息处理器`DelimiterBasedFrameDecoder`，当服务端受到的信息包含换行符号时，才会把之前所有受到的消息，送进Read方法里面。

之前服务端收到的信息都是不包含换行符的，所以呢？就永远被`DelimiterBasedFrameDecoder`拦截了。

不过即使如此，每次服务端收到来自客户端的消息时，都会执行`channelReadComplete`方法，以上。

另注下`DelimiterBasedFrameDecoder`类

它有一个构造器

`public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf delimiter)`

第一个参数是接受数据最大的帧数。如果该处理器接受的数据都超过这个帧数了，还找不到分割符号，就会抛异常。

第二个参数当然就是分割符了。

Netty提供了一些常用的分割符

`Delimiters.lineDelimiter()`其实就是`\r\n`

如果你想自己定义自己的分割符，可以

`ByteBuf delimiter = Unpooled.copiedBuffer("1".getBytes());`

以上。 



