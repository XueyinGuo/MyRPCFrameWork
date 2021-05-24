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
        /*
        * 解码之后的 msg 已经被解码器封装成了 SZUMessage 对象
        * */
        SZUMessage response = (SZUMessage) msg;
        /* 从 callBackMap 中找到对应 ID 的 CompletableFuture，并把结果 response 中的结果给到 CompletableFuture  */
        CallBackMapping.fireGetResponse(response);
    }
}
