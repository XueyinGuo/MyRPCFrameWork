package com.szu.common.task;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 12:57
 */

import java.util.concurrent.LinkedBlockingQueue;

public class RejectedTasks {

    public static final LinkedBlockingQueue<Runnable> rejectTasks;

    static {
        rejectTasks = new LinkedBlockingQueue<>();
    }

}
