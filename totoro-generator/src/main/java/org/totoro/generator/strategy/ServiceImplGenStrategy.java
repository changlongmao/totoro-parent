package org.totoro.generator.strategy;

import org.apache.velocity.VelocityContext;
import org.totoro.generator.config.BaseConfig;
import org.totoro.generator.config.GeneratorConfigFactory;
import org.totoro.generator.config.PackageConfig;
import org.totoro.generator.config.ServiceConfigFactory;
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
public class ServiceImplGenStrategy implements GeneratorStrategy {

    @Override
    public VelocityContext getVelocityContext(BaseConfig baseConfig, Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry, GeneratorConfigFactory... generatorConfigFactoryArr) {
        ServiceConfigFactory config = (ServiceConfigFactory) getConfig(ServiceConfigFactory.class, generatorConfigFactoryArr);
        TableDTO tableDTO = tableDTOListEntry.getKey();

        return initVelocityContext(baseConfig, tableDTO);
    }

    @Override
    public String getTemplate() {
        return TemplateEnum.SERVICE_IMPL.getTitle();
    }

    @Override
    public String getPathname(PackageConfig packageConfig, String className) {
        return packageConfig.getJavaFileDir() + File.separator + (packageConfig.getParentPackage() + "." + packageConfig.getServiceImplPackage())
                .replace(".", File.separator)
                + File.separator + className + GenConstant.SERVICE_IMPL_SUFFIX + GenConstant.JAVA_SUFFIX;
    }

}
