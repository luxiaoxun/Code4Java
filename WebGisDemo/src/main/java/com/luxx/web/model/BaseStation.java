package com.luxx.web.model;

public class BaseStation {
    private long id;

    private String countryCode;

    private String provider;

    private String lac;

    private String cell;

    private double latitude;

    private double longitude;

    private double r_latitude;

    private double r_longitude;

    private double range;

    private String address;

    public BaseStation() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getR_latitude() {
        return r_latitude;
    }

    public void setR_latitude(double r_latitude) {
        this.r_latitude = r_latitude;
    }

    public double getR_longitude() {
        return r_longitude;
    }

    public void setR_longitude(double r_longitude) {
        this.r_longitude = r_longitude;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BaseStation{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", provider='" + provider + '\'' +
                ", lac='" + lac + '\'' +
                ", cell='" + cell + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", r_latitude=" + r_latitude +
                ", r_longitude=" + r_longitude +
                ", range=" + range +
                ", address='" + address + '\'' +
                '}';
    }
}
