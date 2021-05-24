package com.szu.common.dispacher;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:21
 */

import java.util.concurrent.ConcurrentHashMap;

/*
 * 服务启动的时候，会把自己这个服务中所有的 service 实现类注册到此处
 * 从而让接受到的请求中解析到的 需要调用的对象可以从 此处直接获取服务对象
 *
 * ==========================================================
 * ==========================================================
 * 需要注意的是：服务器和客户端都可以注册自己服务对象
 *              如果客户端调用的服务直接就在自己的 invokeMap 中
 *              那么直接走 本地方法调用就好 FC
 *
 *            本地没有的时候才去走 RPC
 * ==========================================================
 * ==========================================================
 * */
public class Dispatcher {

    private final static Dispatcher dispatcher;
    public static ConcurrentHashMap<Class, Object> invokeMap;

    private Dispatcher() {
    }

    static {
        dispatcher = new Dispatcher();
        invokeMap = new ConcurrentHashMap<>();
    }

    public static Dispatcher getDispatcher(){
        return dispatcher;
    }

    public void register(Class<?> clazz, Object object){
        if (invokeMap.get(clazz) == null){
            invokeMap.put(clazz, object);
        }
    }

    public Object getInvokeObject(Class clazz){
        return invokeMap.get(clazz);
    }

}
