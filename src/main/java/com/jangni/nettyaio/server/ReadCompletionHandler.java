package com.jangni.nettyaio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/20/020 22:32
 */
public class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer> {

    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel){
        if(this.channel == null){
            channel = channel;
        }
    }

    @Override
    public void completed(Integer integer, ByteBuffer attachment) {

        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try{
            String req = new String(body,"UTF-8");
            System.out.println("The time server receive order:"+req);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req)?new java.util.Date(System.nanoTime()).toString():"BAD ORDER";
            doWriter(currentTime);

        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }
    private void doWriter(String currentTime){
        if(currentTime != null && currentTime.trim().length() > 0){
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
            writerBuffer.put(bytes);
            writerBuffer.flip();
            channel.write(writerBuffer, writerBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer integer, ByteBuffer byteBuffer) {
                    if(byteBuffer.hasRemaining()){
                        channel.write(byteBuffer,byteBuffer,this);
                    }
                }

                @Override
                public void failed(Throwable throwable, ByteBuffer byteBuffer) {
                    try{
                        channel.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void failed(Throwable throwable, ByteBuffer byteBuffer) {
        try{
            this.channel.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
