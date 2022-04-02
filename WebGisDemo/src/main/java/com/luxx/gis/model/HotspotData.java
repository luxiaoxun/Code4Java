package com.luxx.gis.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HotspotData implements Serializable {
    private static final long serialVersionUID = -2132799122365334550L;

    private Long id;
    private int seqNum;
    private Date collectTime;

    private String imsi;
    private String imei;
    private String tmsi;

    private String srcLac;
    private String deviceID;
    private String teleOper;
    private String ownArea;
    private String teleSevenNum;
    private String teleBrand;
}
