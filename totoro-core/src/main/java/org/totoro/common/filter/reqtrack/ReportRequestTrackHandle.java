package org.totoro.common.filter.reqtrack;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.totoro.common.json.JsonUtils;
import org.totoro.common.util.IpUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ReportRequestTrackHandle的默认实现
 */
@Slf4j
public class ReportRequestTrackHandle {

    private static final List<String> ignoreHeaders = Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Methods",
            "Access-Control-Max-Age", "Access-Control-Allow-Credentials", "Access-Control-Allow-Headers");

    public static StringBuilder buildArrivedReport(RequestTrack requestTrack) {
        StringBuilder sb = new StringBuilder(64);
        sb.append("requestArrive-").append(requestTrack.getUri());
        return sb;
    }

    public static StringBuilder buildLeavedReport(RequestTrack requestTrack,
                                           ContentCachingRequestWrapper contentCachingRequestWrapper,
                                           ContentCachingResponseWrapper contentCachingResponseWrapper) {
        long elapsed = Duration.between(requestTrack.getRequestArrivedAt(), LocalDateTime.now()).toMillis();
        Collection<String> curlLines = convertToCurlLines(requestTrack, contentCachingRequestWrapper);
        String curl = Joiner.on("\n\t").join(curlLines);
        Map<String, String> responseHeaders = getResponseHeaders(contentCachingResponseWrapper);
        String rawResponseBody = limitLength(getRawResponseBody(contentCachingResponseWrapper), 5000);

        StringBuilder sb = new StringBuilder(1024);
        sb.append("requestLeave-").append(requestTrack.getUri()).append("-")
                .append(elapsed).append("ms").append("\n");
        sb.append("HTTP Request").append("\n");
        sb.append("\t").append(curl).append("\n\n");
        sb.append("HTTP Response").append("\n");
        sb.append("\t").append("headers=").append(responseHeaders).append("\n");
        sb.append("\t").append("body=").append(rawResponseBody).append("\n\n");
        sb.append("Others").append("\n");
        sb.append("\t").append("ip=").append(IpUtils.getIp(contentCachingRequestWrapper)).append("\n");
        sb.append("\t").append("time=").append(elapsed).append("ms").append("\n");
        return sb;
    }

    public static StringBuilder buildMoreLeavedReport(Map<String, Object> more) {
        StringBuilder sb = new StringBuilder(64);
        more.forEach((k, v) -> sb.append("\t").append(k).append("=").append(v.toString()).append("\n"));
        return sb;
    }

    private static Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> responseHeaders = Maps.newHashMap();
        Collection<String> headerNames = response.getHeaderNames();
        if (headerNames != null) {
            for (String headerName : headerNames) {
                if (!ignoreHeaders.contains(headerName)) {
                    responseHeaders.put(headerName, response.getHeader(headerName));
                }
            }
        }
        return responseHeaders;
    }

    private static Collection<String> convertToCurlLines(RequestTrack requestTrack, ContentCachingRequestWrapper request) {
        String urlParams = getUrlParams(request);
        final Collection<String> result = Lists.newArrayList();
        result.add("curl --location --request " + requestTrack.getHttpMethod() + " '" + requestTrack.getFullUrl() + urlParams + "'");
        String rawJson = JsonUtils.compressJson(getRawRequestBody(request));
        result.add("--data-raw '" + rawJson + "'");
        getRequestHeaders(request).forEach((k, v) -> {
            if (StringUtils.equals(k, "content-length") || StringUtils.equals(k, "host")) {
                return;
            }
            result.add("--header '" + k + ": " + v + "'");
        });
        return result;
    }

    private static String getUrlParams(HttpServletRequest request) {
        Enumeration<String> enumeration = request.getParameterNames();
        StringJoiner sj = new StringJoiner("&", "?", "");
        while (enumeration.hasMoreElements()) {
            String parameter = enumeration.nextElement();
            sj.add(parameter + "=" + request.getParameter(parameter));
        }
        String urlParams = sj.toString();
        if ("?".equals(urlParams)) {
            return "";
        }

        return urlParams;
    }

    private static Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> requestHeaders = Maps.newHashMap();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                requestHeaders.put(headerName, request.getHeader(headerName));
            }
        }
        return requestHeaders;
    }

    private static String getRawRequestBody(ContentCachingRequestWrapper wrappedRequest) {
        try {
            // Request Body由客户端提供，所以编码从报文中获取
            String encoding = wrappedRequest.getCharacterEncoding();
            String result = IOUtils.toString(wrappedRequest.getContentAsByteArray(), encoding);
            if (StringUtils.isEmpty(result)) {
                // 报文有body，但handler没有@RequestBody时，wrappedRequest.getInputStream()会有内容
                result = IOUtils.toString(wrappedRequest.getInputStream(), encoding);
            }
            return result;
        } catch (IOException e) {
            log.error("读取request body失败", e);
            return "";
        }
    }

    private static String getRawResponseBody(ContentCachingResponseWrapper wrappedResponse) {
        try {
            // Response Body由服务端产生，所以编码固定是UTF-8
            String result = IOUtils.toString(wrappedResponse.getContentInputStream(), StandardCharsets.UTF_8);
            wrappedResponse.copyBodyToResponse();
            return result;
        } catch (IOException e) {
            log.error("读取response body失败", e);
            return "";
        }
    }

    private static String limitLength(String raw, int maxLength) {
        if (raw.length() > maxLength) {
            return raw.substring(0, maxLength) + " <hide beyond part>";
        }
        return raw;
    }

}