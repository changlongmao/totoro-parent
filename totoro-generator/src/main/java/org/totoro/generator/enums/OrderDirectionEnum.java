package org.totoro.generator.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 排序方向枚举
 * @author ChangLF 2023/07/06
 */
@Getter
@AllArgsConstructor
public enum OrderDirectionEnum {

    NONE(1, ""),

    ASC(2, "asc"),

    DESC(3, "desc"),

    ;

    @JsonValue
    private final Integer code;

    private final String title;

    /**
     * 判断参数code是否是一个有效的枚举
     */
    public static boolean valid(Integer code) {
        return Arrays.stream(values()).anyMatch(anEnum -> anEnum.getCode().equals(code));
    }

    /**
     * 获取code对应的枚举
     */
    @JsonCreator
    public static OrderDirectionEnum of(Integer code) {
        return Arrays.stream(values()).filter(anEnum -> anEnum.getCode().equals(code)).findFirst().orElse(null);
    }

    /**
     * 获取code对应的title
     */
    public static String getTitleByCode(Integer code) {
        return Arrays.stream(values()).filter(anEnum -> anEnum.getCode().equals(code)).map(OrderDirectionEnum::getTitle).findFirst().orElse(null);
    }

}