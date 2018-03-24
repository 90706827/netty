package com.jangni.netty.client;

import com.jangni.help.msg.MsgpackDecoder;
import com.jangni.help.msg.MsgpackEncoder;
import com.jangni.netty.server.ChangeLengthServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/24/024 12:55
 */
public class TimeClient {

    public static void main(String[] args)throws Exception{
        String host = "127.0.0.1";
        int port = 8080;
        new TimeClient().connect(host,port);
    }

    public void connect(String host,int port)throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap boot = new Bootstrap();
            boot.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)throws Exception{
                            ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline()
                                    .addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65536,0,2,0,2)) //可变长度解决粘包/拆包
                                    .addLast("msgpack decoder",new MsgpackDecoder())//可变长度使用自定义编解码
                                    .addLast("frameEncoder", new LengthFieldPrepender(2)) //可变长度解决粘包/拆包
                                    .addLast("msgpack encoder",new MsgpackEncoder())//可变长度使用自定义编解码
//                                    .addLast(new FixedLengthFrameDecoder(10))//固定长度解决粘包/拆包
//                                    .addLast(new LineBasedFrameDecoder(1024))//换行回车解决粘包/拆包
//                                    .addLast(new DelimiterBasedFrameDecoder(1024,buf))//特殊符号解决粘包/拆包
//                                    .addLast(new StringDecoder()) // 自定义不适用此解码
                                    .addLast(new ChangeLengthServerHandler());//可变长度使用自定义编解码
//                                    .addLast(new FixedLengthClientHandler());//固定长度解决粘包/拆包
//                                    .addLast(new TimeClientHandler());//换行回车解决粘包/拆包
//                                    .addLast(new EchoClientHandler());//特殊符号解决粘包/拆包
                        }
                    });

            //发起异步连接
            ChannelFuture future = boot.connect(host,port).sync();

            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }
}
