package com.szu.server.handler;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 23:57
 */

import com.szu.common.dispacher.Dispatcher;
import com.szu.common.protocol.SZUMessage;
import com.szu.server.executor.ServerThreadPool;
import com.szu.server.executor.Task;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerRequestHandler extends ChannelInboundHandlerAdapter {

    Dispatcher dispatcher;

    public ServerRequestHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SZUMessage request = (SZUMessage) msg;
        ServerThreadPool.getThreadPoolExecutor().execute(new Task(request, ctx));
    }
}
