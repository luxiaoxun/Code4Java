package com.map.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

public class JsonHelper {

	private static ObjectMapper objMapper = new ObjectMapper();

	static {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		objMapper.setDateFormat(dateFormat);
	}

	public static <type> type jsonToObject(String json, Class<?> cls) {
		type obj = null;
		JavaType javaType = objMapper.getTypeFactory().constructType(cls);
		objMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			obj = objMapper.readValue(json, javaType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static <type> type jsonToObjectList(String json,
			Class<?> collectionClass, Class<?>... elementClass) {
		type obj = null;
		JavaType javaType = objMapper.getTypeFactory().constructParametricType(
				collectionClass, elementClass);
		objMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			obj = objMapper.readValue(json, javaType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static <type> type jsonToObjectHashMap(String json,
			Class<?> keyClass, Class<?> valueClass) {
		type obj = null;
		JavaType javaType = objMapper.getTypeFactory().constructParametricType(
				HashMap.class, keyClass, valueClass);
		objMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
		// System.out.println(json);
		return json;
	}

}
