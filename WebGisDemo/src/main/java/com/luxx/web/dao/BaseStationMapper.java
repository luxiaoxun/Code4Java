package com.luxx.web.dao;

import com.luxx.web.model.BaseStation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseStationMapper {
    List<BaseStation> getAllBaseStation();
}
