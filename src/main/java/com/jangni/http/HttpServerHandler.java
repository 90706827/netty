package com.jangni.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

/**
 * Author ZhangGuoQiang
 * Date: 2018/7/4/004
 * Time: 19:29
 * Description:
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String reqMsg = msg.content().toString(StandardCharsets.UTF_8);

    }
}
