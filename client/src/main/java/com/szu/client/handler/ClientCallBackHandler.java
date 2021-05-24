package com.szu.client.handler;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 23:58
 */

import com.szu.common.protocol.SZUMessage;
import com.szu.client.callback.CallBackMapping;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientCallBackHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SZUMessage response = (SZUMessage) msg;
        CallBackMapping.runCallBack(response);
    }
}
