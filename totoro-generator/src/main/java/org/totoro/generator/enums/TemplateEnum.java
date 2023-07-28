package org.totoro.generator.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模版枚举类
 *
 * @author ChangLF 2023/07/21
 */
@Getter
@AllArgsConstructor
public enum TemplateEnum {

    ENTITY("template/Entity.java.vm"),

    VO("template/VO.java.vm"),

    REQ_DTO("template/ReqDTO.java.vm"),

    PAGE_REQ_DTO("template/PageReqDTO.java.vm"),

    MAPPER("template/Mapper.java.vm"),

    MAPPER_XML("template/Mapper.xml.vm"),

    SERVICE("template/Service.java.vm"),

    SERVICE_IMPL("template/ServiceImpl.java.vm"),

    CONTROLLER("template/Controller.java.vm"),

    ;

    private final String title;

}