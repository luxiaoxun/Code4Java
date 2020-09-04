package com.luxx.index.executor;

import com.luxx.index.config.IndexConfig;
import com.luxx.index.model.PoiData;
import com.luxx.index.service.PoiIndexService;
import com.luxx.index.service.IndexDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "index.db.url")
public class PoiIndexExecutor extends DataIndexExecutor {
    private static Logger log = LogManager.getLogger(PoiIndexExecutor.class);

    @Autowired
    private PoiIndexService poiIndexService;

    @Autowired
    private IndexDataSource indexDataSource;

    @Autowired
    private IndexConfig indexConfig;

    // 读取数据分页
    private long pageNum = 0;
    private int pageCount = 1000;
    private String dataTableName;

    @PostConstruct
    public void init() {
        dataTableName = indexConfig.getDbTable();
    }

    @Override
    public void start() {
        log.info("Start index POI");
        try {
            poiIndexService.clear();

            Thread exportThread = new Thread(new Runnable() {
                public void run() {
                    boolean isRunning = true;
                    while (isRunning) {
                        List<PoiData> dataList = getDataFromDataBase();
                        if (dataList == null || dataList.isEmpty()) {
                            log.info("Index POI finished");
                            break;
                        }
                        int len = dataList.size();
                        poiIndexService.indexPoiDataList(dataList);
                        log.info("Index POI max id：" + dataList.get(len - 1).getId());
                    }
                }
            });
            exportThread.start();
        } catch (Exception ex) {
            log.error("Index POI exception：" + ex.toString());
        }
    }

    private List<PoiData> getDataFromDataBase() {
        List<PoiData> dataList = new ArrayList<>(pageCount);
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = indexDataSource.getConnection();
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


    @Override
    public void stop() {
        if (poiIndexService != null) {
            poiIndexService.close();
        }
    }
}
