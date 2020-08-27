package com.luxx.index.executor;

import com.luxx.index.model.PoiData;
import com.luxx.index.service.PoiIndexService;
import com.luxx.index.util.DataSourceUtil;
import com.luxx.index.util.PropertiesUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PoiIndexExecutor {
    private static Logger log = LogManager.getLogger(PoiIndexExecutor.class);

    private PoiIndexService indexService;

    // 读取数据分页
    private long pageNum = 0;
    private int pageCount = 1000;

    private String dataTableName;

    public PoiIndexExecutor() {
        this.dataTableName = PropertiesUtil.getInstance().getProperty("db.table");
    }

    public void start() {
        log.info("Start index POI");
        try {
            indexService = new PoiIndexService();
            indexService.clear();

            Thread exportThread = new Thread(new Runnable() {
                public void run() {
                    boolean isRunning = true;
                    while (isRunning) {
                        List<PoiData> dataList = getDataFromOldDataBase();
                        if (dataList == null || dataList.isEmpty()) {
                            log.info("Index POI finished");
                            break;
                        }
                        int len = dataList.size();
                        indexService.indexPoiDataList(dataList);
                        log.info("Index POI max id：" + dataList.get(len - 1).getId());
                    }
                }
            });
            exportThread.start();
        } catch (Exception ex) {
            log.error("Index POI exception：" + ex.toString());
        }
    }

    private List<PoiData> getDataFromOldDataBase() {
        List<PoiData> dataList = new ArrayList<>(pageCount);
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = DataSourceUtil.getConnection();
            if (dbConnection != null) {
                stm = dbConnection.createStatement();
                ++pageNum;
                long startNum = (pageNum - 1) * pageCount;
                res = stm.executeQuery(String.format(
                        "SELECT * FROM %s LIMIT %s,%s ", dataTableName,
                        startNum, pageCount));
                if (res != null) {
                    while (res.next()) {
                        PoiData data = new PoiData();
                        data.setId(res.getInt("id"));
                        data.setAddress(res.getString("address"));
                        data.setLat(res.getDouble("latitude"));
                        data.setLng(res.getDouble("longitude"));
                        dataList.add(data);
                    }
                }
            } else {
                log.error("Get connection fail");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.toString());
        } finally {
            attemptClose(res);
            attemptClose(stm);
            attemptClose(dbConnection);
        }

        return dataList;
    }

    private void attemptClose(ResultSet o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

    private void attemptClose(Statement o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

    private void attemptClose(Connection o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

}
