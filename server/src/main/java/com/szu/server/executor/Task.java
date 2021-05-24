package com.szu.server.executor;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 13:03
 */

import com.szu.common.dispacher.Dispatcher;
import com.szu.common.enmu.CodeEnum;
import com.szu.common.protocol.Content;
import com.szu.common.protocol.Head;
import com.szu.common.protocol.SZUMessage;
import com.szu.common.util.Serialize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

public class Task implements Runnable {

    SZUMessage szuMessage;
    ChannelHandlerContext context;


    public Task(SZUMessage szuMessage, ChannelHandlerContext context) {
        this.szuMessage = szuMessage;
    }

    @Override
    public void run() {
        Class<? extends Content> clazz = szuMessage.getContent().getClass();
        String methodName = szuMessage.getContent().getMethodName();
        Object invokeObject = Dispatcher.getDispatcher().getInvokeObject(clazz);
        Object result = null;
        try {
            Method method = invokeObject.getClass().getMethod(methodName, szuMessage.getContent().getParamTypes());
            result = method.invoke(invokeObject, szuMessage.getContent().getArgs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null)
            throw new RuntimeException("Error! result is null !");
        Content content = new Content(result);
        byte[] byteBody = Serialize.serialize(content);
        Head head = new Head(CodeEnum.REP, byteBody.length);
        byte[] byteHead = Serialize.serialize(head);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(byteHead.length + byteBody.length);
        byteBuf.writeBytes(byteHead);
        byteBuf.writeBytes(byteBody);
        context.writeAndFlush(byteBuf);
    }
}
