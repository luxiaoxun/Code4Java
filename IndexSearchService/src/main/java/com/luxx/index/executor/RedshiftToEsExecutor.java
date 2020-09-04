package com.luxx.index.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.luxx.index.config.IndexConfig;
import com.luxx.index.model.EndpointData;
import com.luxx.index.service.RedshiftIndexService;
import com.luxx.index.service.IndexDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "index.es.cluster.address")
public class RedshiftToEsExecutor extends DataIndexExecutor {
    private static Logger log = LogManager.getLogger(RedshiftToEsExecutor.class);

    @Autowired
    private RedshiftIndexService indexService;

    @Autowired
    private IndexDataSource indexDataSource;

    @Autowired
    private IndexConfig indexConfig;

    // 读取数据分页
    private long pageNum = 1;
    private int pageCount = 30000;
    private volatile boolean isFinished = false;
    private String dataTableName;

    @PostConstruct
    public void init() {
        dataTableName = indexConfig.getDbTable();
    }

    @Override
    public void start() {
        // log.info("Delete old index");
        //indexService.deleteIndex();

        log.info("Create new index");
        indexService.createIndex();

        log.info("Create new index type mapping");
        indexService.defineIndexTypeMapping();

        log.info("Start index redshift data to ES");
        Thread exportThread = new Thread(new Runnable() {
            public void run() {
                while (!isFinished) {
                    List<EndpointData> dataList = getDataFromDataBase();
                    if (dataList != null && !dataList.isEmpty()) {
                        indexService.indexHotSpotDataListForRedshift(dataList);
                        log.info(String.format("Index data complete %s pages", pageNum));
                    }
                }
                log.info("Index redshift data to ES complete");
            }
        });
        exportThread.start();
    }

    private List<EndpointData> getDataFromDataBase() {
        List<EndpointData> dataList = new ArrayList<EndpointData>(pageCount);
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = indexDataSource.getConnection();
            stm = dbConnection.createStatement();
            ++pageNum;
            long startNum = (pageNum - 1) * pageCount;
            log.info(String.format("Geting data from database, offset %s limit %s", startNum, pageCount));
            res = stm.executeQuery(
                    String.format("SELECT * FROM %s where date_time > '2017-03-13 16:00:00' OFFSET %s LIMIT %s",
                            dataTableName, startNum, pageCount));
            if (res.next()) {
                do {
                    EndpointData data = new EndpointData();
                    data.setOrg_id(Integer.parseInt(res.getString("org_id")));
                    data.setEndpoint_id(Long.parseLong(res.getString("endpoint_id")));

                    data.setDs_bytes(Long.parseLong(res.getString("ds_bytes")));
                    data.setDs_max_bytes(Double.parseDouble(res.getString("ds_max_bps")));
                    data.setDs_avg_bytes(Double.parseDouble(res.getString("ds_avg_bps")));
                    data.setDs_mwt(Integer.parseInt(res.getString("ds_mwt")));

                    data.setUs_bytes(Long.parseLong(res.getString("us_bytes")));
                    data.setUs_max_bytes(Double.parseDouble(res.getString("us_max_bps")));
                    data.setUs_avg_bytes(Double.parseDouble(res.getString("us_avg_bps")));
                    data.setUs_mwt(Integer.parseInt(res.getString("us_mwt")));

                    data.setDate_time(res.getDate("date_time"));

                    dataList.add(data);
                } while (res.next());
            } else {
                isFinished = true;
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
        indexService.close();
    }
}