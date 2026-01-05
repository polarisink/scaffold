package com.scaffold.download;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DownloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(DownloadApplication.class, args);
    }

    @Bean
    public ScheduledTaskRegistrar scheduledTaskRegistrar() {
        ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setThreadFactory(Thread.ofVirtual().factory());
//        scheduler.setPoolSize(100);
//        scheduler.setThreadNamePrefix("123--");
//        scheduler.setWaitForTasksToCompleteOnShutdown(false);
//        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.initialize();
        registrar.setTaskScheduler(scheduler);
        return registrar;
    }

}
