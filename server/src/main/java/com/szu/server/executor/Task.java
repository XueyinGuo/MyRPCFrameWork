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
        this.context = context;
    }

    /*
    * 在自定义线程池中执行任务
    * */
    @Override
    public void run() {
        Class<?> clazz = szuMessage.getContent().getClazz();
        String methodName = szuMessage.getContent().getMethodName();
        /*
        * 从服务器刚刚启动的时候注册所有服务的 分发器中 找到对象的服务实现类
        *
        * 并通过反射调用对应的方法
        * 【所有类型信息，调用方法名称、参数类型、参数具体值 都在 发送的消息的 content 中】
        * TODO 反射必定效率巨低
        * */
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
        /*
        * 调用完服务端的方法之后，新创建一个消息头和消息体
        * 序列化之后发回客户端，但是需要把之前头中的 id 设置到这个返回的 消息头中
        * 从而实现有状态通信
        * */
        Content content = new Content(result);
        byte[] byteBody = Serialize.serialize(content);
        Head head = new Head(CodeEnum.REP, byteBody.length);
        /*
        * 服务器和客户端共同实现一个有状态的协议，从而使连接可以复用
        * 不像无状态协议的穿行复用，而是直接可以异步的复用
        * */
        head.setId(szuMessage.getHead().getId());
        byte[] byteHead = Serialize.serialize(head);
        /*
        * 使用直接内存，减少一次数据包的拷贝【零拷贝】
        * */
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(byteHead.length + byteBody.length);
        byteBuf.writeBytes(byteHead);
        byteBuf.writeBytes(byteBody);
        context.writeAndFlush(byteBuf);
    }
}
