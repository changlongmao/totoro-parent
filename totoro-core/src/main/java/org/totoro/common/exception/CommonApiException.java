package org.totoro.common.exception;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服务异常信息维护
 * @author ChangLF 2023/5/23 11:27
 **/
public class CommonApiException extends BaseException {

    /**
     * 当前服务错误信息维护api-error.properties错误码后调用此方法
     * 抛出异常后会被GlobalExceptionHandler异常拦截器拦截并处理
     * @param errCode 错误码
	 * @param args 可通过%s替换参数
     * @author ChangLF 2023/5/23 11:26
     **/
    public CommonApiException(String errCode, Object... args) {
        super(errCode, StringUtils.isEmpty(getErrorInfo(errCode)) ? String.valueOf(args[0]) : String.format(getErrorInfo(errCode), args));
    }

    /**
     * 从api-error.properties中获取错误码对应错误信息，若未维护错误码则返回null
     * @author ChangLF 2023/5/23 11:26
     **/
    private static String getErrorInfo(String errCode) {
        return ApiResultUtil.getErrorInfo(errCode);
    }

    public static class ApiResultUtil {
        private static final Properties p = new Properties();

        static {
            try (InputStream inputStream = ApiResultUtil.class.getClassLoader().getResourceAsStream("api-common-error.properties");){
                p.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("读取文件出错", e);
            }
        }

        public static String getErrorInfo(String errCode) {
            return p.getProperty(errCode);
        }
    }

}