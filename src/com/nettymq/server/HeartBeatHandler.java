package com.nettymq.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Handler implementation for heart beating.
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				// Read timeout
				System.out.println("READER_IDLE: read timeout from "+ctx.channel().remoteAddress());
				//ctx.disconnect(); //Channel disconnect
			}
		}
	}

}
