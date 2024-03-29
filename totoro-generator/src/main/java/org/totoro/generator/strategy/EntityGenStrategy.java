package org.totoro.generator.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.totoro.common.util.StringUtil;
import org.totoro.generator.config.BaseConfig;
import org.totoro.generator.config.EntityConfigFactory;
import org.totoro.generator.config.GeneratorConfigFactory;
import org.totoro.generator.config.PackageConfig;
import org.totoro.generator.constant.GenConstant;
import org.totoro.generator.enums.TemplateEnum;
import org.totoro.generator.dto.ColumnDTO;
import org.totoro.generator.dto.TableDTO;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Entity生成策略类
 *
 * @author ChangLF 2023/07/21
 */
public class EntityGenStrategy implements GeneratorStrategy {

    @Override
    public VelocityContext getVelocityContext(BaseConfig baseConfig, Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry,
                                              GeneratorConfigFactory... generatorConfigFactoryArr) {
        EntityConfigFactory entityConfig = (EntityConfigFactory) getConfig(EntityConfigFactory.class, generatorConfigFactoryArr);
        TableDTO tableDTO = tableDTOListEntry.getKey();
        List<ColumnDTO> columnDTOList = tableDTOListEntry.getValue();
        boolean hasDate = false;
        boolean hasBigDecimal = false;
        boolean hasDeleteColumn = false;
        for (ColumnDTO columnDTO : columnDTOList) {
            String attrType = columnDTO.getAttrType();
            if ("Date".equals(attrType)) {
                hasDate = true;
            }
            if ("BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }

            if (columnDTO.getColumnName().equalsIgnoreCase(entityConfig.getLogicDeleteColumnName())
                    && StringUtils.isNotBlank(entityConfig.getLogicDeletePropertyName())) {
                hasDeleteColumn = true;
            }

            columnDTO.setFormatColumnName(StringUtil.sqlValidate(columnDTO.getColumnName()) ?
                    "`" + columnDTO.getColumnName() + "`" : columnDTO.getColumnName());
        }

        VelocityContext context = initVelocityContext(baseConfig, tableDTO);
        context.put("columns", columnDTOList);
        context.put("hasDate", hasDate);
        context.put("hasBigDecimal", hasBigDecimal);
        if (hasDeleteColumn) {
            context.put("logicDeleteColumn", entityConfig.getLogicDeleteColumnName());
            context.put("logicDeleteProperty", entityConfig.getLogicDeletePropertyName());
        }

        return context;
    }

    @Override
    public String getPathname(PackageConfig packageConfig, String className) {
        return packageConfig.getJavaFileDir() + File.separator + (packageConfig.getParentPackage() + "." + packageConfig.getEntityPackage())
                .replace(".", File.separator)
                + File.separator + className + GenConstant.ENTITY_SUFFIX + GenConstant.JAVA_SUFFIX;
    }

    @Override
    public String getTemplate() {
        return TemplateEnum.ENTITY.getTitle();
    }

}
