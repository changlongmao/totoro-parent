package org.totoro.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
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
        ObjectMapper om = new ObjectMapper();

        // 反序列化时，忽略Javabean中Collection属性对应JSON Array中的为null的元素
        om.registerModule(new IgnoreCollectionNullElementDeserializeModule());

        // 反序列化时，忽略Javabean中不存在的属性，而不是抛出异常
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略入参没有任何属性导致的序列化报错
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return om;
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("Never instantiate me.");
    }

    /**
     * 将对象转化为JSON
     */
    public static String toJson(Object object) {
        return toJson(object, om);
    }

    /**
     * 将对象转化为JSON
     */
    public static String toJson(Object object, ObjectMapper om) {
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
        return toJsonPrettily(object, om);
    }

    /**
     * 将对象转化为JSON，结果是美化的
     */
    public static String toJsonPrettily(Object object, ObjectMapper om) {
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
        return toObject(json, clazz, om);
    }

    /**
     * 将JSON转化为对象
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static <T> T toObject(String json, Class<T> clazz, ObjectMapper om) {
        try {
            return om.readValue(json, clazz);
        } catch (IOException e) {
            log.error("json={}, clazz={}", json, clazz, e);
            throw new JsonException(e);
        }
    }

    /**
     * 将JSON转化为对象列表
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static <T> List<T> toListOfObject(String json, Class<T> clazz) {
        return toListOfObject(json, clazz, om);
    }

    /**
     * 将JSON转化为对象列表
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */

    public static <T> List<T> toListOfObject(String json, Class<T> clazz, ObjectMapper om) {
        try {
            @SuppressWarnings("unchecked") Class<T[]> arrayClass = (Class<T[]>) Class
                    .forName("[L" + clazz.getName() + ";");
            return Lists.newArrayList(om.readValue(json, arrayClass));
        } catch (IOException | ClassNotFoundException e) {
            log.error("json={}, clazz={}", json, clazz, e);
            throw new JsonException(e);
        }
    }

    /**
     * JSON -> 参数化的对象
     * <p>
     * 示例： Collection<<User<UserAddress>> users = JsonUtils.toParameterizedObject(text);
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static <T> T toParameterizedObject(String json, TypeReference<T> typeReference) {
        return toParameterizedObject(json, typeReference, om);
    }

    /**
     * JSON -> 参数化的对象
     * <p>
     * 示例： Collection<<User<UserAddress>> users = JsonUtils.toParameterizedObject(text);
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static <T> T toParameterizedObject(String json, TypeReference<T> typeReference, ObjectMapper om) {
        try {
            return om.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("json={}, typeReference={}", json, typeReference, e);
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
        return toTree(json, om);
    }

    /**
     * JSON -> JsonNode对象
     *
     * <strong>除非JSON对应数据结构在运行时是变化的，否则不建议使这个方法</strong>
     *
     * @throws JsonException 任何原因转化失败时，抛出这个异常，如果需要补偿处理，可以进行捕获
     */
    public static JsonNode toTree(String json, ObjectMapper om) {
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
