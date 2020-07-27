package com.luxx.mq.handler;

import com.luxx.mq.server.MqSender;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Netty ChannelInitializer
 */
public class NettyMqServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    // private EventBus eventBus;
    private MqSender mqSender;

    public NettyMqServerChannelInitializer(MqSender mqSender) {
        this.mqSender = mqSender;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Reader idle time 3 minutes
        ch.pipeline().addLast(new IdleStateHandler(3 * 60, 0, 0));
        ch.pipeline().addLast(new HeartBeatHandler());
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, -4, 0));
        ch.pipeline().addLast(new MessageDecoder());
        ch.pipeline().addLast(new EchoServerHandler(mqSender));
    }
}
