package com.jangni.help.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @Description: 解码
 * @Autor: Jangni
 * @Date: Created in  2018/3/24/024 21:46
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte[] array;
        final int length = msg.readableBytes();
        array = new byte[length];
        msg.getBytes(msg.readerIndex(),array,0,length);
        MessagePack msgpack = new MessagePack();
        out.add(msgpack.read(array));
    }
}
