package com.luxx.gis.model;

import lombok.Data;

@Data
public class PoiPoint {
    private double lat;
    private double lng;

    public PoiPoint() {
    }

    public PoiPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
