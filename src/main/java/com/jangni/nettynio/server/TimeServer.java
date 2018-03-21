package com.jangni.nettynio.server;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/19/019 21:33
 */
public class TimeServer {
    public static void main(String[] args){
        int port = 8080;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer,"Nio-TimeServer-001").start();
    }
}
