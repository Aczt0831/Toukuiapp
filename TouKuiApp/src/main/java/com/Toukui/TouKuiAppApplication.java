package com.Toukui;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;


@EnableAsync
@SpringBootApplication(scanBasePackages = "com.Toukui")
@MapperScan("com.Toukui.mapper")  // 扫描Mapper接口
public class TouKuiAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TouKuiAppApplication.class, args);
    }
    // 注入RestTemplate（用于下载微信头像）
    // 注入RestTemplate（用于下载微信头像）
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
