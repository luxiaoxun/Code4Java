package com.luxx.web.service;

import com.luxx.index.model.PoiData;
import com.luxx.index.service.PoiIndexService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
public class IndexService implements InitializingBean, DisposableBean {
    @Autowired
    private PoiIndexService poiIndexService;

    @Override
    public void destroy() throws Exception {
        if (poiIndexService != null) {
            poiIndexService.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public List<PoiData> searchPoiInCircle(double lng, double lat, double radius) {
        return poiIndexService.searchPoiInCircle(lng, lat, radius / 1000);
    }

    public List<PoiData> searchPoiInRectangle(double minLng, double minLat,
                                              double maxLng, double maxLat) {
        return poiIndexService.searchPoiInRectangle(minLng, minLat, maxLng, maxLat);
    }
}
