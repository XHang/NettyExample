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

   当channelRead第一次执行时读取不足4个字节，下次发送端传来数据就可以接着填充直至4个字段填满。

   ​