package com.szu.common.dispacher;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:21
 */

import java.util.concurrent.ConcurrentHashMap;

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
