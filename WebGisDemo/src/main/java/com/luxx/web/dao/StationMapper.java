package com.luxx.web.dao;

import com.luxx.web.model.Station;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {
    List<Station> getAllStation();

    int insert(Station station);

    int delete(int id);

    int update(Station station);

    int pageListCount();

    List<Station> pageList(int offset, int pageSize);
}
