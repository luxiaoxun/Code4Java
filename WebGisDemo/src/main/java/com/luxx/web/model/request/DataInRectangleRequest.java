package com.luxx.web.model.request;

import com.luxx.web.model.PoiPoint;

import java.util.List;

public class DataInRectangleRequest {
    private List<PoiPoint> points;

    public List<PoiPoint> getPoints() {
        return points;
    }

    public void setPoints(List<PoiPoint> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "DataInRectangleRequest{" +
                "points=" + points +
                '}';
    }
}
