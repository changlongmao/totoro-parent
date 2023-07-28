package org.totoro.generator.javabean.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ChangLF 2023-07-19
 */
@Data
@Accessors(chain = true)
public class ColumnDTO {

    /**
     * 数据库字段名
     */
    private String columnName;

    /**
     * 数据库字段名
     */
    private String formatColumnName;

    /**
     * 对应java中属性名
     */
    private String attrName;

    /**
     * 字段类型
     */
    private String dataType;

    /**
     * 对应java中字段属性
     */
    private String attrType;

    /**
     * 列类型,比data_type更详细，如data_type 是int 而column_type是int(11)
     */
    private String columnType;

    /**
     * 字段描述
     */
    private String columnComment;

    /**
     * 索引类型 主键-->PRI  | 唯一索引 -->UNI  一般索引 -->MUL
     */
    private String columnKey;

    /**
     * 是否允许为null
     */
    private String isNullable;

    /**
     * 字段默认值
     */
    private String columnDefault;

}