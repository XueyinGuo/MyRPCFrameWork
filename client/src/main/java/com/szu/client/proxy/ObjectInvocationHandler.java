package com.szu.client.proxy;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:32
 */

import com.szu.client.transport.ClientFactory;
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
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class ObjectInvocationHandler implements InvocationHandler {
    static Random random = new Random();
    Class<?> clazz;
    InetSocketAddress address;

    public <T> ObjectInvocationHandler(Class<?> clazz, InetSocketAddress address) {
        this.clazz = clazz;
        this.address = address;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*
        * 如果本地注册了，直接进行 FC
        * */
        if (Dispatcher.getDispatcher().getInvokeObject(clazz) != null) {
            Method localMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
            return localMethod.invoke(clazz, args);
        }
        /*
        * 进行 RPC
        *
        * 创建 请求头和请求体
        * 在创建 this 实例的时候，构造方法已经给出了 想要调用的类型 和 服务器地址
        * 所以就可以根据这两个信息创建请求了
        * */
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Content content = new Content(clazz, methodName, parameterTypes, args);
        byte[] byteBody = Serialize.serialize(content);
        Head head = new Head(CodeEnum.REQ, byteBody.length);
        head.setId(Math.abs(random.nextLong()));
        byte[] byteHead = Serialize.serialize(head);
        ByteBuf message = PooledByteBufAllocator.DEFAULT.directBuffer(byteHead.length + byteBody.length);
        message.writeBytes(byteHead);
        message.writeBytes(byteBody);
        CompletableFuture<Object> futureResult = new CompletableFuture<>();
        /* 发送数据之前先注册自己的回调函数 */
        CallBackMapping.addCallBack(head.getId(), futureResult);
        /*
        * TODO  1，注册发现 ；2，service的负载均衡；3，IO的负载均衡；
        *  此处的获取一个连接暂时使用的是最基本的 随机！ 【姑且算是复用连接的负载均衡，其实我觉得每个连接都只是简简单单发送一个数据包而已
        *  用一致性哈希的思想来想的话，数量居多的发送的情况下，此时也能负载均衡】
        *
        * 从连接工厂得到一个连接，发送请求到服务端
        *  */
        ClientFactory.transport(message, address);
        /*
        * 当前线程在此阻塞，等待远程调用结果，
        * 当远程调用返回结果之后，触发回调 CallBackAfterGetResponse 的 map 中注册的回调
        * 【说的很高大上，其实就是去给】
        * */
        return futureResult.get();
    }
}
