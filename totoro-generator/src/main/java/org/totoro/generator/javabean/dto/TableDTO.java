package org.totoro.generator.javabean.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 表信息
 * @author ChangLF 2023/07/19
 */
@Data
@Accessors(chain = true)
public class TableDTO {

    /**
     * 数据库表名
     */
    private String tableName;

    /**
     * 对应java类名
     */
    private String className;

    /**
     * 对应java中变量名
     */
    private String attrName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 主键字段
     */
    private ColumnDTO pk;
}
