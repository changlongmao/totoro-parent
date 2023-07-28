package org.totoro.generator.javabean.reqDto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.totoro.generator.enums.OrderDirectionEnum;

import javax.validation.constraints.Min;

/**
 * 列表查询基类
 *
 * @author ChangLF 2023/07/06
 */
@Data
@Accessors(chain = true)
public class SPPageReqDTO {

    /**
     * 页码
     */
    @Min(value = 1, message = "页码最小值为1")
    private Integer pageIndex;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    /**
     * 排序列
     */
    private String sortColumn;

    /**
     * 排序方向
     */
    private OrderDirectionEnum sortDirection;

    /**
     * ID字符串，用英文逗号隔开
     */
    private String ids;

    public SPPageReqDTO() {
        this.pageIndex = 1;
        this.pageSize = 10;
    }

}
