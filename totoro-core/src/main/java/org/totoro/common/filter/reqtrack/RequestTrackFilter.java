package org.totoro.common.filter.reqtrack;

import lombok.extern.slf4j.Slf4j;
import org.totoro.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 请求轨迹过滤器
 *
 * @author changlf 2023/5/16
 */
@Slf4j
public class RequestTrackFilter implements Filter {

    private String excludedUris;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludedUris = filterConfig.getInitParameter("excludedUris");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (StringUtil.isExcludedUri(excludedUris, request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        // 构造RequestTrack对象，保存到上下文
        RequestTrack.RequestTrackBuilder builder = RequestTrack.builder();
        builder.traceId(getTrackId(request));
        builder.requestArrivedAt(LocalDateTime.now());
        builder.httpMethod(request.getMethod());
        builder.uri(request.getRequestURI());
        builder.fullUrl(request.getRequestURL().toString());
        builder.rawRequest(request);
        builder.rawResponse(response);
        RequestTrack requestTrack = builder.build();
        RequestTrack.setCurrent(requestTrack);

        // 设置Log MDC
        MDC.put(RequestTrack.TRACE_NAME, requestTrack.getTraceId());

        // 打印请求到达时报告
        log.info(ReportRequestTrackHandle.buildArrivedReport(requestTrack).toString());

        // 包装request和response（如果需要打印请求离开时报告的话）
        request = new ContentCachingRequestWrapper(request);
        response = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // insignia保存到response报文
            response.setHeader(RequestTrack.TRACE_NAME, requestTrack.getTraceId());

            // 打印请求离开时报告
            StringBuilder report = ReportRequestTrackHandle.buildLeavedReport(requestTrack,
                    (ContentCachingRequestWrapper) request, (ContentCachingResponseWrapper) response);
            StringBuilder moreReport = ReportRequestTrackHandle.buildMoreLeavedReport(requestTrack.getMore());
            if (!"/actuator/health".equals(requestTrack.getUri())) {
                log.info(report.append(moreReport).toString());
            }

            // 清空上下文
            MDC.remove(RequestTrack.TRACE_NAME);
            RequestTrack.removeCurrent();
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public String getTrackId(HttpServletRequest request) {
        String traceId = request.getHeader(RequestTrack.TRACE_NAME);
        if (StringUtils.isEmpty(traceId)) {
            traceId = StringUtil.getUUID();
        }
        return traceId;
    }
}
