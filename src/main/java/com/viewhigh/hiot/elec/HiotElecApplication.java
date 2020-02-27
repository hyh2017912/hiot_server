package com.viewhigh.hiot.elec;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,DruidDataSourceAutoConfigure.class},
        scanBasePackages = {"com.viewhigh.hiot"})
@EnableCaching
@EnableScheduling
public class HiotElecApplication{
    public static void main(String[] args){
        SpringApplication.run(HiotElecApplication.class, args);
    }
}
