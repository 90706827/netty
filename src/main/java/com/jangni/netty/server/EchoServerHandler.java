package com.jangni.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description: //特殊符号解决粘包/拆包
 * @Autor: Jangni
 * @Date: Created in  2018/3/24/024 16:52
 */
public class EchoServerHandler  extends ChannelHandlerAdapter {

    private int count = 1;
    /**
     *  读取客户端发送的请求
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req,"UTF-8");
        String body = (String)msg;
        System.out.println("Server info:"+body);
        ByteBuf resp = Unpooled.copiedBuffer(("收到第"+ count++ +"次请求并回复：兄弟坚持住，即刻发兵麦城支援"+"$_").getBytes());
        ctx.writeAndFlush(resp);
    }

    /**
     * 发生异常 打印日志 释放资源
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
