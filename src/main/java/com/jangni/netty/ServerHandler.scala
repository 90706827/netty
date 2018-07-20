package com.jangni.netty

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.TooLongFrameException
import io.netty.handler.timeout.ReadTimeoutException
import org.slf4j.{Logger, LoggerFactory}

/**
  * Author ZhangGuoQiang
  * Date: 2018/7/20/020
  * Time: 18:49
  * Description:
  */
class ServerHandler extends ChannelInboundHandlerAdapter{

  private val logger:Logger = LoggerFactory.getLogger("ServerHandler")

  override def exceptionCaught(channelHandlerContext: ChannelHandlerContext, throwable: Throwable): Unit = {
    throwable match {
      case _:TooLongFrameException =>
        channelHandlerContext.channel().close()
      case _:ReadTimeoutException =>
        channelHandlerContext.channel().close()
      case _ =>
        channelHandlerContext.channel().close()
    }
  }

  override def channelRead(channelHandlerContext: ChannelHandlerContext, o: scala.Any): Unit = {

    val reqMsg = o.asInstanceOf[Array[Byte]]
    logger.info(new String(reqMsg,"utf-8"))
    val respMsg = "<hhap>ok</hhap>".getBytes
    channelHandlerContext.writeAndFlush(respMsg)
  }


}
