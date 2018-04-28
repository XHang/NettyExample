# netty 类库使用笔记
# Netty是一个什么类库
它是一个NIO工具类框架
# 相关类的介绍
1. EventLoopGroup 这是一个处理IO操作的多线程事件循环
        针对不同的数据类型传输，有不同的EventLoopGroup实现