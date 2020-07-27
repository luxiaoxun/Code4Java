package com.luxx.mq.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Handler implementation for heart beating.
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // Read timeout
                // System.out.println("READER_IDLE: read timeout from "+ctx.channel().remoteAddress());
                // ctx.disconnect(); //Channel disconnect
                log.info("READER_IDLE: read idle from " + ctx.channel().remoteAddress());
            }
        }
    }
}
