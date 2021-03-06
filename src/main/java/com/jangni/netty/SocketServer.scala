package com.jangni.netty

import java.nio.ByteOrder

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.handler.codec.bytes.{ByteArrayDecoder, ByteArrayEncoder}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.handler.timeout.ReadTimeoutHandler

/**
  * Author ZhangGuoQiang
  * Date: 2018/7/20/020
  * Time: 18:34
  * Description:
  */
object SocketServer extends App{

  private val port:Int = 37020
  private val timeout:Int = 60

  val bossGroup = new NioEventLoopGroup(1)
  val workGroup = new NioEventLoopGroup()

  val bootstrap = new ServerBootstrap

  bootstrap.group(bossGroup,workGroup)
    .channel(classOf[NioServerSocketChannel])
      .option(ChannelOption.SO_BACKLOG,Int.box(4096))
    .childOption(ChannelOption.SO_KEEPALIVE,Boolean.box(true))
    .childHandler(new ChannelInitializer[SocketChannel] {
      override def initChannel(c: SocketChannel): Unit = {
        c.pipeline()
          .addLast(new LoggingHandler("server",LogLevel.DEBUG))
          .addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN,4096,0,4,0,4,true))
          .addLast(new LengthFieldPrepender(ByteOrder.BIG_ENDIAN,4,0,false))
          .addLast(new ByteArrayDecoder)
          .addLast(new ByteArrayEncoder)
          .addLast(new ReadTimeoutHandler(timeout))
          .addLast(new ServerHandler)

      }
    })

  bootstrap.bind(port).sync()

}
