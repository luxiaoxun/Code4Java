package com.luxx.log.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luxx.log.client.ElasticSearchClient;
import com.luxx.log.util.JsonUtil;
import com.luxx.log.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
public class LogIndexExecutor extends DataIndexExecutor {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Value("${kafka.url}")
    private String kafkaUrl;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    private String groupId = "log-collector";

    private volatile boolean isRunning = true;
    private Thread worker;
    private KafkaConsumer<String, String> kafkaConsumer;


    @PostConstruct
    public void init() {
        log.info("kafka.url: " + kafkaUrl);
        log.info("kafka.topic: " + kafkaTopic);

        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        kafkaConsumer = new KafkaConsumer<>(p);
        kafkaConsumer.subscribe(Collections.singletonList(kafkaTopic));
    }

    @Override
    public void start() {
        log.info("Start index log service");
        try {
            worker = new Thread(new Runnable() {
                public void run() {
                    while (isRunning) {
                        try {
                            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                            List<String> logList = new ArrayList<>(1000);
                            for (ConsumerRecord<String, String> record : records) {
                                log.debug("topic: {}, offset: {}, log: {}", record.topic(), record.offset(), record.value());
                                try {
                                    String data = record.value();
                                    if (JsonUtil.isValidJson(data)) {
                                        JsonNode json = JsonUtil.decode(data, JsonNode.class);
                                        if (json.has("message")) {
                                            String msg = json.get("message").asText();
                                            JsonNode msgNode = JsonUtil.decode(msg, JsonNode.class);
                                            ObjectNode objectNode = (ObjectNode) json;
                                            objectNode.replace("message", msgNode);
                                            String timestamp = DateTimeUtil.getEsString(DateTime.now().getMillis());
                                            objectNode.put("@timestamp", timestamp);
                                            logList.add(objectNode.toString());
                                        }
                                    } else {
                                        ObjectNode objectNode = JsonUtil.createObjectNode();
                                        String timestamp = DateTimeUtil.getEsString(DateTime.now().getMillis());
                                        objectNode.put("@timestamp", timestamp);
                                        objectNode.put("message", data);
                                        logList.add(objectNode.toString());
                                    }
                                } catch (Exception ex) {
                                    log.error("Process log error: " + ex.toString());
                                }
                            }
                            elasticSearchClient.indexLog(logList);
                        } catch (Exception ex) {
                            log.error("Log index exception：" + ex.toString());
                        }
                    }
                }
            });
            worker.start();
        } catch (Exception ex) {
            log.error("Start index service exception：" + ex.toString());
        }
    }


    @Override
    public void stop() {
        if (worker != null) {
            isRunning = false;
        }
    }
}
