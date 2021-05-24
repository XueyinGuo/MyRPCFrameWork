package com.szu.server.executor;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 12:53
 */

import com.szu.common.task.RejectedTasks;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerRejectPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        /*
        * 把拒绝的请求方法另外的 List 中
        * TODO 试过放到 MQ 中，另外的微服务监听 MQ 队列，但是 runnable 序列化之后是没东西的，
        *  目测暂时只能在线程池中把 SZUMessage对象放到 MQ，但是调用过大的时候又该如何？
        * */
        RejectedTasks.rejectTasks.add(r);
    }
}
