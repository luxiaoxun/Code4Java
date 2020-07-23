package com.luxx.map.service;

import com.luxx.map.util.PropertiesUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map服务，数据库操作
 *
 * @author luxiaoxun
 * @version 1.0
 * @since 2015.06.1
 */
public class MapDbOperation {
    private static Logger log = LoggerFactory.getLogger(MapDbOperation.class);

    //private static ComboPooledDataSource pooledDs;
    private static String dbType;
    private static String sqliteDbPath;
    private static HikariDataSource dataSource;

    public static boolean init() throws ClassNotFoundException, SQLException {
        try {
            //dbType = DBXMLConfiguration.GetInstance().GetDbType();
            dbType = PropertiesUtil.getInstance().GetDbType();
            if (dbType.equals("0")) {
                sqliteDbPath = PropertiesUtil.getInstance().GetSqliteDbPath();
                Class.forName("org.sqlite.JDBC");
                return true;
            } else if (dbType.equals("1")) {
                String url = PropertiesUtil.getInstance().GetMysqlUrl();
                String user = PropertiesUtil.getInstance().GetMysqlUser();
                String pswd = PropertiesUtil.getInstance().GetMysqlPassword();

                HikariConfig config = new HikariConfig();
                //config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
                config.setJdbcUrl(url);
                config.setUsername(user);
                config.setPassword(pswd);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.setMaximumPoolSize(32);
                dataSource = new HikariDataSource(config);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    public static byte[] getTile(int x, int y, int zoom, String dbId) {
        try {
            if (dbType.equals("0")) {
                return getTileFromSqlite(x, y, zoom, dbId);
            }
            if (dbType.equals("1")) {
                return getTileFromMySql(x, y, zoom, dbId);
            } else {
                return null;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    public static byte[] getTileFromSqlite(int x, int y, int zoom,
                                           String dbId) throws SQLException {
        byte[] allBytesInBlob = null;
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:"
                    + sqliteDbPath);
            stm = dbConnection.createStatement();
            res = stm
                    .executeQuery(String
                            .format("select Tile from TilesData where id = (select id from Tiles where X = %s and Y = %s and Zoom = %s and Type = %s)",
                                    x, y, zoom, dbId));
            if (res != null) {
                if (res.next()) {
                    allBytesInBlob = res.getBytes("Tile");
                }
            }
        } finally {
            attemptClose(res);
            attemptClose(stm);
            attemptClose(dbConnection);
        }
        return allBytesInBlob;
    }

    public static byte[] getTileFromMySql(int x, int y, int zoom,
                                          String dbId) throws SQLException {
        byte[] allBytesInBlob = null;
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = dataSource.getConnection();
            stm = dbConnection.createStatement();
            res = stm
                    .executeQuery(String
                            .format("select Tile from gmapnetcache where X = %s and Y = %s and Zoom = %s and Type = %s",
                                    x, y, zoom, dbId));
            if (res != null) {
                if (res.next()) {
                    allBytesInBlob = res.getBytes("Tile");
                }
            }
        } finally {
            attemptClose(res);
            attemptClose(stm);
            attemptClose(dbConnection);
        }

        return allBytesInBlob;
    }

    private static void attemptClose(ResultSet o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

    private static void attemptClose(Statement o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

    private static void attemptClose(Connection o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }
}
