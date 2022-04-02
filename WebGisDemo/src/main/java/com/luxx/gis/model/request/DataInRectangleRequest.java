package com.luxx.gis.model.request;

import com.luxx.gis.model.PoiPoint;
import lombok.Data;

import java.util.List;

@Data
public class DataInRectangleRequest {
    private List<PoiPoint> points;
}
