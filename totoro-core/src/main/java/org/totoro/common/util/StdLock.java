package org.totoro.common.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.totoro.common.exception.CommonApiException;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 标准分布式锁（StdLock, Standard distributed Lock）
 *
 * @author changlf 2023-06-27
 */
@Slf4j
@Component
public class StdLock {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 分布式锁方法，默认对方法加无限持有直至方法执行结束的锁，未获取到锁等待时间为30秒
     *
     * @param key  锁的key，需在constant中维护
     * @param task 被锁住的代码块
     * @throws CommonApiException 错误码101001012=请求失败，请稍候重试
     * @author ChangLF 2023/6/29 11:05
     **/
    public void lock(String key, Runnable task) throws CommonApiException {
        tryLock(key, 30, -1, task);
    }

    /**
     * 分布式锁方法，可指定锁的持有时间和未抢到锁的等待时间
     *
     * @param key  锁的key，需在constant中维护
     * @param task 被锁住的代码块
     * @author ChangLF 2023/6/29 11:05
     **/
    public void lock(String key, long waitTime, long leaseTime, Runnable task) throws CommonApiException {
        tryLock(key, waitTime, leaseTime, task);
    }

    private void tryLock(String key, long waitTime, long leaseTime, Runnable task) throws CommonApiException {
        log.info("StdLock: key={}", key);
        RLock lock = redissonClient.getLock(key);
        boolean gotcha = false;

        try {
            // 未获取锁则最长等待30秒，30秒后获取锁失败，若获取锁成功则一直持有锁直至方法执行结束
            gotcha = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("StdLock: interrupt to wait lock, key={}", key);
        }
        if (!gotcha) {
            log.error("StdLock: cannot get lock, key={}", key);
            throw new CommonApiException("101001012");
        }

        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }

}