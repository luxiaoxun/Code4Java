package com.luxx.log.client;

import com.luxx.log.util.DateTimeUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

@Component
@Lazy
public class ElasticSearchClient {
    private static Logger logger = LogManager.getLogger(ElasticSearchClient.class);

    @Value("${es.host}")
    private String esHost;

    @Value("${es.port}")
    private int esPort;

    @Value("${es.username}")
    private String username;

    @Value("${es.password}")
    private String password;

    @Value("${es.index}")
    private String indexName;

    // ES Client
    private RestHighLevelClient client;

    @PostConstruct
    public void init() throws UnknownHostException {
        logger.info("es.host: " + esHost);
        logger.info("es.port: " + esPort);
        logger.info("es.index: " + indexName);

        client = new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY,
                                new UsernamePasswordCredentials(username, password));
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }));
    }

    @PreDestroy
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
    }

    public void indexLog(List<String> logList) {
        if (logList != null && logList.size() > 0) {
            BulkRequest request = new BulkRequest();
            for (String data : logList) {
                String month = DateTimeUtil.currentYM();
                String index = this.indexName + "_" + month;
                request.add(new IndexRequest(index).source(data, XContentType.JSON).type("_doc"));
            }
            BulkResponse bulkResponse;
            try {
                bulkResponse = client.bulk(request);
                if (bulkResponse.hasFailures()) {
                    logger.error(bulkResponse.buildFailureMessage());
                }
            } catch (IOException e) {
                logger.error(e.toString());
            }
            logger.info("Index {} log to ES", logList.size());
        }
    }


}
