package com.szu.server.executor;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 12:47
 */

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerThreadPool {
    /*
    * 自定义线程池
    * 并且使用自定义的线程工厂
    * */
    public static int coreSize;
    public static int maxSize;
    public static int freeTime;
    public static TimeUnit timeUnit;
    public static final ThreadPoolExecutor threadPoolExecutor;

    static {
        coreSize = 5;
        maxSize = 5;
        freeTime = 10;
        timeUnit = TimeUnit.MINUTES;
        threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxSize, freeTime, timeUnit,
                new LinkedBlockingQueue<>(),
                ServerThreadFactory.getServerThreadFactory(), /* 自定义线程池 */
                new ServerRejectPolicy());                  /* 自定义拒绝策略 */

    }

    public static ThreadPoolExecutor getThreadPoolExecutor(){
        return threadPoolExecutor;
    }

}
