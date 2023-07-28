package org.totoro.generator.config;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.totoro.generator.strategy.*;

import java.util.List;

/**
 * Entity配置类
 *
 * @author ChangLF 2023/07/21
 */
@Data
@Builder
public class EntityConfig implements GeneratorConfig {

    /**
     * 逻辑删除字段名(数据库)
     */
    private String logicDeleteColumnName;

    /**
     * 逻辑删除属性名(实体)
     */
    private String logicDeletePropertyName;

    @Override
    public List<Class<? extends GeneratorStrategy>> getStrategy() {
        return Lists.newArrayList(EntityGenStrategy.class, VOGenStrategy.class, ReqDTOGenStrategy.class,
                PageReqDTOGenStrategy.class);
    }
}
