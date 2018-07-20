package com.jangni.netty

import java.net.InetSocketAddress
import java.nio.ByteOrder

import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.bytes.{ByteArrayDecoder, ByteArrayEncoder}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import org.slf4j.{Logger, LoggerFactory}


/**
  * Author ZhangGuoQiang
  * Date: 2018/7/20/020
  * Time: 19:01
  * Description:
  */
object SocketClient{
  private val logger:Logger= LoggerFactory.getLogger("SocketClient")
  val socketAddress = new InetSocketAddress("127.0.0.1",37020)
  protected val workerGroup = new NioEventLoopGroup(1)
  var channel:Option[Channel] = None

  def main(args:Array[String]):Unit = {

    connect
    val data = "abcd".getBytes()
    val f = Future{
      channel.foreach(c=>{
        c.writeAndFlush(data)
      })
    }

    f.onComplete{
      case _ => println("send ok")
    }
  }

  private def connect:Unit = {
    val result = Try {
      val bootstrap = new Bootstrap
      bootstrap.group(workerGroup)
        .channel(classOf[NioSocketChannel])
        .option(ChannelOption.SO_KEEPALIVE,Boolean.box(true))
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(c: SocketChannel): Unit = {
            val pipeline = c.pipeline()
            pipeline.addLast(new LoggingHandler("client",LogLevel.DEBUG))
              .addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN,4096,0,4,0,4,true))
              .addLast(new ByteArrayDecoder)
              .addLast(new LengthFieldPrepender(ByteOrder.BIG_ENDIAN,4,0,false))
              .addLast(new ByteArrayEncoder)
              .addLast(new SimpleChannelInboundHandler[Array[Byte]]() {
                override def channelRead0(chx: ChannelHandlerContext, msg: Array[Byte]): Unit = {
                  logger.info(msg.toString)
                }
              })
          }
        })
      bootstrap.connect(socketAddress).sync().channel()
    }
    result.failed.foreach(t=> logger.error(s"connect to $socketAddress failed...",t))
    result.foreach(c=> channel = Option(c))
  }

}
