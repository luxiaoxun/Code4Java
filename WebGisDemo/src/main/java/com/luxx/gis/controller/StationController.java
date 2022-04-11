package com.luxx.gis.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luxx.gis.model.Station;
import com.luxx.gis.model.PoiData;
import com.luxx.gis.model.PoiPoint;
import com.luxx.gis.model.ResultData;
import com.luxx.gis.model.request.DataInCircleRequest;
import com.luxx.gis.model.request.DataInRectangleRequest;
import com.luxx.gis.service.StationService;
import com.luxx.gis.service.IndexSearchService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/station")
@Api(tags = "station")
@Slf4j
public class StationController {
    @Autowired
    private StationService stationService;

    @Autowired
    private IndexSearchService indexSearchService;

    @GetMapping("/loadData")
    public ResultData loadData(@RequestParam(defaultValue = "1") @Min(1) int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Station> stationList = stationService.getStation();
        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        ResultData msg = new ResultData();
        msg.setMsg("ok");
        msg.setData(pageInfo);
        return msg;
    }

    @PostMapping("/dataInCircle")
    public ResultData getDataInCircle(@RequestBody DataInCircleRequest request) {
        log.info("Query data in circle: " + request);
        double radius = request.getRadius();
        double lat = request.getLat();
        double lng = request.getLng();
        List<PoiData> dataList = indexSearchService.searchPoiInCircle(lng, lat, radius);
        ResultData msg = new ResultData();
        msg.setMsg("ok");
        msg.setData(dataList);
        return msg;
    }

    @PostMapping("/dataInRectangle")
    public ResultData getDataInRectangle(@RequestBody DataInRectangleRequest request) {
        log.info("Query data in rectangle: " + request);
        List<PoiPoint> points = request.getPoints();
        ResultData msg = new ResultData();
        if (points != null && points.size() >= 4) {
            double minLat = points.get(0).getLat();
            double maxLat = points.get(1).getLat();
            double minLng = points.get(0).getLon();
            double maxLng = points.get(1).getLon();

            for (PoiPoint poiPoint : points) {
                double lat = poiPoint.getLat();
                double lng = poiPoint.getLon();
                maxLat = Math.max(maxLat, lat);
                minLat = Math.min(minLat, lat);
                maxLng = Math.max(maxLng, lng);
                minLng = Math.min(minLng, lng);
            }
            List<PoiData> dataList = indexSearchService.searchPoiInRectangle(minLng, minLat, maxLng, maxLat);
            msg.setMsg("ok");
            msg.setData(dataList);
        } else {
            msg.setMsg("failed");
        }

        return msg;
    }

}
