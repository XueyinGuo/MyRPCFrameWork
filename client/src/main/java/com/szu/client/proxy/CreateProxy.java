package com.szu.client.proxy;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:18
 */

import com.szu.common.dispacher.Dispatcher;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

public class CreateProxy {

    static Dispatcher dispatcher = Dispatcher.getDispatcher();

    /*
    * 创建动态代理
    * */
    public static <T> T getProxy(Class<T> clazz, InetSocketAddress address) {
        ClassLoader loader = clazz.getClassLoader();
        ObjectInvocationHandler handler = new ObjectInvocationHandler(clazz, address);
        return (T)Proxy.newProxyInstance(loader, new Class[]{clazz}, handler);
    }

}
