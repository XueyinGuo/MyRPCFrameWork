package com.szu.client;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 13:19
 */

import com.szu.client.proxy.CreateProxy;
import com.szu.common.service.TimeService;
import com.szu.client.transport.ConnectionPool;

import java.net.InetSocketAddress;

public class Client {
    /**
    * 启动线程而已，创建连接在 {@link ConnectionPool}
    * */
    public static void main(String[] args) throws InterruptedException {
        int size = 50;
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
            threads[i] = new Thread(()->{
                /*
                * 创建代理，进行RPC 或者 FC
                * 不管RPC 还是 FC，都进行代理
                * */
                TimeService service = CreateProxy.getProxy(TimeService.class,
                        new InetSocketAddress("localhost", 8090));
                String serviceArgs = "What time it is?";
                String dateTime = service.getDateTime();
                System.out.println(dateTime);
            });
        }

        for (int i = 0; i < size; i++) {
            threads[i].start();
            threads[i].join();
        }

    }

}
