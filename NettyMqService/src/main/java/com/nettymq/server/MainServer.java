package com.nettymq.server;

/**
 * Main Program
 */
public class MainServer {
    public static void main(String[] args) {
        NettyMqServer server = new NettyMqServer();
        server.doStart();
    }
}
