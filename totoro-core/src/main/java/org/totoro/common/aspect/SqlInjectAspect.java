package org.totoro.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.totoro.common.json.JsonUtils;
import org.totoro.common.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.totoro.common.annotation.IgnoreSqlInject;
import org.totoro.common.constant.CommonConstant;
import org.totoro.common.exception.CommonApiException;

/**
 * Sql防止注入拦截器
 *
 * @author 2023/7/11 ChangLF
 **/
@Slf4j
@Order(CommonConstant.SQL_INJECT_ASPECT_ORDER)
@Aspect
@Component
public class SqlInjectAspect {

    /**
     * 拦截org包路径下controller路径下的所有方法
     */
    @Pointcut("execution(public * org..controller..*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        IgnoreSqlInject ignoreSqlInject = signature.getMethod().getAnnotation(IgnoreSqlInject.class);
        // 若方法上有忽略sql注入注解，则跳过
        if (ignoreSqlInject != null) {
            return point.proceed();
        }
        StringBuilder param = new StringBuilder();
        //获取目标方法的参数信息
        Object[] args = point.getArgs();
        JsonUtils.parseNodeValue(JsonUtils.toTree(JsonUtils.toJson(args)), param);

        // 校验是否包含sql关键字
        if (StringUtil.sqlValidate(param.toString())) {
            throw new CommonApiException("101001013");
        }
        return point.proceed();
    }

}
