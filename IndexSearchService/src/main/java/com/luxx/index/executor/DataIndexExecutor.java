package com.luxx.index.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class DataIndexExecutor {
    public abstract void start();

    public abstract void stop();

    public void attemptClose(ResultSet o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

    public void attemptClose(Statement o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

    public void attemptClose(Connection o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }
}
