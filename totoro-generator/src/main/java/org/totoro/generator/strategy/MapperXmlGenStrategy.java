package org.totoro.generator.strategy;

import org.apache.velocity.VelocityContext;
import org.totoro.generator.config.*;
import org.totoro.generator.constant.GenConstant;
import org.totoro.generator.enums.TemplateEnum;
import org.totoro.generator.dto.ColumnDTO;
import org.totoro.generator.dto.TableDTO;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * MapperXml生成策略类
 * @author ChangLF 2023/07/21
 */
public class MapperXmlGenStrategy implements GeneratorStrategy{

    @Override
    public VelocityContext getVelocityContext(BaseConfig baseConfig, Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry, GeneratorConfigFactory... generatorConfigFactoryArr) {
        MapperConfigFactory config = (MapperConfigFactory) getConfig(MapperConfigFactory.class, generatorConfigFactoryArr);
        EntityConfigFactory entityConfig = (EntityConfigFactory) getConfig(EntityConfigFactory.class, generatorConfigFactoryArr);
        TableDTO tableDTO = tableDTOListEntry.getKey();
        List<ColumnDTO> columnDTOList = tableDTOListEntry.getValue();

        VelocityContext context = initVelocityContext(baseConfig, tableDTO);
        context.put("columns", columnDTOList);
        if (columnDTOList.stream().anyMatch(c -> c.getColumnName().equalsIgnoreCase(entityConfig.getLogicDeleteColumnName()))) {
            context.put("logicDeleteColumn", entityConfig.getLogicDeleteColumnName());
            context.put("logicDeleteProperty", entityConfig.getLogicDeletePropertyName());
        }
        return context;
    }

    @Override
    public String getTemplate() {
        return TemplateEnum.MAPPER_XML.getTitle();
    }

    @Override
    public String getPathname(PackageConfig packageConfig, String className) {
        return packageConfig.getMapperXmlDirectoryPath() + File.separator + className + GenConstant.MAPPER_SUFFIX + GenConstant.XML_SUFFIX;
    }

}
