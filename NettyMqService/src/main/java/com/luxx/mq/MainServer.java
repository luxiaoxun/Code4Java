package com.luxx.mq;

import com.luxx.mq.server.NettyMqServer;

/**
 * Main Program
 */
public class MainServer {
    public static void main(String[] args) {
        NettyMqServer server = new NettyMqServer();
        server.doStart();
    }
}
