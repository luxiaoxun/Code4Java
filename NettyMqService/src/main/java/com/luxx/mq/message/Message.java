package com.luxx.mq.message;

public class Message {
    private Header header;
    private byte[] data;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getBytes() {
        if (header != null) {
            int len = header.getMsgLength();
            byte[] buffer = new byte[len];
            byte[] headerBytes = header.getBytes();
            System.arraycopy(headerBytes, 0, buffer, 0, headerBytes.length);
            if (data != null) {
                System.arraycopy(data, 0, buffer, headerBytes.length, data.length);
            }
            return buffer;
        }
        return null;
    }
}
