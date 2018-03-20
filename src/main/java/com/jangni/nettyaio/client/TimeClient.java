package com.jangni.nettyaio.client;

import com.jangni.nettyaio.server.AsyncTimeServerHandler;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/20/020 23:02
 */
public class TimeClient {

    public static void main(String[] args){
        new Thread(new AsyncTimeClientHandler("127.0.0.1",8080),"Aio-Client-001").start();
    }
}
