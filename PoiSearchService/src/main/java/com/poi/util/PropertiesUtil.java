package com.poi.util;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import org.slf4j.Logger;

public class PropertiesUtil {

	private static Logger log = org.slf4j.LoggerFactory
			.getLogger(PropertiesUtil.class);
	
	private Properties prop = null;

	private PropertiesUtil() {
		String path = null;
		try {
			path = URLDecoder.decode(this.getClass().getClassLoader()
					.getResource("").toString(), "utf-8");
			if (System.getProperty("os.name").indexOf("Linux") >= 0) {
				path = path.substring(5);
			} else {
				path = path.substring(6);
			}
			path += "config.properties";
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
		}
		log.debug("[config.properties] path: " + path);

		prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
		} catch (Exception e) {
			log.error(e.toString());
			throw new IllegalArgumentException(
					"[config.properties] is not found!");
		}
	}

	private static class PropertiesUtilHolder {
		static final PropertiesUtil instance = new PropertiesUtil();
	}

	public static PropertiesUtil getInstance() {
		return PropertiesUtilHolder.instance;
	}

	public String getProperty(String key) {
		String value = prop.getProperty(key);
		return value;
	}

}
