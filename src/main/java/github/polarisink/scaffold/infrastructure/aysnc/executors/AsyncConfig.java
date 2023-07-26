package github.polarisink.scaffold.infrastructure.aysnc.executors;

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author lqs
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {


    private final ThreadPoolTaskExecutor executor;

    public AsyncConfig(@Qualifier("diy") ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        executor.initialize();
        // 这一步千万不能忘了，否则报错： java.lang.IllegalStateException: ThreadPoolTaskExecutor not initialized
        executor.initialize();
        return executor;
    }

    /**
     * 异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (arg0, arg1, arg2) -> {
            LOG.error("==========================" + arg0.getMessage() + "=======================", arg0);
            LOG.error("exception method:" + arg1.getName());
        };
    }

}
