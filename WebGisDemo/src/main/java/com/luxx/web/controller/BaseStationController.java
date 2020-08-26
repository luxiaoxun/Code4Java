package com.luxx.web.controller;

import com.luxx.web.model.BaseStation;
import com.luxx.index.model.PoiData;
import com.luxx.web.model.PoiPoint;
import com.luxx.web.model.ResultData;
import com.luxx.web.model.request.DataInCircleRequest;
import com.luxx.web.service.BaseStationService;
import com.luxx.index.util.JsonUtil;

import com.luxx.web.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class BaseStationController {
    @Autowired
    private BaseStationService baseStationService;

    @Autowired
    private IndexService indexService;

    @GetMapping("/map")
    public String map() {
        return "map";
    }

    @GetMapping("/supermap")
    public String supermap() {
        return "supermap";
    }

    @GetMapping("/loadData")
    public ResultData loadData() {
        ResultData msg = new ResultData();
        List<BaseStation> datas = baseStationService.getAllBaseStation();
        msg.setMsg("ok");
        msg.setData(datas);
        return msg;
    }

    @GetMapping("/dataInCircle")
    public ResultData getDataInCircle(@RequestBody DataInCircleRequest request) {
        double radius = request.getRadius();
        double lat = request.getLat();
        double lng = request.getLng();

        List<PoiData> datas = indexService.searchPoiInCircle(lng, lat, radius / 1000);
        ResultData msg = new ResultData();
        msg.setMsg("ok");
        msg.setData(datas);

        return msg;
    }

    @GetMapping(value = "/dataInRectangle")
    public ResultData getDataInRectangle(Model model, HttpServletRequest request, HttpSession session) {
        String json = request.getParameter("latlngs");
        List<PoiPoint> points = JsonUtil.jsonToObjectList(json, ArrayList.class, PoiPoint.class);
        ResultData msg = new ResultData();
        if (points != null && points.size() >= 4) {
            double minLat = points.get(0).getLat();
            double maxLat = points.get(1).getLat();
            double minLng = points.get(0).getLng();
            double maxLng = points.get(1).getLng();

            for (PoiPoint poiPoint : points) {
                double lat = poiPoint.getLat();
                double lng = poiPoint.getLng();
                if (lat > maxLat) {
                    maxLat = lat;
                }
                if (lat < minLat) {
                    minLat = lat;
                }
                if (lng > maxLng) {
                    maxLng = lng;
                }
                if (lng < minLng) {
                    minLng = lng;
                }
            }

            List<PoiData> datas = indexService.searchPoiInRectangle(minLng, minLat, maxLng, maxLat);
            msg.setMsg("ok");
            msg.setData(datas);
        } else {
            msg.setMsg("failed");
        }

        return msg;
    }

}
