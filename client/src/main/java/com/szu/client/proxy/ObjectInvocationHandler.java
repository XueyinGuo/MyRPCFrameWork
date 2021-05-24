package com.szu.client.proxy;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:32
 */

import com.szu.client.transport.ClientFactory;
import com.szu.client.callback.CallBackAfterGetResponse;
import com.szu.client.callback.CallBackMapping;
import com.szu.common.dispacher.Dispatcher;
import com.szu.common.enmu.CodeEnum;
import com.szu.common.protocol.Content;
import com.szu.common.protocol.Head;
import com.szu.common.util.Serialize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class ObjectInvocationHandler implements InvocationHandler {

    Class<?> clazz;
    InetSocketAddress address;

    public <T> ObjectInvocationHandler(Class<?> clazz, InetSocketAddress address) {
        this.clazz = clazz;
        this.address = address;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Dispatcher.getDispatcher().getInvokeObject(clazz) != null) {
            Method localMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
            return localMethod.invoke(clazz, args);
        }
        String className = clazz.getName();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Content content = new Content(className, methodName, parameterTypes, args);
        byte[] byteBody = Serialize.serialize(content);
        Head head = new Head(CodeEnum.REQ, byteBody == null ? 0 : byteBody.length);
        byte[] byteHead = Serialize.serialize(head);
        ByteBuf message = PooledByteBufAllocator.DEFAULT.directBuffer(byteHead.length + byteBody.length);
        message.writeBytes(byteHead);
        message.writeBytes(byteBody);
        CompletableFuture<Object> futureResult = new CompletableFuture<>();
        futureResult.thenRun(new CallBackAfterGetResponse(head.getId()));
        CallBackMapping.addCallBack(head.getId(), futureResult);
        //TODO 这里是rpc底层的流程   牵扯到 1，注册发现 ；2，provider的负载均衡；3，IO的负载均衡；
        /**
         * serviceA:ipA:port
         *              socket1
         *              socket2
         * serviceA:ipB:port
         * serviceA:ipC:port
         */
        ClientFactory.transport(message, address);
        return futureResult.get();
    }
}
