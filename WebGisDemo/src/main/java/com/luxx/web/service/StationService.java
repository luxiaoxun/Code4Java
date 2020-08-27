package com.luxx.web.service;

import java.util.List;

import com.luxx.web.dao.StationMapper;
import com.luxx.web.model.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    @Autowired
    private StationMapper stationMapper;

    public List<Station> getAllBaseStation() {
        return stationMapper.getAllBaseStation();
    }
}
