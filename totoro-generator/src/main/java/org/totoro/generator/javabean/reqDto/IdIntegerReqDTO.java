package org.totoro.generator.javabean.reqDto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 数值类型id请求基类
 * @author ChangLF 2023/07/05
 */
@Data
@Accessors(chain = true)
public class IdIntegerReqDTO {

    /**
     * 主键id
     */
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于等于0")
    private Integer id;
}
