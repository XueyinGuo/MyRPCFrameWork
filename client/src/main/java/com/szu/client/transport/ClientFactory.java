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

/*
* 连接工厂
*
* ConcurrentHashMap 中存放着 对应 IP:port 的连接池
* K : P:port  ----->>>  V : 对这个地址的连接池
* 每个连接池可以对一个 地址 有多连接
*
* 而且通过简单的随机数，勉强做到了连接的负载均衡
* */
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
        /*
        * 从连接工厂得到一个连接之后，直接通过这个连接 发送请求出去
        * */
        NioSocketChannel channel = clientFactory.getClient(address);
        channel.writeAndFlush(message);
    }

    /*
    * 获取对应地址的 连接池
    * 并在连接池中取得一条连接
    * */
    public NioSocketChannel getClient(InetSocketAddress address) {
        ConnectionPool pool = postman.get(address);
        if (pool == null){
            pool = new ConnectionPool(poolSize);
            postman.put(address, pool);
        }
        // TODO 负载均衡
        NioSocketChannel socketChannel = pool.getSocketChannelPool()[random.nextInt(poolSize)];
        if (socketChannel == null || !socketChannel.isActive()){
            /* 如果连接为空，则创建连接 */
            socketChannel = ConnectionPool.createConnection(address);
        }
        return socketChannel;
    }


}
