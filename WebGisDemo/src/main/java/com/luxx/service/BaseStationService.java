package com.luxx.service;

import com.luxx.dao.BaseStationDb;
import com.luxx.model.BaseStation;
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
