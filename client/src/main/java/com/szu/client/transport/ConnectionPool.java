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

public class ConnectionPool {

    NioSocketChannel[] socketChannelPool;


    public ConnectionPool( int poolSize) {
        this.socketChannelPool = new NioSocketChannel[poolSize];
    }

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
