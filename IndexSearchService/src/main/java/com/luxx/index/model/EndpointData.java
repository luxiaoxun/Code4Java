package com.luxx.index.model;

import java.io.Serializable;
import java.util.Date;

public class EndpointData implements Serializable {
    private static final long serialVersionUID = 1493676048457257941L;

    private int org_id;
    private long endpoint_id;

    private long ds_bytes;
    private double ds_max_bytes;
    private double ds_avg_bytes;
    private int ds_mwt;

    private long us_bytes;
    private double us_max_bytes;
    private double us_avg_bytes;
    private int us_mwt;

    private Date date_time;

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public long getEndpoint_id() {
        return endpoint_id;
    }

    public void setEndpoint_id(long endpoint_id) {
        this.endpoint_id = endpoint_id;
    }

    public long getDs_bytes() {
        return ds_bytes;
    }

    public void setDs_bytes(long ds_bytes) {
        this.ds_bytes = ds_bytes;
    }

    public double getDs_max_bytes() {
        return ds_max_bytes;
    }

    public void setDs_max_bytes(double ds_max_bytes) {
        this.ds_max_bytes = ds_max_bytes;
    }

    public double getDs_avg_bytes() {
        return ds_avg_bytes;
    }

    public void setDs_avg_bytes(double ds_avg_bytes) {
        this.ds_avg_bytes = ds_avg_bytes;
    }

    public int getDs_mwt() {
        return ds_mwt;
    }

    public void setDs_mwt(int ds_mwt) {
        this.ds_mwt = ds_mwt;
    }

    public long getUs_bytes() {
        return us_bytes;
    }

    public void setUs_bytes(long us_bytes) {
        this.us_bytes = us_bytes;
    }

    public double getUs_max_bytes() {
        return us_max_bytes;
    }

    public void setUs_max_bytes(double us_max_bytes) {
        this.us_max_bytes = us_max_bytes;
    }

    public double getUs_avg_bytes() {
        return us_avg_bytes;
    }

    public void setUs_avg_bytes(double us_avg_bytes) {
        this.us_avg_bytes = us_avg_bytes;
    }

    public int getUs_mwt() {
        return us_mwt;
    }

    public void setUs_mwt(int us_mwt) {
        this.us_mwt = us_mwt;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

}
