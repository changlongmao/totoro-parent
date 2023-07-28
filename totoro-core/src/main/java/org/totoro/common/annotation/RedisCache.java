package org.totoro.common.annotation;

import org.totoro.common.aspect.RedisCacheAspect;

import java.lang.annotation.*;

/**
 * 使用此注解会被RedisCacheAspect拦截
 * @see RedisCacheAspect
 * @author changlf 2023-5-23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisCache {

    /**
     * 缓存生效时间，单位为秒，默认为3600秒+120秒以内的随机数，防止缓存雪崩
     */
    int leaseTime() default 0;

    /**
     * 缓存的key，不指定默认为方法全路径名称拼接入参hashCode
     */
    String redisKey() default "";
}
