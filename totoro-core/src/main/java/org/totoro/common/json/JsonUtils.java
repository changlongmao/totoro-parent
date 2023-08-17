package org.totoro.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.totoro.common.exception.JsonException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 *
 * <pre>
 * 支持JSON与对象间的互相转换
 * </pre>
 *
 * @author changlf 2023-05-16
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper om = createObjectMapper();

    public static ObjectMapper createObjectMapper() {
        return JsonMapper.builder()
                // 反序列化时，忽略Javabean中Collection属性对应JSON Array中的为null的元素
                .addModule(new IgnoreCollectionNullElementDeserializeModule())
                // 忽略字段名的大小写
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                // 反序列化时，忽略Javabean中不存在的属性，而不是抛出异常
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 忽略入参没有任何属性导致的序列化报错
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("Never instantiate me.");
    }

    /**
     * 将对象转化为JSON
     */
    public static String toJson(Object object) {
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("object={}", object, e);
            throw new JsonException(e);
        }
    }

    /**
     * 将对象转化为JSON，结果是美化的
     */
    public static String toJsonPrettily(Object object) {
        try {
            return om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("object={}", object, e);
            throw new JsonException("转化JSON失败");
        }
    }

    /**
     * 压缩JSON（去除美化JSON中多余的换行与空格，如果参数字符串不是一个JSON，则无事发生）
     */
    public static String compressJson(String json) {
        try {
            Map<?, ?> map = om.readValue(json, Map.class);
            return toJson(map);
        } catch (Exception e) {
            // is not a json
            return json;
        }
    }

    /**
     * 将JSON转化为对象
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return om.readValue(json, clazz);
        } catch (IOException e) {
            log.error("json={}, clazz={}", json, clazz, e);
            throw new JsonException(e);
        }
    }

    /**
     * 将JSON转化为对象, 支持泛型
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static <T> T toObject(String json, TypeReference<T> valueTypeRef) {
        try {
            return om.readValue(json, valueTypeRef);
        } catch (IOException e) {
            log.error("json={}, valueTypeRef={}", json, valueTypeRef, e);
            throw new JsonException(e);
        }
    }

    /**
     * 将JSON转化为对象列表
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static <T> List<T> toListOfObject(String json, Class<T> clazz) {
        try {
            return om.readValue(json, new TypeReference<List<T>>(){});
        } catch (IOException e) {
            log.error("json={}, clazz={}", json, clazz, e);
            throw new JsonException(e);
        }
    }

    /**
     * JSON -> JsonNode对象
     *
     * <strong>除非JSON对应数据结构在运行时是变化的，否则不建议使这个方法</strong>
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static JsonNode toTree(String json) {
        try {
            return om.readTree(json);
        } catch (IOException e) {
            log.error("json={}", json, e);
            throw new JsonException(e);
        }
    }

    /**
     * 获取JsonNode中的所有value值
     * @param node 通过{@link JsonUtils#toTree(String)}获取的JsonNode
	 * @param result 拼接的结果，入参时传初始空对象
     * @author ChangLF 2023/7/10 17:21
     **/
    public static void parseNodeValue(JsonNode node, StringBuilder result) {
        if (node.isValueNode()) {
            if (node.isTextual()) {
                result.append(node.textValue());
            } else {
                result.append(node);
            }
        }

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                parseNodeValue(entry.getValue(), result);
            }
        }

        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                parseNodeValue(jsonNode, result);
            }
        }
    }

}
