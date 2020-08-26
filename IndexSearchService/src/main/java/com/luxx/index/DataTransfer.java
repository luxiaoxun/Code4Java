package com.luxx.index;

import com.luxx.index.executor.MysqlDataTransferExecutor;

public class DataTransfer {

    public static void main(String[] args) {
        MysqlDataTransferExecutor executor = new MysqlDataTransferExecutor();
        executor.start();
    }
}
