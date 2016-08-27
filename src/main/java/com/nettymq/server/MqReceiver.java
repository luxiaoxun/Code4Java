package com.nettymq.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelGroupFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RabbitMQ Receiver based on RabbitMQ java client API
 */
public class MqReceiver {
	private static final Logger log = LoggerFactory.getLogger(MqReceiver.class);
	private ConnectionFactory connnectionFactory;
	private String exchangeName = "NettyMqServerListenerExchange";
	private String queueName = "MqListenerQueue";
	private String routeKey = "mqListener";

	private Thread listenThread;

	public MqReceiver() {
		connnectionFactory = new ConnectionFactory();
		connnectionFactory.setHost("192.8.125.202");
		connnectionFactory.setUsername("guest");
		connnectionFactory.setPassword("guest");
		connnectionFactory.setPort(5672);
		connnectionFactory.setVirtualHost("/");
	}

	public void start() {
		listenThread = new Thread() {
			@Override
			public void run() {
				try {
					Connection connection = connnectionFactory.newConnection();
					final Channel channel = connection.createChannel();
					channel.exchangeDeclare(exchangeName, "direct", true,
							false, null);
					channel.queueDeclare(queueName, true, false, false, null);
					channel.queueBind(queueName, exchangeName, routeKey);

					// process the message one by one
					channel.basicQos(1);

					QueueingConsumer queueingConsumer = new QueueingConsumer(
							channel);
					// auto-ack is false
					channel.basicConsume(queueName, false, queueingConsumer);
					while (true) {
						final QueueingConsumer.Delivery delivery = queueingConsumer
								.nextDelivery();
						String message = new String(delivery.getBody());
						broudcastMsgAndAck(message, channel, delivery);
					}
				} catch (Exception ex) {
					log.error(String.format(
							"Create Rabbit MQ listener error %s",
							ex.getMessage()));
				}
			}
		};

		listenThread.setDaemon(true);
		listenThread.start();
	}

	private void broudcastMsgAndAck(String message, final Channel channel,
			final QueueingConsumer.Delivery delivery) {
		// Broudcast message to all connected clients
		// If you want to send to a specified client, just add
		// your own logic and ack manually
		// Be aware that ChannelGroup is thread safe
		if (EchoServerHandler.channels != null) {
			log.info(String.format("Conneted client number: %d",
					EchoServerHandler.channels.size()));

			ByteBuf msg = Unpooled.copiedBuffer(message.getBytes());
			EchoServerHandler.channels.writeAndFlush(msg).addListener(
					new ChannelGroupFutureListener() {
						@Override
						public void operationComplete(ChannelGroupFuture arg0)
								throws Exception {
							// manually ack to MQ server
							// when message is consumed.
							channel.basicAck(delivery.getEnvelope()
									.getDeliveryTag(), false);
							log.debug("Mq Receiver get message");
						}
					});
		}

	}
}
