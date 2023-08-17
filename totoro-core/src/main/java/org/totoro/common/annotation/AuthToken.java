package org.totoro.common.annotation;

import java.lang.annotation.*;

/**
 * 校验Token注解
 * @author ChangLF 2023/08/15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface AuthToken {
}
