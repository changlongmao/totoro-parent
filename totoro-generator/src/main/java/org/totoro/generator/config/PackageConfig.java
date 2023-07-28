package org.totoro.generator.config;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Package配置
 * @author ChangLF 2023/07/20
 */
@Data
@Builder
public class PackageConfig {

    /**
     * 输出父文件路径
     */
    @NotBlank
    private String javaFileDir;

    /**
     * 父包名
     */
    @NotBlank
    private String parentPackage;

    /**
     * Entity类的包名
     */
    @NotBlank
    private String entityPackage;

    /**
     * vo类的包名
     */
    @NotBlank
    private String voPackage;

    /**
     * reqDTO类的包名
     */
    @NotBlank
    private String reqDTOPackage;

    /**
     * mapper接口的包名
     */
    @NotBlank
    private String mapperPackage;

    /**
     * mapper.xml所在目录的绝对路径
     */
    @NotBlank
    private String mapperXmlDirectoryPath;

    /**
     * service接口的包名
     */
    @NotBlank
    private String servicePackage;

    /**
     * serviceImpl接口的包名
     */
    @NotBlank
    private String serviceImplPackage;

    /**
     * controller接口的包名
     */
    @NotBlank
    private String controllerPackage;

}
