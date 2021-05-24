package com.szu.client.transport;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:40
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ClientFactory {

    static ClientFactory clientFactory;
    static ConcurrentHashMap<InetSocketAddress, ConnectionPool> postman;
    static int poolSize;
    static Random random;
    static {
        poolSize = 10;
        postman = new ConcurrentHashMap<>();
        random = new Random();
        clientFactory = new ClientFactory();
    }

    private ClientFactory() {
    }

    public static void transport(ByteBuf message, InetSocketAddress address) {


        NioSocketChannel channel = clientFactory.getClient(address);
        channel.writeAndFlush(message);

    }

    public NioSocketChannel getClient(InetSocketAddress address) {
        ConnectionPool pool = postman.get(address);
        if (pool == null){
            pool = new ConnectionPool(poolSize);
            postman.put(address, pool);
        }
        // TODO 负载均衡
        NioSocketChannel socketChannel = pool.getSocketChannelPool()[random.nextInt(poolSize)];
        if (socketChannel == null || !socketChannel.isActive()){
            socketChannel = ConnectionPool.createConnection(address);
        }
        return socketChannel;
    }


}
