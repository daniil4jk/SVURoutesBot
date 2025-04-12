package ru.daniil4jk.svuroutes.tgbot;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncAndMultithreadingConfig {
    @Autowired
    @Lazy
    private AsyncAndMultithreadingConfig me;

    @Bean
    protected ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Math.max(Runtime.getRuntime().availableProcessors() * 2, 16));
        log.info("Physical threads: {}, created threads for taskExecutor: {}",
                Runtime.getRuntime().availableProcessors(), executor.getCorePoolSize());
        return executor;
    }

    @Bean
    protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        ScheduledThreadPoolExecutor executor =
                new ScheduledThreadPoolExecutor(Math.max(Runtime.getRuntime().availableProcessors(), 4));
        log.info("Created threads for scheduledTaskExecutor: {}", executor.getCorePoolSize());
        return executor;
    }

    @PreDestroy
    protected void stopAllExecutors() {
        me.taskExecutor().shutdown();
        me.scheduledThreadPoolExecutor().shutdown();
    }
}

