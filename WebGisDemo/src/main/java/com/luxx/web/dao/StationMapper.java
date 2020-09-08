package com.luxx.web.dao;

import com.luxx.web.model.Station;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {
    List<Station> getStation();

    int insert(Station station);

    int delete(int id);

    int update(Station station);
}
