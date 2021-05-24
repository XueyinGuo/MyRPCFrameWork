package com.szu.server;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:09
 */

import com.szu.common.dispacher.Dispatcher;
import com.szu.common.handler.DecodeHandler;
import com.szu.server.handler.ServerRequestHandler;
import com.szu.common.service.TimeService;
import com.szu.server.service.impl.TimeImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        TimeService timeService = new TimeImpl();
        Dispatcher dispatcher = Dispatcher.getDispatcher();
        dispatcher.register(timeService.getClass(), timeService);

        NioEventLoopGroup boss = new NioEventLoopGroup(20);
        NioEventLoopGroup worker = boss;
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture bind = serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new DecodeHandler());
                        pipeline.addLast(new ServerRequestHandler(dispatcher));
                    }
                }).bind(new InetSocketAddress("localhost", 8090));
        bind.sync().channel().closeFuture().sync();
    }
}
