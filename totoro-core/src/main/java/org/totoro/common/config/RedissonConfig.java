package org.totoro.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置，要根据  redis  的配置情况实现
 * 本项目中 Redis 采用单节点 ，所以使用Redisson的 useSingleServer配置
 *
 * 配置参考
 * https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#21-%E7%A8%8B%E5%BA%8F%E5%8C%96%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
 *
 * @author changlf 2023-5-16
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private Integer timeout;
    @Value("${spring.redis.database}")
    private Integer database;

    /**
     * 单体redis的配置
     * @return Config
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password)
                .setTimeout(timeout)
                .setDatabase(database);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

}
