package org.totoro.common.annotation;

import org.totoro.common.aspect.RedisLockAspect;

import java.lang.annotation.*;

/**
 * 使用此注解会被RedisLockAspect拦截，默认对当前方法加不等待永久持有的锁，直到此方法执行结束释放锁
 * 默认锁的范围为同一ip同一方法同一入参不能重复请求
 * @see RedisLockAspect
 * @author changlf 2023-5-23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisLock {

    /**
     * 等待时间，默认-1秒，不等待，立即失败
     */
    int waitTime() default -1;

    /**
     * 持有锁时间，指定时间后自动释放锁，默认 -1秒，永久持有，直到当前锁被释放
     */
    int leaseTime() default -1;

    /**
     * 锁的key，不指定默认为方法全路径名称拼接入参hashCode
     */
    String redisKey() default "";

    /**
     * redisKey是否需要拼接userId，若找不到userId则拼接用户ip，默认不拼接
     */
    boolean concatUserId() default false;

    /**
     * 若获取锁失败抛出异常的错误码，需在api-error.properties中维护，抛出ApiException
     */
    String errorCode() default "101001010";
}
