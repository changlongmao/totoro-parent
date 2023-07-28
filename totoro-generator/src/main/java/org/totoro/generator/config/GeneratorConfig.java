package org.totoro.generator.config;



import org.totoro.generator.strategy.GeneratorStrategy;

import java.util.List;

/**
 * 配置父类接口，需实现生成策略
 * @author ChangLF 2023-07-19
 */
public interface GeneratorConfig {

    List<Class<? extends GeneratorStrategy>> getStrategy();
}