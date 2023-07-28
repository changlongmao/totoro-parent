package org.totoro.common.filter.aes;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * HttpServletRequest包装类
 * @author ChangLF 2023-05-24
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    private String requestBody;
    private ServletInputStream inputStream;
    private BufferedReader reader;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.inputStream = request.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestBody = getRequestBodyFromReader();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return reader;
    }

    @Override
    public ServletInputStream getInputStream() {
        return inputStream;
    }

    public String getRequestBody() {
        return this.requestBody;
    }


    private String getRequestBodyFromReader() {
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void setRequestBody(String newRequestBody) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));
            writer.write(newRequestBody);
            writer.flush();

            byte[] bytes = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            setInputStream(new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    // Not implemented
                }
            });

            setReader(new BufferedReader(new InputStreamReader(byteArrayInputStream)));

            this.requestBody = newRequestBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setInputStream(ServletInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private void setReader(BufferedReader reader) {
        this.reader = reader;
    }
}
