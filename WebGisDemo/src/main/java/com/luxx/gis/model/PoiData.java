package com.luxx.gis.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PoiData implements Serializable {
    private static final long serialVersionUID = -3978597686509612192L;

    private long id;
    private String address;
    private PoiPoint poiPoint;

    public PoiData() {
    }

    public PoiData(long id, String address, double lat, double lng) {
        this.id = id;
        this.address = address;
        this.poiPoint = new PoiPoint(lat, lng);
    }

}
