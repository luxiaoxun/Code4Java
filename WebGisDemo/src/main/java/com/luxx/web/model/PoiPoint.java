package com.luxx.web.model;

public class PoiPoint {
    private double lat;
    private double lng;

    public PoiPoint() {

    }

    public PoiPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "PoiPoint{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
