package org.totoro.common.annotation;

import java.lang.annotation.*;

/**
 * 忽略sql注入注解
 * @author ChangLF 2023/07/11
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreSqlInject {
}
