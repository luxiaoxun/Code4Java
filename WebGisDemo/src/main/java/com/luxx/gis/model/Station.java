package com.luxx.gis.model;

import lombok.Data;

@Data
public class Station {
    private long id;

    private String countryCode;

    private String provider;

    private String lac;

    private String cell;

    private double latitude;

    private double longitude;

    private String address;
}
