package org.totoro.common.thread;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置,@EnableAsync开启支持异步注解
 * 在方法上加@Async("executorService")，可在执行方法时使用此线程池异步
 * @author changlf 2023-05-16
 */
@EnableAsync
@Configuration
public class TtlThreadPoolExecutorConfig {

    @Value("${spring.application.name}")
    private String appName;

    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;

    /**
     * 默认最大线程数
     */
    private static final int DEFAULT_MAX_POOL_SIZE = 100;

    /**
     * 默认队列容量
     */
    private static final int DEFAULT_QUEUE_CAPACITY = 1000;

    /**
     * 线程池维护线程所允许的空闲时间(线程数>CorePoolSize并且空闲超过这个时间会销毁线程)
     */
    private static final int KEEP_ALIVE_TIME = 100;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * 当线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
     * 当线程池中的数量等于corePoolSize，但是缓冲队列workQueue未满，那么任务被放入缓冲队列。
     * 当线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
     * 当线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 handler所指定的拒绝策略来处理此任务。
     * AbortPolicy:直接抛出异常，默认情况下采用这种策略，CallerRunsPolicy:只用调用者所在线程来运行任务。也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程 maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
     * 当线程池中的线程数量大于corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。
     */
    @Bean
    public ExecutorService executorService() {
        // 阿里的TTl包装线程池，可以使TransmittableThreadLocal在父子线程中传递
        return TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY),
                r -> new Thread(r, appName + "-thread-" + threadNumber.getAndIncrement()),
                new ThreadPoolExecutor.CallerRunsPolicy()));
    }

    /**
     * 定时任务线程池使用
     */
    @Bean
    protected ScheduledExecutorService scheduledExecutorService() {
        // 阿里的TTl包装线程池，可以使TransmittableThreadLocal在父子线程中传递
        return TtlExecutors.getTtlScheduledExecutorService(new ScheduledThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE,
                runnable -> new Thread(runnable, appName + "-scheduled-thread-" + threadNumber.getAndIncrement()){{setDaemon(true);}},
                new ThreadPoolExecutor.CallerRunsPolicy()));
    }
}
