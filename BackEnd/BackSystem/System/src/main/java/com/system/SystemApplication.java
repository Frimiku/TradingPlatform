package com.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.system.business.mapper")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
    }
}
