package com.map.controller;

import com.map.model.BaseStation;
import com.map.model.Message;
import com.map.model.PoiPoint;
import com.map.service.BaseStationService;
import com.map.util.JsonHelper;
import com.poi.service.PoiData;
import com.poi.service.PoiIndexService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by luxiaoxun on 2015/11/25.
 */

@Controller
public class BaseStationController {
    private BaseStationService baseStationService = new BaseStationService();
    private PoiIndexService poiIndexService;
    
    public BaseStationController(){
    	try {
			poiIndexService = new PoiIndexService();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value="/map", method= {RequestMethod.GET,RequestMethod.POST})
    public String map(Model model,HttpServletRequest request,HttpSession session) {
        return "map";
    }
    
    @RequestMapping(value="/supermap", method= {RequestMethod.GET,RequestMethod.POST})
    public String supermap(Model model,HttpServletRequest request,HttpSession session) {
        return "supermap";
    }

    @RequestMapping(value="/loadData", method= {RequestMethod.GET,RequestMethod.POST})
    public @ResponseBody Message loadData(Model model,HttpServletRequest request,HttpSession session) {
        Message msg = new Message();
        List<BaseStation> datas = baseStationService.getAllBaseStation();
        msg.setMsg("ok");
        msg.setData(datas);
        return msg;
    }
    
    @RequestMapping(value="/getDataInCircle", method= {RequestMethod.GET,RequestMethod.POST})
    public @ResponseBody Message getDataInCircle(Model model,HttpServletRequest request,HttpSession session) {
        double radius =Double.parseDouble(request.getParameter("radius"));
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lng = Double.parseDouble(request.getParameter("lng"));
        
        List<PoiData> datas = poiIndexService.searchPoiInCircle(lng, lat, radius/1000);
        Message msg = new Message();
        msg.setMsg("ok");
        msg.setData(datas);

        return msg;
    }
    
    @RequestMapping(value="/getDataInRectangle", method= {RequestMethod.GET,RequestMethod.POST})
    public @ResponseBody Message getDataInRectange(Model model,HttpServletRequest request,
    		HttpSession session) {
    	String json = request.getParameter("latlngs");
        List<PoiPoint> points = JsonHelper.jsonToObjectList(json, ArrayList.class, PoiPoint.class);
        Message msg = new Message();
        if(points!=null && points.size()>=4){
        	double minLat = points.get(0).getLat();
            double maxLat = points.get(1).getLat();
            double minLng = points.get(0).getLng();
            double maxLng = points.get(1).getLng();
            
            for (PoiPoint poiPoint : points) {
            	double lat = poiPoint.getLat();
            	double lng = poiPoint.getLng();
				if(lat > maxLat){
					maxLat = lat;
				}
				if(lat < minLat){
					minLat = lat;
				}
				if(lng > maxLng){
					maxLng = lng;
				}
				if(lng < minLng){
					minLng = lng;
				}
			}
            
            List<PoiData> datas = poiIndexService.searchPoiInRectangle(minLng, minLat, maxLng, maxLat);
            msg.setMsg("ok");
            msg.setData(datas);
        }
        else {
			msg.setMsg("failed");
		}

        return msg;
    }

}
