package com.luxx.web.model;

public class ResultData {
    private String msg;
    private Object data;

    public ResultData() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
