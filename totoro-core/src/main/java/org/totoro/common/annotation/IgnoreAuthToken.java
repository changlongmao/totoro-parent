package org.totoro.common.annotation;

import java.lang.annotation.*;

/**
 * 忽略{@link AuthToken}
 * @author ChangLF 2023/08/17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IgnoreAuthToken {
}
