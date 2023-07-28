package org.totoro.common.filter.aes;

import lombok.extern.slf4j.Slf4j;
import org.totoro.common.exception.CommonApiException;
import org.totoro.common.properties.RsaKeyProperties;
import org.totoro.common.util.StringUtil;
import org.totoro.common.util.encrypt.AESUtils;
import org.totoro.common.util.encrypt.RSAUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 出入参加解密过滤器
 *
 * @author ChangLF 2023-05-24
 */
@Slf4j
public class AesDecryptFilter implements Filter {

    private final RsaKeyProperties rsaKeyProperties;

    private String excludedUris;

    public AesDecryptFilter(RsaKeyProperties rsaKeyProperties) {
        this.rsaKeyProperties = rsaKeyProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludedUris = filterConfig.getInitParameter("excludedUris");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (StringUtil.isExcludedUri(excludedUris, httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String method = httpRequest.getMethod();
        String aesKey = httpRequest.getHeader("aesKey");
        String authorization = httpRequest.getHeader("Authorization");
        // 过滤需要加解密的请求
        if ("POST".equalsIgnoreCase(method) && StringUtil.isNotEmpty(aesKey) && StringUtil.isNotEmpty(authorization) && authorization.startsWith("bearer ")) {
            String aesKeyDecrypt;
            try {
                aesKeyDecrypt = RSAUtil.decrypt(aesKey, rsaKeyProperties.getPrivateKey());
            } catch (Exception e) {
                log.warn("解密aesKey失败", e);
                throw new CommonApiException("101001005");
            }
            // 解密请求体
            RequestWrapper requestWrapper = new RequestWrapper(httpRequest);
            String requestBody = requestWrapper.getRequestBody();
            String decryptedRequestBody = AESUtils.decrypt(requestBody, aesKeyDecrypt);
            // 将解密后的请求体重新设置到请求中
            requestWrapper.setRequestBody(decryptedRequestBody);

            // 封装响应体
            ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);
            try {
                chain.doFilter(requestWrapper, responseWrapper);
            } finally {
                // 加密响应体
                String encryptedResponse = AESUtils.encrypt(responseWrapper.getResponseBody(), aesKeyDecrypt);
                try (ServletOutputStream outputStream = httpResponse.getOutputStream()) {
                    outputStream.write(encryptedResponse.getBytes(StandardCharsets.UTF_8));
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
