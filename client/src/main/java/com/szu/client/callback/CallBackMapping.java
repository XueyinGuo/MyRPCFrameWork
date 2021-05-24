package com.szu.client.callback;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 0:00
 */

import com.szu.common.protocol.SZUMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CallBackMapping {

    static ConcurrentHashMap<Long, CompletableFuture> callBackMap;

    static {
        callBackMap = new ConcurrentHashMap<>();
    }

    /*
    * 给 CompletableFuture 中的 result赋值
    * 并触发回调函数
    * */
    public static void fireGetResponse(SZUMessage response){
        long id = response.getHead().getId();
        CompletableFuture future = callBackMap.get(id);
        future.complete(response.getContent().getResult());
        future.thenRun(new CallBackAfterGetResponse(id)); /* 执行回调 */
        removeCallBack(id);
    }

    private static void removeCallBack(long id) {
        callBackMap.remove(id);
    }

    public static void addCallBack(long id, CompletableFuture future) {
        callBackMap.put(id, future);
    }
}
