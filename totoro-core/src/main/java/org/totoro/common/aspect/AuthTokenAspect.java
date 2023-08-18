package org.totoro.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.totoro.common.annotation.IgnoreAuthToken;
import org.totoro.common.constant.CommonConstant;
import org.totoro.common.exception.AuthException;
import org.totoro.common.filter.reqtrack.RequestTrack;
import org.totoro.common.javabean.dto.JwtClaimDTO;
import org.totoro.common.util.JwtSolver;
import org.totoro.common.util.StringUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Token校验拦截器
 * @author ChangLF 2023/8/15
 **/
@Slf4j
@Order(CommonConstant.AUTH_TOKEN_ASPECT_ORDER)
@Aspect
@Component
public class AuthTokenAspect {

    @Resource
    private JwtSolver jwtSolver;

    /**
     * 切点函数@annotation表示匹配方法上的注解，切点函数@within表示匹配类上的注解，将拦截类中所有方法
     * @param point 切点
     * @author ChangLF 2023/8/16 09:52
     * @return java.lang.Object
     **/
    @Around("@annotation(org.totoro.common.annotation.AuthToken) || @within(org.totoro.common.annotation.AuthToken)")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        IgnoreAuthToken ignoreAuthToken = signature.getMethod().getAnnotation(IgnoreAuthToken.class);
        // 若方法上有ignoreAuthToken注解则跳过
        if (ignoreAuthToken != null) {
            return point.proceed();
        }
        RequestTrack requestTrack = RequestTrack.getCurrent();

        String bearerToken = requestTrack.getRawRequest().getHeader(CommonConstant.AUTHORIZATION);
        if (StringUtil.isEmpty(bearerToken) || !bearerToken.startsWith(CommonConstant.BEARER)) {
            requestTrack.getRawResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new AuthException("401", "token为空或格式错误");
        }
        String token = bearerToken.substring(CommonConstant.BEARER.length());
        if (jwtSolver.isTokenExpired(token)) {
            requestTrack.getRawResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new AuthException("401", "token已过期");
        }
        JwtClaimDTO jwtClaimDTO = jwtSolver.parseToken(token);
        if (jwtClaimDTO == null) {
            requestTrack.getRawResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new AuthException("401", "token解析失败");
        }
        requestTrack.setJwtClaimDTO(jwtClaimDTO);
        return point.proceed();
    }

}
