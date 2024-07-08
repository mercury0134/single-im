package org.mercury.im.common.json;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setTimeZone(TimeZone.getDefault()); // 设置默认时区为当前系统时区
        objectMapper.setLocale(Locale.getDefault()); // 设置默认本地化环境为当前系统环境
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用将日期序列化为时间戳的功能
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS); // 禁用将持续时间序列化为时间戳的功能
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); // 禁用在序列化空对象时抛出异常的功能
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 禁用在反序列化未知属性时抛出异常的功能
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // 禁用在序列化空对象时抛出异常的功能
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> T parse(String jsonText, Class<T> specificClass) {
        if (StrUtil.isBlank(jsonText)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonText, specificClass);
        } catch (JsonProcessingException e) {
            log.error("json:{} parser error" + e, jsonText);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(Reader jsonText, Class<T> specificClass) {
        try {
            return objectMapper.readValue(jsonText, specificClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String toJsonString(T object) {
        if (null == object) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("object:{} tostring error" + e, object);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseList(String jsonText) {
        if (StrUtil.isBlank(jsonText)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonText, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("json:{} parser list error" + e, jsonText);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseList(String jsonText, TypeReference<List<T>> tTypeReference) {
        if (StrUtil.isBlank(jsonText)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonText, tTypeReference);
        } catch (JsonProcessingException e) {
            log.error("json:{} parser list error" + e, jsonText);
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> toMapStr(Object object) {
        return objectMapper.convertValue(object,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
    }

    public static Map<String, Object> toMapObject(Object object) {
        return objectMapper.convertValue(object,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
    }

    public static <T> T mapToObject(Map<String, String> map, Class<T> valueType) {
        return objectMapper.convertValue(map, valueType);
    }

    public static Map<String, String> toMapStr(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("json:{} parser error" + e, json);
            throw new RuntimeException(e);
        }
    }
}
