package com.jangni.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description: 客户端 换行回车解决粘包/拆包
 * @Autor: Jangni
 * @Date: Created in  2018/3/24/024 13:01
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

    /**
     * 发送请求
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] req = null;
        for(int i = 1; i<=10; i++){
            req = ("第"+i+"次通讯：大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!" +
                    "大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!" +
                    "大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!" +
                    "大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!" +
                    System.getProperty("line.separator")).getBytes();
            ByteBuf writerBuf = Unpooled.buffer(req.length);
            writerBuf.writeBytes(req);
            ctx.writeAndFlush(writerBuf);
        }
    }

    /**
     * 接收请求
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf)msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req,"UTF-8");
        String body = (String)msg;
        System.out.println("Client info:"+body);
    }

    /**
     * 发送异常 打印日志 释放客户端资源
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
