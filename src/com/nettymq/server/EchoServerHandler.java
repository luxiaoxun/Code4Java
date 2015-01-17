package com.nettymq.server;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo server.
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger log = LoggerFactory
			.getLogger(EchoServerHandler.class);

	static final ChannelGroup channels = new DefaultChannelGroup(
			GlobalEventExecutor.INSTANCE);

	private MqSender mqSender;

	public EchoServerHandler(MqSender mqSender) {
		this.mqSender = mqSender;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = (ByteBuf) msg;
		String data = in.toString(io.netty.util.CharsetUtil.UTF_8);

		// Receive message from client
		// Send message to rabbit MQ who want to subscribe
		mqSender.send(data);

		// Echo server: send back the msg to client
		ctx.writeAndFlush(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// A closed channel will be removed from ChannelGroup automatically
		channels.add(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Disconnected client "+ctx.channel().remoteAddress());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
		log.warn(cause.getMessage());
	}

}
