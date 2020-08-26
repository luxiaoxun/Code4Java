package com.luxx.index.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static ObjectMapper objMapper = new ObjectMapper();

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objMapper.setDateFormat(dateFormat);
    }

    public static <type> type jsonToObject(String json, Class<?> cls) {
        type obj = null;
        JavaType javaType = objMapper.getTypeFactory().constructType(cls);
        objMapper.configure(Feature.ALLOW_MISSING_VALUES, true);
        try {
            obj = objMapper.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <type> type jsonToObjectList(String json, Class<?> collectionClass, Class<?>... elementClass) {
        type obj = null;
        JavaType javaType = objMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
        objMapper.configure(Feature.ALLOW_MISSING_VALUES, true);
        try {
            obj = objMapper.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <type> type jsonToObjectHashMap(String json, Class<?> keyClass, Class<?> valueClass) {
        type obj = null;
        JavaType javaType = objMapper.getTypeFactory().constructParametricType(HashMap.class, keyClass, valueClass);
        objMapper.configure(Feature.ALLOW_MISSING_VALUES, true);
        try {
            obj = objMapper.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String objectToJson(Object o) {
        String json = "";
        try {
            json = objMapper.writeValueAsString(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

}
