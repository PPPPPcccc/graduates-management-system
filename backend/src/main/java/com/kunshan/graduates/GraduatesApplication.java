package com.kunshan.graduates;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kunshan.graduates.mapper")
public class GraduatesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduatesApplication.class, args);
    }
}