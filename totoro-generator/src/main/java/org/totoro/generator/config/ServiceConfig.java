package org.totoro.generator.config;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.totoro.generator.strategy.GeneratorStrategy;
import org.totoro.generator.strategy.ServiceGenStrategy;
import org.totoro.generator.strategy.ServiceImplGenStrategy;

import java.util.List;


/**
 * @author ChangLF 2023/07/21
 */
@Data
@Builder
public class ServiceConfig implements GeneratorConfig {

    @Override
    public List<Class<? extends GeneratorStrategy>> getStrategy() {
        return Lists.newArrayList(ServiceGenStrategy.class, ServiceImplGenStrategy.class);
    }
}
