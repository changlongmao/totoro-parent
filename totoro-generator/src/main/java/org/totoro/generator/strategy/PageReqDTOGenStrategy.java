package org.totoro.generator.strategy;

import org.apache.velocity.VelocityContext;
import org.totoro.generator.config.BaseConfig;
import org.totoro.generator.config.EntityConfig;
import org.totoro.generator.config.GeneratorConfig;
import org.totoro.generator.config.PackageConfig;
import org.totoro.generator.constant.GenConstant;
import org.totoro.generator.enums.JdbcTypeEnum;
import org.totoro.generator.enums.TemplateEnum;
import org.totoro.generator.dto.ColumnDTO;
import org.totoro.generator.dto.TableDTO;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * ReqDTO生成策略类
 *
 * @author ChangLF 2023/07/21
 */
public class PageReqDTOGenStrategy implements GeneratorStrategy {

    @Override
    public VelocityContext getVelocityContext(BaseConfig baseConfig, Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry,
                                              GeneratorConfig... generatorConfigArr) {
        EntityConfig entityConfig = (EntityConfig) getConfig(EntityConfig.class, generatorConfigArr);
        TableDTO tableDTO = tableDTOListEntry.getKey();
        List<ColumnDTO> columnDTOList = tableDTOListEntry.getValue();

        VelocityContext context = initVelocityContext(baseConfig, tableDTO);
        context.put("columns", columnDTOList);
        for (int i = 0; i < columnDTOList.size(); i++) {
            ColumnDTO columnDTO = columnDTOList.get(i);
            String attrType = columnDTO.getAttrType();
            if (JdbcTypeEnum.DATE.getTitle().equals(attrType)) {
                context.put("hasDate", true);
            }
            if (JdbcTypeEnum.DECIMAL.getTitle().equals(attrType)) {
                context.put("hasBigDecimal", true);
            }

            if (columnDTO.getColumnName().equals(entityConfig.getLogicDeleteColumnName())) {
                columnDTOList.remove(columnDTO);
                i--;
            }
        }

        return context;
    }

    @Override
    public String getPathname(PackageConfig packageConfig, String className) {
        return packageConfig.getJavaFileDir() + File.separator + (packageConfig.getParentPackage() + "." + packageConfig.getReqDTOPackage())
                .replaceAll("\\.", "/")
                + File.separator + className + GenConstant.PAGE_REQ_DTO_SUFFIX + GenConstant.JAVA_SUFFIX;
    }

    @Override
    public String getTemplate() {
        return TemplateEnum.PAGE_REQ_DTO.getTitle();
    }

}
