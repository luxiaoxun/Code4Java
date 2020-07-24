package com.luxx.map.service;

public class MapRequestParam {
    private boolean isOk;
    private String dbType;
    private int zoom;
    private int x;
    private int y;

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean isOk) {
        this.isOk = isOk;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        return this.dbType.hashCode() ^ this.x ^ this.y ^ this.zoom;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapRequestParam)) {
            return false;
        }
        MapRequestParam that = (MapRequestParam) obj;
        return this.isOk == that.isOk()
                && this.dbType == that.getDbType()
                && this.zoom == that.getZoom()
                && this.x == that.getX()
                && this.y == that.getY();
    }

    @Override
    public String toString() {
        return "IsOk:" + this.isOk + " DbType:" + this.dbType + " Zoom:" + this.zoom + " X:" + this.x + " Y:" + this.y;
    }

}
