package com.szu.server.handler;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 23:57
 */

import com.szu.common.protocol.SZUMessage;
import com.szu.server.executor.ServerThreadPool;
import com.szu.server.executor.Task;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerRequestHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        /*
        * 经过上一个 Decode Handler 解码之后，请求头和体被包装成 SZUMessage 对象
        * */
        SZUMessage request = (SZUMessage) msg;
        /*
        * 然后把解码之后的对象，交给线程池执行
        * IO线程与业务线程分离！！！！！！！
        *
        * 考虑到并发量巨高的情况，如果 IO 线程设计业务的处理，那么必定除了读取数据包IO之外还要涉及计算
        * 那么后续的 IO 必定会被阻塞。
        * 如果并发量巨高，人家操作系统已经帮我们实现了 CPU 关中断疯狂把网卡中的数据包搬入操作系统内核
        *
        * 如果我们的 IO 线程从内核复制数据包到进程的工作空间还要进行计算，会让数据积压在内核buffer
        * 当操作系统的这个 BUFFER 满了之后，就会导致后续的请求发不进来了
        *
        * 所以我让这个 IO 线程只是负责从内核搬运数据到 进程空间，计算任务交给另外的业务线程池
        *
        * 另外服务端和客户端在创建数据包的时候都是用直接内存，减少一次数据包的拷贝【零拷贝】
        * 我认为的数据拷贝过程是：
        *   1. 接受端： 网卡 -> 内核 -> JVM进程空间 -> Java虚拟机的堆空间
        *   2. 发送端： Java虚拟机的对空间 -> JVM进程空间 -> 内核 -> 网卡
        *
        * 其中内核部分应该是这样的：
        *   网卡接受的数据包中都有对端的socket，我所猜测红黑树的排序规则是根据socket排序
        *   这样就能在 logN 的复杂度内找到 对应 socket 的 buffer 在哪，从而直接拷贝网卡中的数据到 buffer
        *
        * 这样使用直接内存（JVM进程堆的内存，而不是JAVA虚拟机的堆内存，JAVA虚拟机堆可以认为是 JVM进程的堆上堆）减少一次内存拷贝
        * */
        ServerThreadPool.getThreadPoolExecutor().execute(new Task(request, ctx));
    }
}
