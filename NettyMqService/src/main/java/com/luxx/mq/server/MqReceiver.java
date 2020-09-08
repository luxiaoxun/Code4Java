package com.luxx.mq.server;

import com.luxx.mq.config.MqConfig;
import com.luxx.mq.handler.EchoServerHandler;
import com.luxx.mq.util.PropertiesUtil;
import com.rabbitmq.client.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.group.ChannelGroupFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * RabbitMQ Receiver based on RabbitMQ java client API
 */
public class MqReceiver {
    private static final Logger log = LoggerFactory.getLogger(MqReceiver.class);
    private ConnectionFactory connectionFactory;
    private String exchangeName = "NettyMqServerListenerExchange";
    private String queueName = "MqListenerQueue";
    private String routeKey = "mqListener";

    private Thread listenThread;

    public MqReceiver() {
        String host = PropertiesUtil.getInstance().getProperty(MqConfig.MQ_HOST);
        int port = PropertiesUtil.getInstance().getPropertyAsInt(MqConfig.MQ_PORT);
        String username = PropertiesUtil.getInstance().getProperty(MqConfig.MQ_USERNAME);
        String password = PropertiesUtil.getInstance().getProperty(MqConfig.MQ_PASSWORD);
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost("/");
    }

    public void start() {
        listenThread = new Thread(() -> {
            try {
                Connection connection = connectionFactory.newConnection();
                final Channel channel = connection.createChannel();
                channel.exchangeDeclare(exchangeName, "direct", true, false, null);
                channel.queueDeclare(queueName, true, false, false, null);
                channel.queueBind(queueName, exchangeName, routeKey);
                // process the message one by one
                channel.basicQos(1);

                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                        String message = new String(body, StandardCharsets.UTF_8);
                        broadcastMsgAndAck(message, channel, envelope);
                    }
                };
                channel.basicConsume(queueName, false, consumer);
            } catch (Exception ex) {
                log.error("Create Rabbit MQ listener error: " + ex.getMessage());
            }
        });

        listenThread.setDaemon(true);
        listenThread.start();
    }

    private void broadcastMsgAndAck(String message, final Channel channel, final Envelope envelope) {
        // Broadcast message to all connected clients
        // If you want to send to a specified client, just add your own logic and ack manually
        // Be aware that ChannelGroup is thread safe
        log.info("Connected client number: " + EchoServerHandler.channels.size());
        ByteBuf msg = Unpooled.copiedBuffer(message.getBytes());
        EchoServerHandler.channels.writeAndFlush(msg).addListener(
                (ChannelGroupFutureListener) arg0 -> {
                    // manually ack to MQ server when message is consumed.
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    log.debug("Mq Receiver get message");
                });
    }
}
