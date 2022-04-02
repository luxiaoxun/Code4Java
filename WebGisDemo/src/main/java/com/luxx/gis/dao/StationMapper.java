package com.luxx.gis.dao;

import com.luxx.gis.model.Station;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {
    List<Station> getStation();

    int insert(Station station);

    int delete(int id);

    int update(Station station);
}
