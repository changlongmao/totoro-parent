package org.totoro.common.util;

import lombok.extern.slf4j.Slf4j;
import org.totoro.common.json.JsonUtils;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * okHttp工具类
 * @author ChangLF 2022-03-02
 */
@Slf4j
@Component
public class OkHttpTemplate {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;

    public OkHttpTemplate(ExecutorService executorService){
        client = new OkHttpClient.Builder()
                // 使用当前服务的线程池
                .dispatcher(new Dispatcher(executorService))
                // 连接池参数
                .connectionPool(new ConnectionPool(100, 60, TimeUnit.SECONDS))
                // 连接超时时间
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    /**
     * 发送get请求
     * @param url url
     * @param params 参数
     * @return 响应结果
     */
    public String doGet(String url, Map<String, Object> params) {
        return doGet(url, params, null);
    }


    /**
     * 发送get请求
     * @param url url
     * @param params 参数
     * @param headers 请求头
     * @return 响应结果
     */
    public String doGet(String url, Map<String, Object> params, Map<String, String> headers) {

        Headers.Builder builder = new Headers.Builder();
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(builder::add);
        }
        if (!CollectionUtils.isEmpty(params)) {
            StringJoiner sj = new StringJoiner("&", "?", "");
            params.forEach((k,v) -> sj.add(k + "=" + v));
            url += sj.toString();
        }
        log.info("request 最终地址：" + url);
        Request request = new Request.Builder()
                .url(url)
                .headers(builder.build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("发送Get请求失败", e);
        }
        return null;
    }

    /**
     * 发送post请求
     * @param url url
     * @param body 请求体，若不为String则转为json
     * @return 响应结果
     */
    public String doPost(String url, Object body) {
        return doPost(url, body instanceof String ? (String) body : JsonUtils.toJson(body), null);
    }

    /**
     * 发送post请求
     * @param url url
     * @param json json格式参数
     * @return 响应结果
     */
    public String doPost(String url, String json) {
        return doPost(url, json, null);
    }

    /**
     * 发送post请求
     * @param url url
     * @param json json格式参数
     * @param headers 请求头
     * @return 响应结果
     */
    public String doPost(String url, String json, Map<String, String> headers) {
        RequestBody body = RequestBody.create(JSON, json);

        Headers.Builder builder = new Headers.Builder();
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(builder::add);
        }

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(builder.build())
                .build();
        log.info("request 最终地址：" + url + " 入参：" + json);
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("发送Post请求失败", e);
        }
        return null;
    }

}
