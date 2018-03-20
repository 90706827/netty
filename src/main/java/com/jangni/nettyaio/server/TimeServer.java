package com.jangni.nettyaio.server;

import java.io.IOException;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/20/020 22:07
 */
public class TimeServer {

    public static void main(String[] args) throws IOException{

        AsyncTimeServerHandler timeServerHandler = new AsyncTimeServerHandler(8080);
        new Thread(timeServerHandler,"AIO-AsyncTimeServerHandler").start();
    }
}
