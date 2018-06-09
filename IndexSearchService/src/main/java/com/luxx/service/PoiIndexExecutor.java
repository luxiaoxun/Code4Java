package com.luxx.service;

import com.luxx.model.PoiData;
import com.luxx.util.DataSourceUtil;
import com.luxx.util.PropertiesUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class PoiIndexExecutor {

	private static Logger log = org.slf4j.LoggerFactory
			.getLogger(PoiIndexExecutor.class);

	private PoiIndexService indexService;

	// 读取数据分页
	private long pageNum = 0;
	private int pageCount = 30000;

	private String dataTableName;

	public PoiIndexExecutor() {
		this.dataTableName = PropertiesUtil.getInstance().getProperty(
				"mysql.database.data");
	}

	public void start() throws IOException {
		log.info("开始数据迁移服务......");
		
		indexService = new PoiIndexService();
		indexService.clear();

		Thread exportThread = new Thread(new Runnable() {
			public void run() {
				boolean isRunning = true;
				while (isRunning) {
					List<PoiData> dataList = getDataFromOldDataBase();
					if (dataList == null || dataList.isEmpty()) {
						isRunning = false; // 读取完毕
						log.info("数据迁移服务完成......");
						break;
					}
					int len = dataList.size();
					indexService.indexPoiDataList(dataList);
					System.out.println("索引热点数据最大ID："+dataList.get(len - 1).getId());
				}
			}
		});
		exportThread.start();
	}

	private List<PoiData> getDataFromOldDataBase() {
		List<PoiData> dataList = new ArrayList<PoiData>(pageCount);
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
						data.setId(res.getInt("BaseStationId"));
						data.setAddress(res.getString("Address"));
						data.setLat(res.getDouble("Latitude"));
						data.setLng(res.getDouble("Longitude"));
						dataList.add(data);
					}
				}
			} else {
				log.error("获取数据库连接失败！");
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
