package org.totoro.generator.javabean.reqDto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 字符串类型id请求基类
 * @author ChangLF 2023/07/05
 */
@Data
@Accessors(chain = true)
public class IdStringReqDTO {

    /**
     * 主键id
     */
    @NotBlank(message = "id不能为空")
    private String id;
}
