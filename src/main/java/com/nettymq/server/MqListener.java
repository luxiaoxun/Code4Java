package com.nettymq.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * RabbitMQ Listener based on Spring AMQP
 */
public class MqListener implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(MqListener.class);

	public MqListener() {

	}

	@Override
	public void onMessage(Message message) {
		log.debug("Get message from rabbitMQ");
		// do some thing with the message
		if (message != null) {
			for (Channel c : EchoServerHandler.channels) {
				ByteBuf msg = Unpooled.copiedBuffer(message.getBody());
				c.writeAndFlush(msg);
			}
		}
	}
}
