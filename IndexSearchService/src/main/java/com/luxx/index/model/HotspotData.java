package com.luxx.index.model;

import java.io.Serializable;
import java.util.Date;

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

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int newSeqNum) {
        seqNum = newSeqNum;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date newCollectTime) {
        collectTime = newCollectTime;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String newImsi) {
        imsi = newImsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String newImei) {
        imei = newImei;
    }

    public String getTmsi() {
        return tmsi;
    }

    public void setTmsi(String newTmsi) {
        tmsi = newTmsi;
    }

    public String getSrcLac() {
        return srcLac;
    }

    public void setSrcLac(String newSrcLac) {
        srcLac = newSrcLac;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String newDeviceID) {
        deviceID = newDeviceID;
    }

    public String getTeleOper() {
        return teleOper;
    }

    public void setTeleOper(String newTeleOper) {
        teleOper = newTeleOper;
    }

    public String getOwnArea() {
        return ownArea;
    }

    public void setOwnArea(String newOwnArea) {
        ownArea = newOwnArea;
    }

    public String getTeleSevenNum() {
        return teleSevenNum;
    }

    public void setTeleSevenNum(String newTeleSevenNum) {
        teleSevenNum = newTeleSevenNum;
    }

    public String getTeleBrand() {
        return teleBrand;
    }

    public void setTeleBrand(String newTeleBrand) {
        teleBrand = newTeleBrand;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj || !(obj instanceof HotspotData)) {
            return false;
        }
        HotspotData hot = (HotspotData) obj;

        if (!imei.equals(hot.getImei()))
            return false;
        if (!imsi.equals(hot.getImsi()))
            return false;
        if (collectTime.getTime() != hot.getCollectTime().getTime())
            return false;
        if (!deviceID.equals(hot.getDeviceID()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((collectTime == null) ? 0 : collectTime.hashCode());
        result = prime * result + ((deviceID == null) ? 0 : deviceID.hashCode());
        result = prime * result + ((imei == null) ? 0 : imei.hashCode());
        result = prime * result + ((imsi == null) ? 0 : imsi.hashCode());
        return result;
    }
}
