package org.totoro.generator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ChangLF 2023/07/20
 */
@Getter
@AllArgsConstructor
public enum JdbcTypeEnum {

    TINYINT("tinyint", "Byte"),

    SMALLINT("smallint", "Integer"),

    MEDIUMINT("mediumint", "Integer"),

    INT("int", "Integer"),

    BIGINT("bigint", "Long"),

    FLOAT("float", "Float"),

    DOUBLE("double", "Double"),

    DECIMAL("decimal", "BigDecimal"),

    BIT("bit", "Boolean"),

    BLOB("blob", "byte[]"),

    LONGBLOB("longblob", "byte[]"),

    CHAR("char", "String"),

    VARCHAR("varchar", "String"),

    TINYTEXT("tinytext", "String"),

    TEXT("text", "String"),

    MEDIUMTEXT("mediumtext", "String"),

    LONGTEXT("longtext", "String"),

    DATE("date", "Date"),

    TIME("time", "Date"),

    DATETIME("datetime", "Date"),

    TIMESTAMP("timestamp", "Date"),

    ;

    private final String code;

    private final String title;

    /**
     * 获取code对应的title
     */
    public static String getTitleByCode(String code) {
        return Arrays.stream(values()).filter(anEnum -> anEnum.getCode().equalsIgnoreCase(code)).map(JdbcTypeEnum::getTitle).findFirst().orElse("unKnownType");
    }

}