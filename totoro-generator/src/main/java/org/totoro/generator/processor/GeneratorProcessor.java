package org.totoro.generator.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.totoro.common.util.ValidatorUtils;
import org.totoro.generator.config.BaseConfig;
import org.totoro.generator.config.GeneratorConfig;
import org.totoro.generator.factory.GeneratorSingletonFactory;
import org.totoro.generator.dto.ColumnDTO;
import org.totoro.generator.dto.TableDTO;
import org.totoro.generator.strategy.GeneratorStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 代码生成启动类
 *
 * @author ChangLF 2023/07/21
 */
@Slf4j
public class GeneratorProcessor {

    /**
     * 开始生成代码
     *
     * @param generatorConfigArr 核心配置
     * @author ChangLF 2023/7/21 11:37
     **/
    public static void process(BaseConfig baseConfig, GeneratorConfig... generatorConfigArr) {
        log.info("开始生成代码");
        // 校验参数
        ValidatorUtils.validateBean(baseConfig);
        ValidatorUtils.validateBean(generatorConfigArr);
        if (StringUtils.isBlank(baseConfig.getGeneratorDate())) {
            baseConfig.setGeneratorDate(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        }
        VelocityProcessor.setFileOverride(baseConfig.getFileOverride());

        // 获取表字段信息
        Map<TableDTO, List<ColumnDTO>> tableColumnMap = InformationSchemaProcessor.getTableColumn(baseConfig);
        // 装饰表参数
        InformationSchemaProcessor.decorate(tableColumnMap);

        // 根据配置获取代码生成策略
        List<GeneratorStrategy> strategyList = new ArrayList<>();
        Stream.of(generatorConfigArr).map(GeneratorConfig::getStrategy)
                .forEach(list -> list.forEach(s -> strategyList.add(GeneratorSingletonFactory.doCreateBean(s))));

        // 按表顺序生成代码，若中间失败则前面生成的表仍成功
        for (Map.Entry<TableDTO, List<ColumnDTO>> tableDTOListEntry : tableColumnMap.entrySet()) {
            for (GeneratorStrategy generatorStrategy : strategyList) {
                generatorStrategy.execute(baseConfig, tableDTOListEntry, generatorConfigArr);
            }
        }
    }

}
