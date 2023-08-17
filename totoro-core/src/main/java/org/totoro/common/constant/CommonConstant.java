package org.totoro.common.constant;

/**
 * @author ChangLF 2023-05-29
 */
public interface CommonConstant {

    String SERVICE_NAME = "totoro-common:";

    /*------------------------------------------ Filter start ------------------------------------------*/

    // Filter过滤器拦截一定在Aspect前面，两者之间顺序无冲突

    /**
     * XssFilter顺序
     */
    int XSS_FILTER_ORDER = 100;

    /**
     * 跨域请求过滤器
     */
    int CORS_FILTER_ORDER = 500;

    /**
     * AesDecryptFilter顺序
     */
    int AES_DECRYPT_FILTER_ORDER = 1000;

    /**
     * RequestTrackFilter顺序
     */
    int REQUEST_TRACK_FILTER_ORDER = 2000;

    /*------------------------------------------ Filter start ------------------------------------------*/


    /*------------------------------------------ Aspect start ------------------------------------------*/

    /**
     * SqlInjectAspect拦截顺序，值越小越早执行
     */
    int AUTH_TOKEN_ASPECT_ORDER = 3000;

    /**
     * SqlInjectAspect拦截顺序，值越小越早执行
     */
    int SQL_INJECT_ASPECT_ORDER = 5000;

    /**
     * XxlJobAspect拦截顺序
     */
    int XXL_JOB_ASPECT_ORDER = 5300;

    /**
     * RabbitListenerAspect拦截顺序
     */
    int RABBIT_LISTENER_ASPECT_ORDER = 5400;

    /*------------------------------------------ Aspect end ------------------------------------------*/

    /**
     * aesKey
     */
    String AES_KEY = "AesKey";

    /**
     * token前缀
     */
    String BEARER = "bearer ";

    /**
     * token请求头
     */
    String AUTHORIZATION = "Authorization";

}
