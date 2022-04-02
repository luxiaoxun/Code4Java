package com.luxx.gis.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Lazy
public class ElasticSearchClient {
    private static Logger log = LogManager.getLogger(ElasticSearchClient.class);

    @Value("${es.address}")
    private String esAddress;

    @Value("${es.username:}")
    private String username;

    @Value("${es.password:}")
    private String password;

    // ES Client
    private RestHighLevelClient client;
    private BulkProcessor bulkProcessor;
    private int bulkSize = 5;
    private int bulkActions = 1000;
    private int flushInterval = 3;
    private int concurrentRequests = 5;

    @PostConstruct
    public void init() throws UnknownHostException {
        log.info("es.address: " + esAddress);
        String[] hostPort = esAddress.split(":");
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostPort[0], Integer.parseInt(hostPort[1])))
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                            credentialsProvider.setCredentials(AuthScope.ANY,
                                    new UsernamePasswordCredentials(username, password));
                            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        }
                    }));
        } else {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostPort[0], Integer.parseInt(hostPort[1]))));
        }

        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                log.debug("ES before bulk, number of actions: {}", request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse responses) {
                if (responses.hasFailures()) {
                    try {
                        StringBuilder sb = new StringBuilder("Failure in bulk execution: ");
                        int pos = responses.getItems().length - 1;
                        BulkItemResponse response = responses.getItems()[pos];
                        while (!response.isFailed() && pos >= 0) {
                            response = responses.getItems()[pos];
                            --pos;
                        }
                        sb.append("\n[").append(pos)
                                .append("]: index [").append(response.getIndex())
                                .append("], type [").append(response.getType())
                                .append("], id [").append(response.getId())
                                .append("], message [").append(response.getFailureMessage())
                                .append("]");
                        log.warn(sb.toString());
                    } catch (Exception e) {
                        log.warn("Print es bulk error failed: {}", e.toString());
                        if (log.isDebugEnabled())
                            log.debug("ES bulk error: {}", responses.buildFailureMessage());
                    }
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("ES bulk got exception: {}", failure.toString());
            }
        };

        bulkProcessor = BulkProcessor.builder(
                (request, bulkListener) -> client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                listener, "ES-bulk-processor")
                .setBulkActions(bulkActions)
                .setBulkSize(new ByteSizeValue(bulkSize, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(flushInterval))
                .setConcurrentRequests(concurrentRequests)
                .build();
    }

    @PreDestroy
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
    }

    public RestHighLevelClient getClient() {
        return this.client;
    }

    // Create Index
    public boolean createIndex(String indexName) {
        if (StringUtils.isEmpty(indexName)) {
            return false;
        }
        try {
            org.elasticsearch.client.indices.CreateIndexRequest request = new CreateIndexRequest(indexName);
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            return createIndexResponse.isAcknowledged();
        } catch (Exception e) {
            log.error("Create index error: " + e.toString());
            return false;
        }
    }

    // Delete Index
    public boolean deleteIndex(String indexName) {
        if (StringUtils.isEmpty(indexName)) {
            return false;
        }
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse deleteIndexResponse = client.indices()
                    .delete(request, RequestOptions.DEFAULT);
            return deleteIndexResponse.isAcknowledged();
        } catch (Exception ex) {
            log.error("Delete index {} error: {}", indexName, ex);
            return false;
        }
    }

    // Define index mapping
    public void createIndexMapping(String index, XContentBuilder contentBuilder) {
        PutMappingRequest request = new PutMappingRequest(index);
        request.source(contentBuilder);
        try {
            client.indices().putMapping(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Put mapping error: " + e.toString());
        }
    }

    public BulkProcessor getBulkRequest() {
        return this.bulkProcessor;
    }

    public IndexRequest getIndexRequest(String index, String jsonSource) {
        return new IndexRequest(index).source(jsonSource);
    }

    // Get data
    public List<Integer> getSearchData(String index, QueryBuilder queryBuilder, int size) {
        List<Integer> ids = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.size(size);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit searchHit : searchHits) {
                Integer id = (Integer) searchHit.getSourceAsMap().get("id");
                ids.add(id);
            }
        } catch (IOException e) {
            log.error(e);
        }
        return ids;
    }

    // Get data by scroll
    public List<Integer> getSearchDataByScrolls(String index, QueryBuilder queryBuilder) {
        List<Integer> ids = new ArrayList<>();
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            while (searchHits != null && searchHits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
                for (SearchHit searchHit : searchHits) {
                    Integer id = (Integer) searchHit.getSourceAsMap().get("id");
                    ids.add(id);
                }
            }
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
        } catch (Exception ex) {
            log.error(ex);
        }
        return ids;
    }

    // Get results from aggregation
    public Map<String, String> getAggSearchResult(String index, QueryBuilder queryBuilder,
                                                  AggregationBuilder aggregationBuilder, String aggName) {
        Map<String, String> resultsMap = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get(aggName);
            if (terms != null) {
                for (Terms.Bucket entry : terms.getBuckets()) {
                    resultsMap.put(entry.getKey().toString(), String.valueOf(entry.getDocCount()));
                }
            }
        } catch (Exception ex) {
            log.error(ex);
        }

        return resultsMap;
    }

    // Get results from date histogram aggregation
    public Map<String, String> getDateHistogramAggSearchResult(String index, QueryBuilder queryBuilder,
                                                               DateHistogramAggregationBuilder aggregationBuilder, String aggName) {
        Map<String, String> resultsMap = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            ParsedDateHistogram dateHistogram = searchResponse.getAggregations().get(aggName);
            if (dateHistogram != null) {
                for (Histogram.Bucket entry : dateHistogram.getBuckets()) {
                    resultsMap.put(entry.getKey().toString(), String.valueOf(entry.getDocCount()));
                }
            }
        } catch (Exception ex) {
            log.error(ex);
        }

        return resultsMap;
    }

    public Map<String, String> getSumAggSearchOrderResult(String index, QueryBuilder queryBuilder,
                                                          AggregationBuilder aggregationBuilder, String aggName, String subAggName) {
        Map<String, String> resultsMap = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get(aggName);
            if (terms != null) {
                for (Terms.Bucket entry : terms.getBuckets()) {
                    String fieldName = entry.getKey().toString();
                    // long fieldCount = entry.getDocCount();
                    Sum maxagg = entry.getAggregations().get(subAggName);
                    double fieldValue = maxagg.getValue();
                    // System.out.println(String.format("%s %s %s", fieldName, fieldCount, fieldValue));
                    resultsMap.put(fieldName, String.valueOf(fieldValue));
                }
            }
        } catch (Exception ex) {
            log.error(ex);
        }
        return resultsMap;
    }

    public Map<String, String> getMaxAggSearchOrderResult(String index, QueryBuilder queryBuilder,
                                                          AggregationBuilder aggregationBuilder, String aggName, String subAggName) {
        Map<String, String> resultsMap = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get(aggName);
            if (terms != null) {
                for (Terms.Bucket entry : terms.getBuckets()) {
                    String fieldName = entry.getKey().toString();
                    // long fieldCount = entry.getDocCount();
                    Max maxagg = entry.getAggregations().get(subAggName);
                    double fieldValue = maxagg.getValue();
                    // System.out.println(String.format("%s %s %s", fieldName, fieldCount, fieldValue));
                    resultsMap.put(fieldName, String.valueOf(fieldValue));
                }
            }
        } catch (Exception ex) {
            log.error(ex);
        }
        return resultsMap;
    }

}
