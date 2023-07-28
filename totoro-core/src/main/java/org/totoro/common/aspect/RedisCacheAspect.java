package org.totoro.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.totoro.common.annotation.RedisCache;
import org.totoro.common.constant.RedisKeyConstant;
import org.totoro.common.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * RedisCache拦截器，此拦截器需结合@RedisCache使用
 *
 * @author changlf 2022-5-29
 * @see RedisCache
 */
@Slf4j
@Aspect
@Order(RedisKeyConstant.REDIS_CACHE_ASPECT_ORDER)
@Component
public class RedisCacheAspect {

    @Resource
    private RedissonClient redissonClient;

    private static final int defaultLeaseTime = 3600;

    private final Random random = new Random();

    // 定义切点
    @Pointcut("@annotation(org.totoro.common.annotation.RedisCache)")
    public void pointCut() {
    }

    /**
     * 当加了注解没有指定任何参数时，默认对同一方法同一入参将从Redis缓存中获取，默认缓存失效时间3600秒。
     * 可在注解 {@link RedisCache}中指定缓存生效时间，缓存的key
     **/
    @Around(value = "pointCut() && @annotation(redisCache)")
    public Object around(ProceedingJoinPoint joinPoint, RedisCache redisCache) throws Throwable {
        // 获取方法的全路径名
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "#" + signature.getName();

        StringJoiner sj = new StringJoiner("_");

        Object[] args = joinPoint.getArgs();
        // 判断是否为空
        if (StringUtil.isNotEmpty(redisCache.redisKey())) {
            sj.add(redisCache.redisKey());
        } else {
            // 不指定默认为方法全路径名称拼接入参hashCode
            sj.add(methodName).add(String.valueOf(Arrays.hashCode(args)));
        }

        String redisKey = sj.toString();
        RBucket<Object> rBucket = redissonClient.getBucket(redisKey);

        // 若缓存存在则返回缓存
        if (rBucket.get() != null) {
            log.info("从缓存中获取数据，缓存key为{}", redisKey);
            return rBucket.get();
        }

        // 获取缓存时加锁，防止缓存击穿
        synchronized (redisKey) {
            // 双重检查
            if (rBucket.get() != null) {
                return rBucket.get();
            }
            // 执行方法业务逻辑，获取返回值
            Object result = joinPoint.proceed();
            int leaseTime = redisCache.leaseTime();
            // 若没有设置失效时间，默认为3600秒+120秒以内的随机数，防止缓存雪崩
            if (leaseTime <= 0) {
                leaseTime = defaultLeaseTime + random.nextInt(120);
            }
            if (result != null) {
                rBucket.set(result, leaseTime, TimeUnit.SECONDS);
                log.info("执行方法逻辑获取数据并存入缓存，缓存key为{}，缓存时间为{}秒", redisKey, leaseTime);
            }
            return result;
        }
    }

}
