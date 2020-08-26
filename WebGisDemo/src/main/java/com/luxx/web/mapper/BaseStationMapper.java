package com.luxx.web.mapper;

import com.luxx.web.model.BaseStation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseStationMapper {
    List<BaseStation> getAllBaseStation();
}
