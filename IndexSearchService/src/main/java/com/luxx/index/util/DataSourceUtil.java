package com.luxx.index.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceUtil {

    private static final Long HIKARI_MAX_LIFE_TIME_MS = 1800000L; // 30 min
    private static final Long HIKARI_LEAK_DETECTION_THRESHOLD_MS = 60000L;
    private static final int HIKARI_POOL_SIZE = 5;

    private static HikariDataSource dataSource = null;

    private DataSourceUtil() {
    }

    static {
        String dbUrl = PropertiesUtil.getInstance().getProperty("db.url");
        String username = PropertiesUtil.getInstance().getProperty("db.username");
        String password = PropertiesUtil.getInstance().getProperty("db.password");
        String dbType = PropertiesUtil.getInstance().getProperty("db.type");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);
        if (dbType.equals("redshift")) {
            config.setDriverClassName("com.amazon.redshift.jdbc.Driver");
        } else if (dbType.equals("mysql")) {
            config.setDriverClassName("com.mysql.jdbc.Driver");
        }
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaxLifetime(HIKARI_MAX_LIFE_TIME_MS);
        config.setMaximumPoolSize(HIKARI_POOL_SIZE);
        config.setMinimumIdle(HIKARI_POOL_SIZE);
        config.setLeakDetectionThreshold(HIKARI_LEAK_DETECTION_THRESHOLD_MS);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            return null;
        }
    }
}
