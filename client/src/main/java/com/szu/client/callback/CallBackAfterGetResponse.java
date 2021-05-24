package com.szu.client.callback;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 11:24
 */

public class CallBackAfterGetResponse implements Runnable {
    long id;
    public CallBackAfterGetResponse(long id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("RequestID: [" + id + "] got his response!");
    }
}
