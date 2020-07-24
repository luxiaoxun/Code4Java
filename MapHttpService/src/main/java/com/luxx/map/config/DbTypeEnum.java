package com.luxx.map.config;

public enum DbTypeEnum {
    sqlite(0), mysql(1);
    private int type;

    DbTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
