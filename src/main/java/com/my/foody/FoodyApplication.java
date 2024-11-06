package com.my.foody;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class FoodyApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodyApplication.class, args);
    }
}
