package com.otakumap.global.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "mailExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("Async MailExecutor-");
        executor.initialize();

        return executor;
    }
    //https://jeong-pro.tistory.com/253

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error(
                    "[Async 예외] type={}, method={}, parameter={}",
                    ex.getClass().getSimpleName(),
                    method.getName(),
                    params,
                    ex
            );
        };
    }
}
