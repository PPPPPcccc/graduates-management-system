package com.kunshan.graduates;

import com.kunshan.graduates.service.impl.UserServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.kunshan.graduates.mapper")
public class GraduatesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduatesApplication.class, args);
    }

    /**
     * 启动时自动做的事：把默认账号的密码初始化成 BCrypt 加密值
     */
    @Bean
    public CommandLineRunner initDefaultUsers(UserServiceImpl userService) {
        return args -> {
            userService.encodeAndUpdatePassword("admin", "admin123");
            userService.encodeAndUpdatePassword("staff", "staff123");
            System.out.println("========== 默认账号密码已初始化 ==========");
            System.out.println("admin / admin123 （管理员）");
            System.out.println("staff / staff123 （普通用户）");
            System.out.println("==========================================");
        };
    }
}