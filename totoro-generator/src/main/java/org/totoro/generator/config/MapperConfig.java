package org.totoro.generator.config;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.totoro.generator.strategy.GeneratorStrategy;
import org.totoro.generator.strategy.MapperGenStrategy;
import org.totoro.generator.strategy.MapperXmlGenStrategy;

import java.util.List;

/**
 * @author ChangLF 2023/07/21
 */
@Data
@Builder
public class MapperConfig implements GeneratorConfig {

    @Override
    public List<Class<? extends GeneratorStrategy>> getStrategy() {
        return Lists.newArrayList(MapperGenStrategy.class, MapperXmlGenStrategy.class);
    }
}
