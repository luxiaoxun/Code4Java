package com.luxx.index.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.luxx.index.config.IndexConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@ConditionalOnProperty(name = "index.db.url")
public class IndexDataSource {
    private static final Long HIKARI_MAX_LIFE_TIME_MS = 1800000L; // 30 min
    private static final Long HIKARI_LEAK_DETECTION_THRESHOLD_MS = 60000L;
    private static final int HIKARI_POOL_SIZE = 5;
    private static HikariDataSource dataSource = null;

    @Autowired
    private IndexConfig indexConfig;

    @PostConstruct
    public void init() {
        String dbUrl = indexConfig.getDbUrl();
        String username = indexConfig.getDbUserName();
        String password = indexConfig.getDbPassword();
        String dbType = indexConfig.getDbType();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);
        if (dbType.equals("redshift")) {
            config.setDriverClassName("com.amazon.redshift.jdbc.Driver");
        } else if (dbType.equals("mysql")) {
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
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

    public Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            return null;
        }
    }
}
