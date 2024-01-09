package org.totoro.generator.config;



import org.totoro.generator.strategy.GeneratorStrategy;

import java.util.List;

/**
 * 配置父类接口，需实现生成策略
 * @author ChangLF 2023-07-19
 */
public interface GeneratorConfigFactory {

    /**
     * 获取生成策略类
     * @author ChangLF 2024/1/9 15:29
     * @return java.util.List<java.lang.Class<? extends org.totoro.generator.strategy.GeneratorStrategy>>
     **/
    List<Class<? extends GeneratorStrategy>> getStrategy();
}