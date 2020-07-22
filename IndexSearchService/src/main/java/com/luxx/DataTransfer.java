package com.luxx;

import com.luxx.executor.RedshiftDataTransferExecutor;

public class DataTransfer {

    public static void main(String[] args) {
        // MysqlDataTransferEexcutor executor = new MysqlDataTransferEexcutor();
        // executor.start();

        RedshiftDataTransferExecutor executor = new RedshiftDataTransferExecutor();
        executor.start();
    }
}
