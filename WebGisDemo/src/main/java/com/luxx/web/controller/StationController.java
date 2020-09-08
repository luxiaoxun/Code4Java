package com.luxx.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luxx.web.model.Station;
import com.luxx.index.model.PoiData;
import com.luxx.web.model.PoiPoint;
import com.luxx.web.model.ResultData;
import com.luxx.web.model.request.DataInCircleRequest;
import com.luxx.web.model.request.DataInRectangleRequest;
import com.luxx.web.service.StationService;
import com.luxx.web.service.IndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/station")
@Api(tags = "station")
@Slf4j
public class StationController {
    @Autowired
    private StationService stationService;

    @Autowired
    private IndexService indexService;

    @GetMapping("/loadData")
    @ApiOperation(value = "loadData", notes = "loadData")
    public ResultData loadData(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Station> stationList = stationService.getStation();
        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        ResultData msg = new ResultData();
        msg.setMsg("ok");
        msg.setData(pageInfo);
        return msg;
    }

    @RequestMapping(value = "/dataInCircle", method = RequestMethod.POST)
    @ApiOperation(value = "dataInCircle", notes = "dataInCircle")
    public ResultData getDataInCircle(@RequestBody DataInCircleRequest request) {
        log.info("Query data in circle: " + request);
        double radius = request.getRadius();
        double lat = request.getLat();
        double lng = request.getLng();
        List<PoiData> dataList = indexService.searchPoiInCircle(lng, lat, radius);
        ResultData msg = new ResultData();
        msg.setMsg("ok");
        msg.setData(dataList);

        return msg;
    }

    @RequestMapping(value = "/dataInRectangle", method = RequestMethod.POST)
    @ApiOperation(value = "dataInRectangle", notes = "dataInRectangle")
    public ResultData getDataInRectangle(@RequestBody DataInRectangleRequest request) {
        log.info("Query data in rectangle: " + request);
        List<PoiPoint> points = request.getPoints();
        ResultData msg = new ResultData();
        if (points != null && points.size() >= 4) {
            double minLat = points.get(0).getLat();
            double maxLat = points.get(1).getLat();
            double minLng = points.get(0).getLng();
            double maxLng = points.get(1).getLng();

            for (PoiPoint poiPoint : points) {
                double lat = poiPoint.getLat();
                double lng = poiPoint.getLng();
                maxLat = Math.max(maxLat, lat);
                minLat = Math.min(minLat, lat);
                maxLng = Math.max(maxLng, lng);
                minLng = Math.min(minLng, lng);
            }

            List<PoiData> dataList = indexService.searchPoiInRectangle(minLng, minLat, maxLng, maxLat);
            msg.setMsg("ok");
            msg.setData(dataList);
        } else {
            msg.setMsg("failed");
        }

        return msg;
    }

}
