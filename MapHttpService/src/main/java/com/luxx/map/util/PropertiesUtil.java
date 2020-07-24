package com.luxx.map.util;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
    private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    private Properties prop = null;
    private static String propertiesFile = "/mapConfig.properties";

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

    public String GetListenPort() {
        String port = prop.getProperty("port");
        return port;
    }


    public int GetDbType() {
        String dbType = prop.getProperty("database.type");
        return Integer.valueOf(dbType);
    }

    public String GetSqliteDbPath() {
        String dbPath = prop.getProperty("database.sqlite.path");
        return dbPath;
    }

    public String GetMysqlUrl() {
        String url = prop.getProperty("database.mysql.url");
        return url;
    }

    public String GetMysqlUser() {
        String user = prop.getProperty("database.mysql.username");
        return user;
    }

    public String GetMysqlPassword() {
        String pswd = prop.getProperty("database.mysql.password");
        return pswd;
    }

}
