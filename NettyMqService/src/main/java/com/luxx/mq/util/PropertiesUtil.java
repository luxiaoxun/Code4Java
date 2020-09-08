package com.luxx.mq.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final String propertiesFile = "/config.properties";
    private Properties prop;

    private PropertiesUtil() {
        prop = new Properties(System.getProperties());
        try {
            InputStream propFile = PropertiesUtil.class.getResourceAsStream(propertiesFile);
            prop.load(propFile);
        } catch (Exception e) {
            log.error("Load properties file " + propertiesFile + " failed. " + e.getMessage());
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

    public Integer getPropertyAsInt(String key) {
        String value = prop.getProperty(key);
        return Integer.valueOf(value);
    }
}
