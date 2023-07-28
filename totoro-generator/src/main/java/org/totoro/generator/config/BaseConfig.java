package org.totoro.generator.config;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 基础配置
 * @author ChangLF 2023/07/20
 */
@Data
@Builder
public class BaseConfig {

    /**
     * 数据库连接
     */
    @NotBlank
    private String jdbcUrl;

    /**
     * 数据库用户名
     */
    @NotBlank
    private String userName;

    /**
     * 数据库密码
     */
    @NotBlank
    private String password;

    /**
     * 指定table，非必填，未填写时代表schema下所有的table
     */
    private Set<String> tables;

    /**
     * 为生成的代码指定作者
     */
    @NotBlank
    private String author;

    /**
     * 包路径配置
     */
    @Valid
    @NotNull
    private PackageConfig packageConfig;

    /**
     * 是否文件覆盖，默认为true
     */
    @NotNull
    private Boolean fileOverride;

    /**
     * 生成注释日期，可指定，默认为当前时间yyyy/mm/dd格式
     */
    private String generatorDate;

}
