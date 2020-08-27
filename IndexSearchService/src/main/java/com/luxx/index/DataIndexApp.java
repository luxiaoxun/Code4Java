package com.luxx.index;

import com.luxx.index.executor.PoiIndexExecutor;

public class DataIndexApp {

    public static void main(String[] args) {
//        MysqlDataTransferExecutor executor = new MysqlDataTransferExecutor();
//        executor.start();
        PoiIndexExecutor executor = new PoiIndexExecutor();
        executor.start();
    }
}
