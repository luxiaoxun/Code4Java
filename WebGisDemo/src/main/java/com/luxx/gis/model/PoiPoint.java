package com.luxx.gis.model;

import lombok.Data;

@Data
public class PoiPoint {
    private double lat;
    private double lon;

    public PoiPoint() {
    }

    public PoiPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
