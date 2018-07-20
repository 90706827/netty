package com.jangni.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

/**
 * Author ZhangGuoQiang
 * Date: 2018/7/4/004
 * Time: 19:29
 * Description:
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = LoggerFactory.getLogger("server handler");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String reqMsg = msg.content().toString(StandardCharsets.UTF_8);

    }

    private void write(ChannelHandlerContext ctx,FullHttpRequest request,String resp){
        logger.info("服务响应报文："+resp);
        ByteBuf buf = Unpooled.copiedBuffer(resp,StandardCharsets.UTF_8);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK,buf);
        HttpServletRequest httpReq = (HttpServletRequest)request;
        String contextType = httpReq.getContentType()+";charset=utf-8";
        response.headers().add(HttpHeaderNames.CONTENT_TYPE,contextType);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());

        if(HttpUtil.isKeepAlive(request)){
            response.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        }else{
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
