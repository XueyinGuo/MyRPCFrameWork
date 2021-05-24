package com.szu.client.transport;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 23:17
 */


import com.szu.client.handler.ClientCallBackHandler;
import com.szu.common.handler.DecodeHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/*
* 客户端堆服务端的连接池
* */
public class ConnectionPool {

    NioSocketChannel[] socketChannelPool;


    public ConnectionPool( int poolSize) {
        this.socketChannelPool = new NioSocketChannel[poolSize];
    }

    /*
    * 创建连接就是简单的启动一个 NIOSocketChannel
    * 并在 channel 的 pipeline 中添加两个 handler
    * 第一个 handler 当然是解码器
    * 第二个是 handler 回调函数的触发器 ：获得服务的结果之后所要触发哪个回调函数，每个 request都有自己的 ID
    *                                在发出一个请求的同时，自己注册一个回调函数到 CallBackAfterGetResponse 中
    *                                在接收到服务端的回应之后，就会调用这个 回调函数
    * */
    public static NioSocketChannel createConnection(InetSocketAddress address) {
        NioEventLoopGroup clientSlave = new NioEventLoopGroup(1);
        ChannelFuture connect = new Bootstrap().group(clientSlave)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new DecodeHandler());
                        pipeline.addLast(new ClientCallBackHandler());
                    }
                }).connect(address);
        NioSocketChannel clientChannel = null;
        try {
            clientChannel = (NioSocketChannel) connect.sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientChannel;
    }

    public NioSocketChannel[] getSocketChannelPool() {
        return socketChannelPool;
    }

}
