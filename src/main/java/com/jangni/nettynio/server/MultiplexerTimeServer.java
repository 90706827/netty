package com.jangni.nettynio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/19/019 21:34
 */
public class MultiplexerTimeServer implements Runnable{
    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port){
        try{
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);//设置异步非阻塞
            servChannel.socket().bind(new InetSocketAddress(port),1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);//用于套接字接受操作的操作集位
            System.out.println("The time server is start in port:" + port);
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop = true;
    }
    @Override
    public void run() {
        while(!stop){
            try{
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while(iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    try{
                        handleInput(key);

                    }catch(Exception e){
                        if(key != null){
                            key.channel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch (Throwable t){
                t.printStackTrace();
            }
        }
            if (selector != null){
                try{
                    selector.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
    }
    private void handleInput(SelectionKey key)throws IOException {
        if(key.isValid()){//此键是否有效
            if(key.isAcceptable()){//是否已准备好接受新的套接字连接
                ServerSocketChannel ssc = (ServerSocketChannel)key.channel();//返回为之创建此键的通道
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector,SelectionKey.OP_READ);//用于读取操作的操作集位

            }
            if(key.isReadable()){//是否已准备好进行读取
                SocketChannel sc = (SocketChannel)key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("The time server receive order:"+body);
                    String currentTiem = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.nanoTime()).toString():"BAO ORDER";
                    doWriter(sc,currentTiem);
                }else if(readBytes < 0){
                    key.channel();
                    sc.close();
                }else
                    ;
            }
        }
    }
    private void doWriter(SocketChannel channel,String response)throws IOException{
        if(response !=null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
            writerBuffer.put(bytes);
            writerBuffer.flip();
            channel.write(writerBuffer);
        }
    }
}
