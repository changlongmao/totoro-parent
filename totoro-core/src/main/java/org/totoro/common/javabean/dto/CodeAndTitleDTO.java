package org.totoro.common.javabean.dto;

import lombok.Data;

/**
 * @author changlf 2023-05-23
 */
@Data
public class CodeAndTitleDTO {

    /**
     * 枚举code
     */
    private final Object code;

    /**
     * 枚举标题
     */
    private final String title;

}