package com.my.foody;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FoodyApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodyApplication.class, args);
    }

}
