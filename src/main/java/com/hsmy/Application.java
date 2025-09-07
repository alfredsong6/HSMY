package com.hsmy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 应用启动类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@SpringBootApplication
@MapperScan("com.hsmy.mapper")
@EnableTransactionManagement
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("========================================");
        System.out.println("        敲敲木鱼项目启动成功！");
        System.out.println("        Server: http://localhost:8080/api");
        System.out.println("========================================");
    }
}