package com.szu.common.handler;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 12:36
 */

import com.szu.common.enmu.CodeEnum;
import com.szu.common.protocol.Config;
import com.szu.common.protocol.Content;
import com.szu.common.protocol.Head;
import com.szu.common.protocol.SZUMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/*
* 解码器
* 客户端和服务器都需要有的一个 handler
*
* 而且都必须是在 pipeline 中的第一个 handler
* */
public class DecodeHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inBuf, List<Object> out) throws Exception {
        /*
        * TODO headSize 暂时是写死的
        * */
        while (inBuf.readableBytes() >= Config.HEAD_SIZE){
            byte[] headBytes = new byte[Config.HEAD_SIZE];
            /* getBytes 不会移动指针 */
            inBuf.getBytes(inBuf.readerIndex(), headBytes);
            ByteArrayInputStream headByteArrInStream = new ByteArrayInputStream(headBytes);
            ObjectInputStream headObjectInputStream = new ObjectInputStream(headByteArrInStream);
            Head head = (Head) headObjectInputStream.readObject();
            if (inBuf.readableBytes() >= head.getDataLength()){
                /* 如果后边的数据包大小也够一个 body，才移动指针 */
                /*
                * ByteToMessageDecoder 中有 帮我们拼接 剩余这部分没用的字节数组 到下次接收额字节数组中
                * 虽然 TCP 会截断数据包，但是两次传输 肯定会保证 被截断的数据包的完整性
                * */
                inBuf.readBytes(Config.HEAD_SIZE); // 移动指针到 body 开始的位置
                byte[] contentBytes = new byte[head.getDataLength()];
                inBuf.readBytes(contentBytes);
                ByteArrayInputStream contentByteArrInStream = new ByteArrayInputStream(contentBytes);
                ObjectInputStream contentObjectArrInStream = new ObjectInputStream(contentByteArrInStream);
                Content content = (Content) contentObjectArrInStream.readObject();

                if (head.getCode() == CodeEnum.REQ){
                    out.add(new SZUMessage(head, content));
                }else if (head.getCode() == CodeEnum.REP){
                    out.add(new SZUMessage(head, content));
                }else
                    break;
            }
        }
    }
}

