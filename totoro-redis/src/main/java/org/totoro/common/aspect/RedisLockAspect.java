package org.totoro.common.aspect;

import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.totoro.common.annotation.RedisLock;
import org.totoro.common.exception.CommonApiException;
import org.totoro.common.redission.RedisKeyConstant;
import org.totoro.common.util.IpUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * redisLock拦截器，此拦截器需结合@RedisLock使用
 * @see RedisLock
 * @author changlf 2022-5-29
 */
@Slf4j
@Aspect
@Order(RedisKeyConstant.REDIS_LOCK_ASPECT_ORDER)
@Component
public class RedisLockAspect {

    @Resource
    private RedissonClient redissonClient;

    // 定义切点
    @Pointcut("@annotation(org.totoro.common.annotation.RedisLock)")
    public void pointCut() {
    }

    /**
     * 当加了注解没有指定任何参数时，默认对当前方法加不等待永久持有的锁，锁的key为方法全路径名称+入参hashCode+userId(为空时拼接ip)
     * 可在注解 {@link RedisLock}中指定等待时间，锁占有时长，是否拼接userId，抛出异常的errorCode
     **/
    @Around(value = "pointCut() && @annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "#" + signature.getName();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String ip = "";
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            ip = IpUtils.getIp(request);
        }

        // TODO ChangLF，等待JWT接入后配置
        String userId = "";
//        if (RequestTrack.getCurrent() != null && RequestTrack.getCurrent().getJwt() != null) {
//            userId = RequestTrack.getCurrent().getJwt().getStaffId().toString();
//        }

        // 拼接锁的key
        StringJoiner redisKey = new StringJoiner("_");

        Object[] args = joinPoint.getArgs();
        // 判断是否为空
        if (StringUtil.isNotEmpty(redisLock.redisKey())) {
            redisKey.add(redisLock.redisKey());
        } else {
            // 不指定默认为方法全路径名称拼接入参hashCode
            redisKey.add(methodName).add(String.valueOf(Arrays.hashCode(args)));
        }
        // 若需要拼接用户id则拼接
        if (redisLock.concatUserId()) {
            redisKey.add(StringUtil.isEmpty(userId) ? ip : userId);
        }

        // 尝试获取锁
        RLock lock = redissonClient.getLock(redisKey.toString());
        // 默认锁时长为不等待，永久持有，redisson有看门狗机制，会默认加锁30秒，每10秒检查锁是否仍在使用进行续期30秒，若服务挂掉则30秒后会自动解锁
        boolean tryLock = lock.tryLock(redisLock.waitTime(), redisLock.leaseTime(), TimeUnit.SECONDS);
        if (!tryLock) {
            log.warn("ip地址: {}，用户id为: {}，访问方法路径：{}，获取锁失败, 锁的key为：{}", ip, userId, methodName, redisKey);
            // 可指定抛出异常错误码，不指定默认错误
            throw new CommonApiException(redisLock.errorCode());
        }

        log.info("ip地址: {}，用户id为: {}，访问方法路径：{}，获取锁成功, 锁的key为：{}", ip, userId, methodName, redisKey);
        Object proceed;
        try {
            // 执行方法业务逻辑
            proceed = joinPoint.proceed();
        } finally {
            // 若方法内部抛出异常保证锁能正常释放
            lock.unlock();
            log.info("{}分布式锁释放", redisKey);
        }

        return proceed;
    }

}
