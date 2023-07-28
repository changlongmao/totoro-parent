package org.totoro.common.filter;

import org.totoro.common.constant.CommonConstant;
import org.totoro.common.filter.aes.AesDecryptFilter;
import org.totoro.common.filter.cors.CorsFilter;
import org.totoro.common.filter.reqtrack.RequestTrackFilter;
import org.totoro.common.filter.xss.XssFilter;
import org.totoro.common.properties.RsaKeyProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.DispatcherType;

/**
 * 过滤器配置类
 * @author ChangLF 2023-05-16
 */
@Configuration
public class FilterConfig {

    @Value("${filter.aesDecryptFilter.enable}")
    private Boolean aesDecryptFilterEnable;

    @Value("${filter.aesDecryptFilter.excludedUris}")
    private String aesDecryptFilterExcludedUris;

    @Value("${filter.requestTrackFilter.enable}")
    private Boolean requestTrackFilterEnable;

    @Value("${filter.requestTrackFilter.excludedUris}")
    private String requestTrackFilterExcludedUris;

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public FilterRegistrationBean<AesDecryptFilter> aesDecryptFilterRegistration() {
        FilterRegistrationBean<AesDecryptFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new AesDecryptFilter(applicationContext.getBean(RsaKeyProperties.class)));
        registration.addUrlPatterns("/*");
        registration.setName("aesDecryptFilter");
        registration.setOrder(CommonConstant.AES_DECRYPT_FILTER_ORDER);
        registration.setEnabled(aesDecryptFilterEnable);
        registration.addInitParameter("excludedUris", aesDecryptFilterExcludedUris);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestTrackFilter> requestTrackFilterRegistration() {
        FilterRegistrationBean<RequestTrackFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new RequestTrackFilter());
        registration.addUrlPatterns("/*");
        registration.setName("requestTrackFilter");
        registration.setOrder(CommonConstant.REQUEST_TRACK_FILTER_ORDER);
        registration.setEnabled(requestTrackFilterEnable);
        registration.addInitParameter("excludedUris", requestTrackFilterExcludedUris);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.setFilter(new CorsFilter());
        registrationBean.setOrder(CommonConstant.CORS_FILTER_ORDER);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(CommonConstant.XSS_FILTER_ORDER);
        return registration;
    }

}
