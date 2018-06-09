package com.luxx.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesReader {

    private static Logger log = LogManager.getLogger(PropertiesReader.class);

    private Properties prop = null;

    public PropertiesReader(String propertiesName) {
        prop = new Properties(System.getProperties());
        try {
            InputStream propFile = PropertiesUtil.class.getResourceAsStream(propertiesName);
            prop.load(propFile);
        } catch (Exception e) {
            log.error("Load properties file " + propertiesName + " failed. " + e.getMessage());
        }
    }

    public String getProperty(String key) {
        String value = prop.getProperty(key);
        return value;
    }

}
