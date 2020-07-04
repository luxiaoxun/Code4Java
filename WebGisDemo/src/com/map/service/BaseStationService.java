package com.map.service;

import com.map.dao.BaseStationDb;
import com.map.model.BaseStation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by luxiaoxun on 2015/11/25.
 */

public class BaseStationService implements IBaseStationOperation {

    private BaseStationDb baseStaionDb = new BaseStationDb();

    public List<BaseStation> getAllBaseStation() {
        return baseStaionDb.getAllBaseStations();
    }
}
