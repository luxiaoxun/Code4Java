package com.luxx.index;

import com.luxx.index.executor.PoiIndexExecutor;

public class DataIndexApp {

    public static void main(String[] args) {
        PoiIndexExecutor executor = new PoiIndexExecutor();
        executor.start();
    }
}
