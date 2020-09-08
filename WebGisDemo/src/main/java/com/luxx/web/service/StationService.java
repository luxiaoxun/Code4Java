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

    public List<Station> getStation() {
        return stationMapper.getStation();
    }

    public int insert(Station station){
        return stationMapper.insert(station);
    }

    public int update(Station station){
        return stationMapper.update(station);
    }

    public int delete(int id){
        return stationMapper.delete(id);
    }


}
