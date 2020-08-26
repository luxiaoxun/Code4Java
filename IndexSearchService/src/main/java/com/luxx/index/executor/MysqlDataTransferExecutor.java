package com.luxx.index.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.luxx.index.model.HotspotData;
import com.luxx.index.service.MysqlIndexService;
import com.luxx.index.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luxx.index.util.DataSourceUtil;

public class MysqlDataTransferExecutor {
    private static Logger log = LogManager.getLogger(MysqlDataTransferExecutor.class);

    private MysqlIndexService indexService = new MysqlIndexService();

    // 读取数据分页
    private long pageNum = 0;
    private int pageCount = 30000;
    private boolean isFinished = false;

    private String dataTableName;

    public MysqlDataTransferExecutor() {
        this.dataTableName = PropertiesUtil.getInstance().getProperty("db.table");
    }

    public void start() {

        log.info("Start index ...");

        //indexService.deleteIndex();
        indexService.createIndex();
        indexService.defineIndexTypeMapping();

        Thread exportThread = new Thread(new Runnable() {
            public void run() {
                while (!isFinished) {
                    List<HotspotData> dataList = getDataFromOldDataBase();
                    if (dataList != null && !dataList.isEmpty()) {
                        indexService.indexHotSpotDataList(dataList);
                        log.info(String.format("Index data complete %s pages", pageNum));
                    }
                }
                log.info("Index complete ...");
            }
        });
        exportThread.start();
    }

    private List<HotspotData> getDataFromOldDataBase() {
        List<HotspotData> dataList = new ArrayList<HotspotData>(pageCount);
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = DataSourceUtil.getConnection();
            stm = dbConnection.createStatement();
            ++pageNum;
            long startNum = (pageNum - 1) * pageCount;
            log.info(String.format("Geting data from database, offset %s limit %s", startNum, pageCount));
            res = stm.executeQuery(String.format("SELECT * FROM %s LIMIT %s,%s ", dataTableName, startNum, pageCount));
            if (res.next()) {
                do {
                    HotspotData datagram = new HotspotData();
                    datagram.setId(res.getLong("id"));
                    datagram.setDeviceID(res.getString("deviceID"));
                    datagram.setImei(res.getString("IMEI"));
                    datagram.setImsi(res.getString("IMSI"));
                    datagram.setTmsi(res.getString("TMSI"));
                    datagram.setSeqNum(Integer.parseInt(res.getString("seqNum")));
                    datagram.setSrcLac(res.getString("sourceLac"));
                    Date date = res.getDate("timeStamp");
                    datagram.setCollectTime(date);
                    datagram.setOwnArea(res.getString("ownerArea"));
                    datagram.setTeleOper(res.getString("teleOper"));
                    datagram.setTeleSevenNum(res.getString("telNum"));

                    dataList.add(datagram);
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
