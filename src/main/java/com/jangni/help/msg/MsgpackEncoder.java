package com.jangni.help.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @Description: 编码器
 * @Autor: Jangni
 * @Date: Created in  2018/3/24/024 21:43
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        MessagePack msgpack = new MessagePack();
        byte[] raw = msgpack.write(msg);
        out.writeBytes(raw);
    }
}
