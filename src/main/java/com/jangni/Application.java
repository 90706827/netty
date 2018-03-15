package com.jangni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description:
 * @Autor: Jangni
 * @Date: Created in  2018/3/15/015 22:26
 */
@SpringBootApplication( scanBasePackages = "com.jangni")
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
