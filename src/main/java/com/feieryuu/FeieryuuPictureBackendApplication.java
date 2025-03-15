package com.feieryuu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.feieryuu.picturebackend.mapper")
@EnableAsync
public class FeieryuuPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeieryuuPictureBackendApplication.class, args);
    }

}
