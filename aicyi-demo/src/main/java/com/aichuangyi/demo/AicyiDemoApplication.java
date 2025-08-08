package com.aichuangyi.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 2023/9/7
 **/
@SpringBootApplication(scanBasePackages = {"com.aichuangyi.demo"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties
public class AicyiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AicyiDemoApplication.class, args);
    }
}
