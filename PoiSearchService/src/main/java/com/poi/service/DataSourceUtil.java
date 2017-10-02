package com.poi.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceUtil{

    private static HikariDataSource dataSource;

    private DataSourceUtil(){
    }

    static{
        //String dbname = PropertiesUtil.getInstance().getProperty("mysql.database");
        String mysqlUrl = PropertiesUtil.getInstance().getProperty("mysql.url");
        
		String user = PropertiesUtil.getInstance().getProperty("mysql.username");
		String pswd = PropertiesUtil.getInstance().getProperty("mysql.password");
		
        HikariConfig config = new HikariConfig();
		config.setJdbcUrl(mysqlUrl);
		config.setUsername(user);
		config.setPassword(pswd);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(32);
		dataSource = new HikariDataSource(config);        
    }

    public static Connection getConnection() throws SQLException{
        if (dataSource != null){
            return dataSource.getConnection();
        }
        else{
            return null;
        }
    }

}
