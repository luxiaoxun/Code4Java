package com.luxx.web.model.request;

public class DataInCircleRequest {
    private double radius;
    private double lat;
    private double lng;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
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
        return "DataInCircleRequest{" +
                "radius=" + radius +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
