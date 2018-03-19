package com.jangni.netty1.client;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/19/019 23:11
 */
public class TimeClient {
    public static void main(String[] args){
        String host = "127.0.0.1";
        int port = 8080;

        new Thread(new TimeClientHandle(host,port),"TimeClient-001").start();
    }
}
