package github.polarisink.scaffold.infrastructure.aysnc.executors;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author lqs
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 系统可用计算资源
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    /**
     * 空闲线程存活时间
     */
    private static final int KEEP_ALIVE_SECONDS = 30;

    /**
     * 工厂模式
     */
    private static final MyThreadFactory MY_THREAD_FACTORY = new MyThreadFactory();

    /**
     * 饱和策略
     */
    private static final ThreadRejectedExecutionHandler THREAD_REJECTED_EXECUTION_HANDLER = new ThreadRejectedExecutionHandler.CallerRunsPolicy();

    @Bean("diy")
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(CORE_POOL_SIZE);
        //最大线程数
        executor.setMaxPoolSize(MAXIMUM_POOL_SIZE);
        //队列大小
        executor.setQueueCapacity(128);
        executor.setThreadFactory(MY_THREAD_FACTORY);
        //线程最大空闲时间
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        //指定用于新创建的线程名称的前缀。
        executor.setThreadNamePrefix("async-executor-");
        // 拒绝策略（一共四种，此处省略）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 拒绝策略（一共四种，此处省略）
        executor.setRejectedExecutionHandler(THREAD_REJECTED_EXECUTION_HANDLER);
        return executor;
    }
}
