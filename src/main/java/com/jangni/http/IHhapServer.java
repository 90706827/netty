package com.jangni.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author ZhangGuoQiang
 * Date: 2018/6/28/028
 * Time: 20:35
 * Description:
 */
public class IHhapServer implements INetServer {
    private int port; //端口号
    private int idleTimeout; //连接空闲超时时间
    private int workThreadNum; //工作线程组
    Logger logger = LoggerFactory.getLogger("flow");
    private NioEventLoopGroup listenGroup = new NioEventLoopGroup(1);
    private NioEventLoopGroup ioGroup = new NioEventLoopGroup();
    private int msgMaxLength = 1024 * 3;

    @Override
    public void start() {
        try {
            new ServerBootstrap()
                    .group(listenGroup, ioGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler("server", LogLevel.DEBUG))
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(msgMaxLength))
                                    .addLast(new HttpServerExpectContinueHandler())
                                    .addLast(new ReadTimeoutHandler(idleTimeout))
                                    .addLast(new HttpServerHandler());
                        }
                    }).bind(port)
                    .sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("服务监听端口成功。。。");
    }

    @Override
    public void stop() {
        logger.info("服务准备停止...");
        listenGroup.shutdownGracefully();
        ioGroup.shutdownGracefully();
        logger.info("服务停止操作完成...");
    }
}
