package org.totoro.common.filter.xss;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class XssFilter implements Filter {
    private String[] excludedPageArray = new String[]{"/service/model/"};

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);

        boolean isExcludedPage = false;

        for (String page : excludedPageArray) {
            if (((HttpServletRequest) request).getServletPath().indexOf(page) > -1) {
                isExcludedPage = true;
                break;
            }
        }

        if (isExcludedPage) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(xssRequest, response);
        }
    }

    @Override
    public void destroy() {
    }

}
