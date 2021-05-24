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
    /*
     * 启动服务端
     * */
    public static void main(String[] args) throws InterruptedException {
        /* 现在调用分发器中注册 timeService接口 是由 哪一个 具体实现类提供服务 */
        TimeService timeService = new TimeImpl();
        Dispatcher dispatcher = Dispatcher.getDispatcher();
        dispatcher.register(TimeService.class, timeService);
        /*
        * 启动服务器监听
        *
        * 并在注册需要添加在 NioSocketChannel 中的两个 handler
        * */
        NioEventLoopGroup boss = new NioEventLoopGroup(20);
        NioEventLoopGroup worker = boss;
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture bind = serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new DecodeHandler()); /* 负责解码客户端发送的数据 */
                        /* 负责处理上一个 handler 解码的请求 */
                        pipeline.addLast(new ServerRequestHandler());
                    }
                }).bind(new InetSocketAddress("localhost", 8090));
        /* 等待绑定成功之后，继续等待服务器关闭 */
        bind.sync().channel().closeFuture().sync();
    }
}
