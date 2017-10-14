package com.service.map;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import org.slf4j.Logger;

public class PropertiesUtil {
	
	private static Logger log = org.slf4j.LoggerFactory
			.getLogger(PropertiesUtil.class);
	
	private Properties prop = null;
	private static String propertiesFile = "/mapConfig.properties";
	
	private PropertiesUtil(){
//		String path = null;
//		try {
//			path = URLDecoder.decode(this.getClass().getClassLoader().getResource("").toString(), "utf-8");
//			if(System.getProperty("os.name").indexOf("Linux")>=0){
//				path = path.substring(5);
//			}
//			else{
//				path = path.substring(6);
//			}
//			path += "mapConfig.properties";
//		} catch (UnsupportedEncodingException e) {
//			log.error(e.toString());
//		}
//		log.debug("[mapConfig.properties] path: "+path);
//
//		prop = new Properties();
//        try
//        {
//            prop.load(new FileInputStream(path));
//        }
//        catch (Exception e)
//        {
//        	log.error(e.toString());
//            throw new IllegalArgumentException(
//                "[mapConfig.properties] is not found!");
//        }
		prop = new Properties(System.getProperties());
		try {
			InputStream propFile = PropertiesUtil.class.getResourceAsStream(propertiesFile);
			prop.load(propFile);
		} catch (Exception e) {
			log.error("Load properties file " + propertiesFile + " failed. " + e.getMessage());
		}
	}
	
	private static class PropertiesUtilHolder{
		static final PropertiesUtil instance = new PropertiesUtil();
	}
	
	public static PropertiesUtil getInstance(){
		return PropertiesUtilHolder.instance;
	}
	
	public String GetListenPort(){
		String port = prop.getProperty("listenPort");
		return port;
	}
	
	
	public String GetDbType(){
		String dbType = prop.getProperty("database.dbType");
		return dbType;
	}

	public String GetSqliteDbPath() {
		String dbPath = prop.getProperty("database.Sqlite.SqliteDbPath");
		return dbPath;
	}
	
	public String GetMysqlUrl(){
		String url = prop.getProperty("database.Mysql.Url");
		return url;
	}
	
	public String GetMysqlUser(){
		String user = prop.getProperty("database.Mysql.User");
		return user;
	}
	
	public String GetMysqlPassword(){
		String pswd = prop.getProperty("database.Mysql.Password");
		return pswd;
	}

}
