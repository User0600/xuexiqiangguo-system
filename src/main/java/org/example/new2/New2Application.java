package org.example.new2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("org.example.new2.mapper")//扫描 Mapper 接口
@EnableScheduling
public class New2Application {

    public static void main(String[] args) {
        SpringApplication.run(New2Application.class, args);
    }

}
