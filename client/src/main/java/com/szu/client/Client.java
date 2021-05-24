package com.szu.client;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 13:19
 */

import com.szu.client.proxy.CreateProxy;
import com.szu.common.dispacher.Dispatcher;
import com.szu.common.service.TimeService;

import java.net.InetSocketAddress;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        int size = 50;
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
            threads[i] = new Thread(()->{

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
