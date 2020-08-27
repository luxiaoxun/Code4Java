package com.luxx.index.model;

import java.io.Serializable;

public class PoiData implements Serializable {
    private static final long serialVersionUID = -3978597686509612192L;

    private long id;
    private String address;
    private double lat;
    private double lng;

    public PoiData() {
    }

    public PoiData(int id, String address, double lat, double lng) {
        this.id = id;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

}
