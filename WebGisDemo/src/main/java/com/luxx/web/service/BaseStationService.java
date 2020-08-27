package com.luxx.web.service;

import java.util.List;

import com.luxx.web.dao.BaseStationMapper;
import com.luxx.web.model.BaseStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseStationService {
    @Autowired
    private BaseStationMapper baseStationMapper;

    public List<BaseStation> getAllBaseStation() {
        return baseStationMapper.getAllBaseStation();
    }
}
