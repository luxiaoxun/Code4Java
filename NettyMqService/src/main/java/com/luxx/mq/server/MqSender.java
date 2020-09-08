package com.luxx.mq.server;

import com.luxx.mq.config.MqConfig;
import com.luxx.mq.util.PropertiesUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * RabbitMQ Sender based on Spring AMQP
 */
public class MqSender {
    private static final Logger log = LoggerFactory.getLogger(MqSender.class);
    private ConnectionFactory connectionFactory;
    private String exchangeName = "NettyMqServerSenderExchange";
    private Channel channel = null;

    public MqSender() {
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

        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "fanout");
        } catch (Exception e) {
            log.error("Exception: " + e.toString());
        }
    }

    public void send(String data) {
        try {
            channel.basicPublish(exchangeName, "", null, data.getBytes());
        } catch (IOException e) {
            log.error("Mq sender error: " + e.toString());
        }
    }
}
