package com.luxx.gis.service;

import com.luxx.gis.model.PoiData;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexSearchService {
    @Autowired
    private PoiIndexService poiIndexService;

    public List<PoiData> searchPoiInCircle(double lng, double lat, double radius) {
        return poiIndexService.searchPoiInCircle(lng, lat, String.valueOf(radius));
    }

    public List<PoiData> searchPoiInRectangle(double minLng, double minLat,
                                              double maxLng, double maxLat) {
        return poiIndexService.searchPoiInRectangle(minLng, minLat, maxLng, maxLat);
    }
}
