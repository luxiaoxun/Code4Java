package com.luxx.gis.executor;

import com.luxx.gis.config.IndexConfig;
import com.luxx.gis.model.PoiData;
import com.luxx.gis.service.IndexDataSource;
import com.luxx.gis.service.PoiIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@ConditionalOnProperty(name = "index.db.url")
public class PoiIndexExecutor extends IndexExecutor {
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
            poiIndexService.createIndexMapping();
            poiIndexService.createIndex();

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
                        int id = res.getInt("id");
                        String address = res.getString("address");
                        double lat = res.getDouble("latitude");
                        double lng = res.getDouble("longitude");
                        PoiData data = new PoiData(id, address, lat, lng);
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
    }
}
