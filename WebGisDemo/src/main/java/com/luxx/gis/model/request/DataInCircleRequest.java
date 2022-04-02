package com.luxx.gis.model.request;

import lombok.Data;

@Data
public class DataInCircleRequest {
    private double radius;
    private double lat;
    private double lng;
}
