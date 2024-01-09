package org.totoro.generator;

import com.google.common.collect.Sets;
import org.totoro.generator.config.*;
import org.totoro.generator.processor.GeneratorProcessor;

import java.util.Set;

/**
 * 代码生成入口
 * @author ChangLF 2023/07/18
 */
public class GeneratorCodeBoot {

    public static void main(String[] args) {
        // 要生成的表信息
        Set<String> tables = Sets.newHashSet("table1", "table2");
        // 当前项目路径
        String home = System.getProperty("user.dir");
        // 数据库连接配置，必须有
        BaseConfig baseConfig = BaseConfig.builder()
                .jdbcUrl("jdbc:mysql://127.0.0.1:3306/${your_database}?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
                .userName("${your_username}")
                .password("${your_password}")
                // 指定生成的表，非必填，为空代表该数据库下所有表
                .tables(tables)
                .author("ChangLF")
                // 若为false，文件已存在则不生成
                .fileOverride(true)
                .packageConfig(PackageConfig.builder()
                        // java文件输出的父文件路径, 直接输出到项目代码中，请注意文件覆盖问题
                        .javaFileDir(home + "/src/main/java")
                        // java文件输出的父文件路径, 输出到外部/generator文件夹下，需自行拷贝代码
//                        .javaFileDir(home + "/generator")
                        .parentPackage("org.totoro.demo")
                        .entityPackage("entity")
                        .reqDTOPackage("javabean.dto")
                        .voPackage("javabean.vo")
                        .mapperPackage("mapper")
                        .servicePackage("service")
                        .serviceImplPackage("serviceImpl")
                        .controllerPackage("controller")
                        // 独立于javaFileDir
                        .mapperXmlDirectoryPath(home + "/src/main/resources/mapper")
                        .build())
                .build();
        // entity配置，必须有
        EntityConfigFactory entityConfig = EntityConfigFactory.builder()
                .logicDeleteColumnName("is_delete")
                .logicDeletePropertyName("deleteFlag")
                .build();
        // mapper配置，必须有
        MapperConfigFactory mapperConfig = MapperConfigFactory.builder()
                .build();
        // 可没有，若没有则不生成对应代码
        ServiceConfigFactory serviceConfig = ServiceConfigFactory.builder()
                .build();
        // 可没有，若没有则不生成对应代码
        ControllerConfigFactory controllerConfig = ControllerConfigFactory.builder()
                .build();

        // 生成代码，不填写config则不生成对应的代码
        GeneratorProcessor.process(baseConfig, entityConfig, mapperConfig, serviceConfig, controllerConfig);
    }

}
