package org.totoro.generator.strategy;

import org.apache.velocity.VelocityContext;
import org.totoro.generator.config.BaseConfig;
import org.totoro.generator.config.GeneratorConfig;
import org.totoro.generator.config.MapperConfig;
import org.totoro.generator.config.PackageConfig;
import org.totoro.generator.constant.GenConstant;
import org.totoro.generator.enums.TemplateEnum;
import org.totoro.generator.dto.ColumnDTO;
import org.totoro.generator.dto.TableDTO;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Mapper生成策略类
 *
 * @author ChangLF 2023/07/21
 */
public class MapperGenStrategy implements GeneratorStrategy {

    @Override
    public VelocityContext getVelocityContext(BaseConfig baseConfig, Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry, GeneratorConfig... generatorConfigArr) {
        MapperConfig config = (MapperConfig) getConfig(MapperConfig.class, generatorConfigArr);
        TableDTO tableDTO = tableDTOListEntry.getKey();

        return initVelocityContext(baseConfig, tableDTO);
    }

    @Override
    public String getTemplate() {
        return TemplateEnum.MAPPER.getTitle();
    }

    @Override
    public String getPathname(PackageConfig packageConfig, String className) {
        return packageConfig.getJavaFileDir() + File.separator + (packageConfig.getParentPackage() + "." + packageConfig.getMapperPackage())
                .replaceAll("\\.", File.separator)
                + File.separator + className + GenConstant.MAPPER_SUFFIX + GenConstant.JAVA_SUFFIX;
    }

}
