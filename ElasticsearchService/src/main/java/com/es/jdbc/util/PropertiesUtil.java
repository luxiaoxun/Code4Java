package com.es.jdbc.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesUtil {

    private static Logger log = LogManager.getLogger(PropertiesUtil.class);
    private Properties prop = null;
    private static String propertiesFile = "/config.properties";

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

}
