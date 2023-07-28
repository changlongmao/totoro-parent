package org.totoro.common.idWorker;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.totoro.common.constant.RedisKeyConstant;

import javax.annotation.Resource;

/**
 *
 * @author changlf 2023-5-16
 */
@Configuration
public class SnowflakeAutoConfig {

    @Resource
    private RedissonClient redissonClient;

    @Value("${spring.application.name}")
    private String appName;

    /**
     * 初始化snowflakeIdWorker
     */
    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        Snowflake snowflake = new Snowflake();
        snowflake.setDatacenterId(getDatacenterId());
        snowflake.setWorkerId(getWorkerId());
        return new SnowflakeIdWorker(snowflake);
    }

    /**
     * 获取workerId，根据服务名保存映射关系，正常不会超过31
     * @author ChangLF 2023/5/16 15:43
     * @return long
     **/
    public long getWorkerId() {
        RMap<String, Long> serverMap = redissonClient.getMap(RedisKeyConstant.SNOW_SERVER_LOCK);
        Long workerId = serverMap.get(appName);
        if (workerId == null) {
            RAtomicLong workerIdAtomic = redissonClient.getAtomicLong(RedisKeyConstant.SNOW_WORKER_ID_LOCK);
            long id = workerIdAtomic.incrementAndGet();
            if (id > 31) {
                workerIdAtomic.set(0);
                id = 0;
            }
            workerId = id;
            serverMap.put(appName, workerId);
        }
        return workerId;
    }

    /**
     * 获取数据中心自增id，超过31后从0开始
     * @author ChangLF 2023/5/16 15:41
     * @return long
     **/
    public long getDatacenterId() {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(RedisKeyConstant.SNOW_DATACENTER_ID_LOCK);
        long id = atomicLong.incrementAndGet();
        if (id > 31) {
            atomicLong.set(0);
            id = 0;
        }
        return id;
    }


}
