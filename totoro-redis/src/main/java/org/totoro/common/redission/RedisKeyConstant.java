package org.totoro.common.redission;

/**
 * redis key变量管理
 * @author ChangLF 2023-05-16
 */
public interface RedisKeyConstant {


    String SERVICE_NAME = "zdwp-java-common-redis:";

    /*------------------------------------------ Aspect start ------------------------------------------*/

    /**
     * RedisLockAspect拦截顺序，值越小越早执行
     */
    int REDIS_LOCK_ASPECT_ORDER = 5100;

    /**
     * RedisCacheAspect拦截顺序
     */
    int REDIS_CACHE_ASPECT_ORDER = 5200;

    /*------------------------------------------ Aspect end ------------------------------------------*/


    /*------------------------------------------ redis锁key start ------------------------------------------*/

    String LOCK_BASE_NAME = SERVICE_NAME + "lock:";

    String SNOW_SERVER_LOCK = LOCK_BASE_NAME + "snowServer";

    String SNOW_WORKER_ID_LOCK = LOCK_BASE_NAME + "snowWorkerId";

    String SNOW_DATACENTER_ID_LOCK = LOCK_BASE_NAME + "snowDatacenterId";


    /*------------------------------------------ 缓存key start ------------------------------------------*/

    String CACHE_BASE_NAME = SERVICE_NAME + "cache:";


}
