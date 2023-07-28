package org.totoro.common.javabean.dto;


/**
 * 所有枚举的直接或间接父类
 *
 * @author changlf 2023-05-23
 */
public interface EnumAncestor<C> {

    /**
     * 获取枚举的code的方式
     *
     * 当派生的枚举对象转化为JSON时，JSON Value会来自这个方法
     */
    C getCode();

    /**
     * 获取枚举的标题的方式
     */
    String getTitle();

    /**
     * 转化成Javabean形式
     */
    default CodeAndTitleDTO asJavabean() {
        return new CodeAndTitleDTO(getCode(), getTitle());
    }

}
