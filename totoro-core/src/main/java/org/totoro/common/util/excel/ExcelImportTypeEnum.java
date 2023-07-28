package org.totoro.common.util.excel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.totoro.common.javabean.dto.EnumAncestor;

import java.util.Arrays;

/**
 * @author ChangLF 2023-05-13
 */
@Getter
@AllArgsConstructor
public enum ExcelImportTypeEnum implements EnumAncestor<String> {

    /**
     * 1为fileUrl，2为inputStream，3为file
     */
    FILE_URL("1", "fileUrl"),

    INPUT_STREAM("2", "inputStream"),

    MULTIPART_FILE("3", "file"),

    ;

    @JsonValue
    private final String code;

    private final String title;

    /**
     * 判断参数code是否是一个有效的枚举
     */
    public static boolean valid(String code) {
        return Arrays.stream(values()).anyMatch(anEnum -> anEnum.getCode().equals(code));
    }

    /**
     * 获取code对应的枚举
     */
    @JsonCreator
    public static ExcelImportTypeEnum of(String code) {
        return Arrays.stream(values()).filter(anEnum -> anEnum.getCode().equals(code)).findFirst().orElse(null);
    }

    /**
     * 获取code对应的title
     */
    public static String getTitleByCode(Byte code) {
        return Arrays.stream(values()).filter(anEnum -> anEnum.getCode().equals(String.valueOf(code))).map(ExcelImportTypeEnum::getTitle).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return asJavabean().toString();
    }

}