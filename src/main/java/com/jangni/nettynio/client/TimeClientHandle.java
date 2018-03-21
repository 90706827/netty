package com.jangni.nettynio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/19/019 23:12
 */
public class TimeClientHandle implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandle(String host,int port){
        this.host = host;
        this.port = port;
        try{
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);//设置异步非阻塞
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    @Override
    public void run() {

        try{
            doContent();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        while(!stop){
            try{
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    try{
                        handleInput(key);
                    }catch(Exception e){
                        if(key!=null){
                            key.channel();//返回为之创建此键的通道
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        if(selector !=null){
            try{
                selector.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }
    private void doContent()throws IOException{
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            socketChannel.register(selector,SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else{
            socketChannel.register(selector,SelectionKey.OP_CONNECT);//用于套接字连接操作的操作集位。
        }
    }

    private void handleInput(SelectionKey key)throws IOException{
        if(key.isValid()){ //是否有效
            SocketChannel sc = (SocketChannel)key.channel();
            if(key.isConnectable()){ //是否已完成其套接字连接操作
                if(sc.finishConnect()){ //是否连接就绪
                    sc.register(selector,SelectionKey.OP_READ);
                    doWrite(sc);
                }else{
                    System.out.println("连接失败退出。。。");
                    System.exit(1);
                }
            }
            if (key.isReadable()){//是否已准备好进行读取
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);//创建一个指定capacity的ByteBuffer
                int readBytes = sc.read(readBuffer);
                if(readBytes >0){
                    readBuffer.flip();//把limit设为当前position，把position设为0，一般在从Buffer读出数据前调用。
                    byte[] bytes = new byte[readBuffer.remaining()]; //方法返回limit - position
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("Now is:"+body);
                    this.stop = true;
                }else if(readBytes <0){
                    key.channel();
                    sc.close();
                }else{
                    ;
                }
            }
        }
    }
    private void doWrite(SocketChannel sc)throws IOException{
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();//把limit设为当前position，把position设为0，一般在从Buffer读出数据前调用。
        sc.write(writeBuffer);
        if(!writeBuffer.hasRemaining()){ //此方法返回position < limit
            System.out.println("Send order 2 server succeed.");
        }
    }
}
