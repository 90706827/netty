package com.jangni.netty.server;

import com.jangni.help.msg.MsgpackDecoder;
import com.jangni.help.msg.MsgpackEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.websocket.Extension;
import java.net.Socket;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/22/022 22:16
 */
public class TimeServer {

    public static void main(String[] args) throws Exception{
        int port =8080;
        new TimeServer().bind(port);
    }

    public void bind(int port) throws Exception{

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap boot = new ServerBootstrap();
            //boss worker两个线程组 一个用户接收客户端的连接 一个进行网络读写
            boot.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel sc) {
                            try {
                                ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                                sc.pipeline()
                                        .addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2))//可变长度解决粘包/拆包
                                        .addLast("msgpack decoder",new MsgpackDecoder())//可变长度解决粘包/拆包
                                        .addLast("frameEncoder",new LengthFieldPrepender(2))//可变长度解决粘包/拆包
                                        .addLast("msgpacke encoder",new MsgpackEncoder())//可变长度解决粘包/拆包
//                                        .addLast(new FixedLengthFrameDecoder(10)) //固定长度解决粘包/拆包
//                                        .addLast(new DelimiterBasedFrameDecoder(1024,buf)) //特殊符号解决粘包/拆包
//                                        .addLast(new LineBasedFrameDecoder(1024)) //换行回车解决粘包/拆包
//                                        .addLast(new StringDecoder())//可变长度采用定义编解码
                                        .addLast(new ChangeLengthServerHandler());//可变长度解决粘包/拆包
//                                        .addLast(new FixedLengthClientHandler());//固定长度解决粘包/拆包
//                                        .addLast(new EchoServerHandler());//特殊符号解决粘包/拆包
//                                        .addLast(new TimeServerHandler());//换行回车解决粘包/拆包
                            }catch(Exception e1){
                                e1.printStackTrace();
                            }
                        }
                    });
            //绑定端口，同步等待成功
            ChannelFuture future = boot.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();

        }finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
