package org.totoro.generator.strategy;

import org.apache.velocity.VelocityContext;
import org.totoro.generator.config.BaseConfig;
import org.totoro.generator.config.GeneratorConfig;
import org.totoro.generator.config.PackageConfig;
import org.totoro.generator.constant.GenConstant;
import org.totoro.generator.javabean.dto.ColumnDTO;
import org.totoro.generator.javabean.dto.TableDTO;
import org.totoro.generator.processor.VelocityProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 生成代码策略父类
 * @author ChangLF 2023/07/21
 */
public interface GeneratorStrategy {

    /**
     * 获取替换模板上下文
     * @param baseConfig 基础配置
	 * @param tableDTOListEntry 表信息
	 * @param generatorConfigArr 其他各种配置
     * @author ChangLF 2023/7/25 08:54
     * @return org.apache.velocity.VelocityContext 替换模板的上下文
     **/
    VelocityContext getVelocityContext(BaseConfig baseConfig, Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry,
                                       GeneratorConfig... generatorConfigArr);

    /**
     * 获取模板所在文件的路径名称，需在classpath下
     * @author ChangLF 2023/7/25 08:57
     * @return java.lang.String
     **/
    String getTemplate();

    /**
     * 获取要生成的文件全路径名称
     * @param packageConfig 基础配置
	 * @param className 生成的文件类名
     * @author ChangLF 2023/7/25 08:58
     * @return java.lang.String
     **/
    String getPathname(PackageConfig packageConfig, String className);

    /**
     * 生成代码执行器，调用
     * @param baseConfig 基础配置
	 * @param tableDTOListEntry 表信息
	 * @param generatorConfigArr 生成的配置文件
     * @author ChangLF 2023/7/25 08:59
     **/
    default void execute(BaseConfig baseConfig, Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry,
                         GeneratorConfig... generatorConfigArr) {
        VelocityProcessor.process(this.getVelocityContext(baseConfig, tableDTOListEntry, generatorConfigArr),
                this.getTemplate(), this.getPathname(baseConfig.getPackageConfig(), tableDTOListEntry.getKey().getClassName()));
    }

    /**
     * 初始化通用VelocityContext配置
     * @param baseConfig 基础配置
	 * @param tableDTO 表信息
     * @author ChangLF 2023/7/26 15:41
     * @return org.apache.velocity.VelocityContext
     **/
    default VelocityContext initVelocityContext(BaseConfig baseConfig, TableDTO tableDTO) {
        VelocityContext context = new VelocityContext();
        context.put("tableName", tableDTO.getTableName());
        String className = tableDTO.getClassName();
        context.put("className", className);
        String attrName = tableDTO.getAttrName();
        context.put("attrName", attrName);
        context.put("tableComment", tableDTO.getTableComment());
        context.put("author", baseConfig.getAuthor());
        context.put("date", baseConfig.getGeneratorDate());
        context.put("pk", tableDTO.getPk());
        PackageConfig packageConfig = baseConfig.getPackageConfig();
        String parentPackage = packageConfig.getParentPackage();
        context.put("controllerPackage", parentPackage + "." + packageConfig.getControllerPackage());
        context.put("controllerName", className + GenConstant.CONTROLLER_SUFFIX);
        context.put("serviceImplPackage", parentPackage + "." + packageConfig.getServiceImplPackage());
        context.put("serviceImplName", className + GenConstant.SERVICE_IMPL_SUFFIX);
        context.put("servicePackage", parentPackage + "." + packageConfig.getServicePackage());
        context.put("serviceName", className + GenConstant.SERVICE_SUFFIX);
        context.put("serviceAttrName", attrName + GenConstant.SERVICE_SUFFIX);
        context.put("mapperPackage", parentPackage + "." + packageConfig.getMapperPackage());
        context.put("mapperName", className + GenConstant.MAPPER_SUFFIX);
        context.put("mapperAttrName", attrName + GenConstant.MAPPER_SUFFIX);
        context.put("entityPackage", parentPackage + "." + packageConfig.getEntityPackage());
        context.put("entityName", className + (className.endsWith("Entity") ? "" : GenConstant.ENTITY_SUFFIX));
        context.put("voPackage", parentPackage + "." + packageConfig.getVoPackage());
        context.put("voName", className + GenConstant.VO_SUFFIX);
        context.put("voAttrName", attrName + GenConstant.VO_SUFFIX);
        context.put("reqDTOPackage", parentPackage + "." + packageConfig.getReqDTOPackage());
        context.put("reqDTOName", className + GenConstant.REQ_DTO_SUFFIX);
        context.put("reqDTOAttrName", attrName + GenConstant.REQ_DTO_SUFFIX);
        context.put("pageReqDTOName", className + GenConstant.PAGE_REQ_DTO_SUFFIX);
        context.put("pageReqDTOAttrName", attrName + GenConstant.PAGE_REQ_DTO_SUFFIX);
        return context;
    }

    /**
     * 从配置数组中获取目标配置
     * @param classT 目标配置class
	 * @param generatorConfigArr 配置数组
     * @author ChangLF 2023/7/25 09:00
     * @return org.totoro.generator.config.GeneratorConfig
     **/
    default GeneratorConfig getConfig(Class<? extends GeneratorConfig> classT, GeneratorConfig... generatorConfigArr) {
        return Arrays.stream(generatorConfigArr).filter(c -> classT.equals(c.getClass())).findFirst().orElse(null);
    }
}
