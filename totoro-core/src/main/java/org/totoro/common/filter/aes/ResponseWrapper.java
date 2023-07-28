package org.totoro.common.filter.aes;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * HttpServletResponse包装类
 * @author ChangLF 2023-05-24
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private final ServletOutputStream servletOutputStream;

    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        byteArrayOutputStream = new ByteArrayOutputStream();
        servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }

            @Override
            public void flush() throws IOException {
                byteArrayOutputStream.flush();
            }

            @Override
            public void close() throws IOException {
                byteArrayOutputStream.close();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // Not implemented
            }
        };
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(byteArrayOutputStream);
    }

    @Override
    public void setContentLength(int len) {
        // 不设置 Content-Length 头，让 Servlet 容器自动计算
    }

    public String getResponseBody() {
        return byteArrayOutputStream.toString();
    }
}
