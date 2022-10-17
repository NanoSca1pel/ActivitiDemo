package com.lht.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: 将bpmn的xml文件放在resources/processes文件夹下，可以自动deploy
 * @author: lhtao
 * @date: 2022年10月14日 16:53
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
