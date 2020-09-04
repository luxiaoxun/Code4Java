package com.luxx.index.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.luxx.index.config.IndexConfig;
import com.luxx.index.model.HotspotData;
import com.luxx.index.service.MysqlIndexService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luxx.index.service.IndexDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "index.es.cluster.address")
public class MysqlToEsExecutor extends DataIndexExecutor {
    private static Logger log = LogManager.getLogger(MysqlToEsExecutor.class);

    @Autowired
    private MysqlIndexService mysqlIndexService;

    @Autowired
    private IndexDataSource indexDataSource;

    @Autowired
    private IndexConfig indexConfig;

    // 读取数据分页
    private long pageNum = 0;
    private int pageCount = 30000;
    private volatile boolean isFinished = false;
    private String dataTableName;

    @PostConstruct
    public void init() {
        dataTableName = indexConfig.getDbTable();
    }

    @Override
    public void start() {
        log.info("Start index mysql data to ES");

        //indexService.deleteIndex();
        mysqlIndexService.createIndex();
        mysqlIndexService.defineIndexTypeMapping();

        Thread exportThread = new Thread(new Runnable() {
            public void run() {
                while (!isFinished) {
                    List<HotspotData> dataList = getDataFromDataBase();
                    if (dataList != null && !dataList.isEmpty()) {
                        mysqlIndexService.indexHotSpotDataList(dataList);
                        log.info(String.format("Index data complete %s pages", pageNum));
                    }
                }
                log.info("Index mysql data to ES complete ");
            }
        });
        exportThread.start();
    }

    private List<HotspotData> getDataFromDataBase() {
        List<HotspotData> dataList = new ArrayList<>(pageCount);
        Connection dbConnection = null;
        Statement stm = null;
        ResultSet res = null;
        try {
            dbConnection = indexDataSource.getConnection();
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

    @Override
    public void stop() {
        mysqlIndexService.close();
    }
}
