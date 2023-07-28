package org.totoro.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author changlf 2023-05-16
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = JsonUtils.createObjectMapper();
        // 反序列化时，忽略Javabean中Collection属性对应JSON Array中的为null的元素
        om.registerModule(new IgnoreCollectionNullElementDeserializeModule());
        // 反序列化时，trim所有字符串
        om.registerModule(stringTrimModule());
        return om;
    }

    private SimpleModule stringTrimModule() {
        SimpleModule stringTrimModule = new SimpleModule();
        stringTrimModule.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            private static final long serialVersionUID = 1487233789902579884L;

            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
                return StringUtils.trimWhitespace(jsonParser.getValueAsString());
            }
        });
        return stringTrimModule;
    }

}