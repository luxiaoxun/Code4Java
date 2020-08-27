package com.luxx.web.dao;

import com.luxx.web.model.Station;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {
    List<Station> getAllBaseStation();
}
