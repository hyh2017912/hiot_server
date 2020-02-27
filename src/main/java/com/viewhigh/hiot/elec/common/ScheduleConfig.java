package com.viewhigh.hiot.elec.common;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @ClassName SchedulingConfiguration
 * Description 定时配置
 * @Author huyang
 * Date 2019/5/25 17:42
 **/
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
//    @Bean(destroyMethod="shutdown")
// todo  关于线程的关闭
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskExecutor());
    }
}
