package com.szu.common.handler;/*
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

public class DecodeHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inBuf, List<Object> out) throws Exception {
        while (inBuf.readableBytes() >= Config.HEAD_SIZE){
            byte[] headBytes = new byte[Config.HEAD_SIZE];
            inBuf.getBytes(inBuf.readerIndex(), headBytes);
            ByteArrayInputStream headByteArrInStream = new ByteArrayInputStream(headBytes);
            ObjectInputStream headObjectInputStream = new ObjectInputStream(headByteArrInStream);
            Head head = (Head) headObjectInputStream.readObject();
            if (inBuf.readableBytes() >= head.getDataLength()){

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

