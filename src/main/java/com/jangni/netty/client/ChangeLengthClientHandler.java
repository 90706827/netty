package com.jangni.netty.client;

import com.jangni.entity.News;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description: 客户端 自定义编解码 可变长度解决粘包/拆包
 * @Autor: Jangni
 * @Date: Created in  2018/3/24/024 19:55
 */
public class ChangeLengthClientHandler extends ChannelHandlerAdapter {

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
                    "大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!大哥，小弟被困麦城，请求支援!").getBytes();
            ByteBuf writerBuf = Unpooled.copiedBuffer(req);
//            writerBuf.writeBytes(writerBuf);
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

    /**
     * 创建测试使用数据
     * @return UserInfo
     */
    private News[] newsInfo(){
        News[] newss = new News[2];
        News news = new News("麦城危及","大哥，麦城危及速速派兵支援！","关羽","公元223年元月二十");
        newss[1] = news;
        news =  new News("麦城危及","三弟，麦城危及速速派兵支援！","关羽","公元223年元月二十");
        newss[2] = news;
        return newss;
    }

}
