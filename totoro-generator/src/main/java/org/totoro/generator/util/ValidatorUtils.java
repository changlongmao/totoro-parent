package org.totoro.generator.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 可充当@Valid 在代码中校验参数
 *
 * @author ChangLF 2023/7/8 10:43
 **/
public class ValidatorUtils {

    private static final Validator VALIDATOR;

    static {
        VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static void validateBean(Object object, Class<?>... groups) {
        if (object instanceof Collection) {
            for (Object obj : (Collection<?>) object) {
                valid(obj, groups);
            }
        } else if (object instanceof Map) {
            for (Map.Entry<?, ?> entity : ((Map<?, ?>) object).entrySet()) {
                valid(entity.getValue(), groups);
            }
        } else if (object.getClass().isArray()) {
            for (Object obj : ((Object[]) object)) {
                valid(obj, groups);
            }
        } else {
            valid(object, groups);
        }
    }

    private static void valid(Object object, Class<?>[] groups) {
        Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append("参数校验失败：");
            for (ConstraintViolation<Object> constraint : constraintViolations) {
                msg.append(constraint.getPropertyPath().toString()).append(constraint.getMessage()).append(";\t");
            }
            throw new RuntimeException(msg.toString());
        }
    }
}
