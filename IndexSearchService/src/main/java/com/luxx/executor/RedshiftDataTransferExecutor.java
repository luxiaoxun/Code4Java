package com.luxx.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.luxx.model.EndpointData;
import com.luxx.service.RedshiftIndexService;
import com.luxx.util.DataSourceUtil;
import com.luxx.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedshiftDataTransferExecutor {
    private static Logger log = LogManager.getLogger(RedshiftDataTransferExecutor.class);

    private RedshiftIndexService indexService = new RedshiftIndexService();

    // 读取数据分页
    private long pageNum = 1;
    private int pageCount = 30000;
    private boolean isFinished = false;

    private String dataTableName;

    public RedshiftDataTransferExecutor() {
        this.dataTableName = PropertiesUtil.getInstance().getProperty("db.table");
    }

    public void start() {
        // log.info("Delete old index...");
        //indexService.deleteIndex();

        log.info("Create new index...");
        indexService.createIndex();

        log.info("Create new index type mapping...");
        indexService.defineIndexTypeMapping();

        log.info("Start index ...");
        Thread exportThread = new Thread(new Runnable() {
            public void run() {
                while (!isFinished) {
                    List<EndpointData> dataList = getDataFromOldDataBase();
                    if (dataList != null && !dataList.isEmpty()) {
                        indexService.indexHotSpotDataListForRedshift(dataList);
                        log.info(String.format("Index data complete %s pages", pageNum));
                    }
                }
                log.info("Index complete ...");
            }
        });
        exportThread.start();
    }

    private List<EndpointData> getDataFromOldDataBase() {
        List<EndpointData> dataList = new ArrayList<EndpointData>(pageCount);
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = DataSourceUtil.getConnection();
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
