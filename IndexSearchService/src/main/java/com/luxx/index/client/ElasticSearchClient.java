package com.luxx.index.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.luxx.index.util.HostAndPort;
import com.luxx.index.util.PropertiesReader;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElasticSearchClient {
    private static Logger log = LogManager.getLogger(ElasticSearchClient.class);

    // ES Client
    private TransportClient client;

    private static class ElasticSearchClientHolder {
        static final ElasticSearchClient instance = new ElasticSearchClient();
    }

    public static ElasticSearchClient getInstance() {
        return ElasticSearchClientHolder.instance;
    }

    private ElasticSearchClient() {
        PropertiesReader reader = new PropertiesReader("/config.properties");
        String address = reader.getProperty("es.server.address");
        List<HostAndPort> hostAndPortList = parseHosts(address);
        try {
            if (hostAndPortList != null) {
                for (HostAndPort hostAndPort : hostAndPortList) {
                    client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
                            new TransportAddress(InetAddress.getByName(hostAndPort.getHost()),
                                    hostAndPort.getPort()));
                }
            } else {
                log.error("No elasticsearch address found in config file.");
            }
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
    }

    private List<HostAndPort> parseHosts(String envHosts) {
        if (envHosts != null && envHosts.length() > 0) {
            String[] hostDefs = envHosts.split(",");
            if (hostDefs != null && hostDefs.length >= 1) {
                List<HostAndPort> envHostsAndPorts = new ArrayList<HostAndPort>(hostDefs.length);
                for (String hostDef : hostDefs) {
                    String[] hostAndPortParts = HostAndPort.extractParts(hostDef);
                    if (hostAndPortParts != null && hostAndPortParts.length == 2) {
                        String host = hostAndPortParts[0];
                        int port = 9300;
                        try {
                            port = Integer.parseInt(hostAndPortParts[1]);
                        } catch (final NumberFormatException nfe) {
                            log.warn("Parse elasticsearch port error. Using default 9300.");
                        }
                        envHostsAndPorts.add(new HostAndPort(host, port));
                    }
                }
                return envHostsAndPorts;
            }
        }
        return null;
    }

    public ElasticSearchClient(String ipAddress, int port) {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(ipAddress), port));
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }

    // Create Index
    public void createIndex(String index) {
        IndicesExistsResponse indicesExistsResponse = client.admin().indices()
                .exists(new IndicesExistsRequest(new String[]{index})).actionGet();
        if (!indicesExistsResponse.isExists()) {
            client.admin().indices().create(new CreateIndexRequest(index)).actionGet();
        }
    }

    // Delete Index
    public void deleteIndex(String index) {
        IndicesExistsResponse indicesExistsResponse = client.admin().indices()
                .exists(new IndicesExistsRequest(new String[]{index})).actionGet();
        if (indicesExistsResponse.isExists()) {
            client.admin().indices().delete(new DeleteIndexRequest(index)).actionGet();
        }
    }

    // Delete Type
    public void deleteType(String index, String type) {
        client.prepareDelete().setIndex(index).setType(type).execute().actionGet();
    }

    private boolean isMappingExist(String index, String type) {
        ClusterState cs = client.admin().cluster().prepareState().setIndices(index).execute().actionGet().getState();
        // Check index metadata existence.
        IndexMetaData imd = cs.getMetaData().index(index);
        if (imd == null) {
            return false;
        }
        // Check mapping metadata existence.
        MappingMetaData mdd = imd.mapping(type);
        if (mdd != null) {
            return true;
        }
        return false;
    }

    // Define type mapping
    public void defineIndexTypeMapping(String index, String type, XContentBuilder mapBuilder) {
        if (!isMappingExist(index, type)) {
            PutMappingRequest putMappingRequest = Requests.putMappingRequest(index).type(type).source(mapBuilder);
            client.admin().indices().putMapping(putMappingRequest).actionGet();
        }
    }

    public BulkRequestBuilder getBulkRequest() {
        return client.prepareBulk();
    }

    public IndexRequestBuilder getIndexRequest(String index, String type, String jsonSource) {
        return client.prepareIndex(index, type).setSource(jsonSource);
    }

    // Index data
    public boolean indexData(String index, String type, String jsonSource) {
        if (jsonSource != null) {
            IndexRequestBuilder requestBuilder = client.prepareIndex(index, type);
            requestBuilder.setSource(jsonSource).execute().actionGet();
            return true;
        }
        return false;
    }

    // Get data
    public List<Integer> getSearchData(String index, String type, QueryBuilder queryBuilder, int size) {
        List<Integer> ids = new ArrayList<>();
        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setQuery(queryBuilder).setSize(size)
                .execute().actionGet();
        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            Integer id = (Integer) searchHit.getSourceAsMap().get("id");
            ids.add(id);
        }
        return ids;
    }

    // Get data by scroll
    public List<Integer> getSearchDataByScrolls(String index, String type, QueryBuilder queryBuilder, int size) {
        List<Integer> ids = new ArrayList<>();
        SearchResponse scrollResp = client.prepareSearch(index).setTypes(type).setScroll(new TimeValue(60000))
                .setQuery(queryBuilder).setSize(size).execute().actionGet();
        while (true) {
            for (SearchHit searchHit : scrollResp.getHits().getHits()) {
                Integer id = (Integer) searchHit.getSourceAsMap().get("id");
                ids.add(id);
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute()
                    .actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }

        return ids;
    }

    // Get results of aggregation
    public Map<String, String> getAggSearchResult(String index, QueryBuilder queryBuilder,
                                                  AggregationBuilder aggregationBuilder, String aggName) {
        Map<String, String> resultsMap = new HashMap<>();
        SearchResponse searchResponse = client.prepareSearch(index).setQuery(queryBuilder)
                .addAggregation(aggregationBuilder).execute().actionGet();
        Terms terms = searchResponse.getAggregations().get(aggName);
        if (terms != null) {
            for (Terms.Bucket entry : terms.getBuckets()) {
                resultsMap.put(entry.getKey().toString(), String.valueOf(entry.getDocCount()));
            }
        }

        return resultsMap;
    }

    public Map<String, String> getSumAggSearchOrderResult(String index, QueryBuilder queryBuilder,
                                                          AggregationBuilder aggregationBuilder, String aggName, String subAggName) {
        Map<String, String> resultsMap = new HashMap<>();
        SearchResponse searchResponse = client.prepareSearch(index).setQuery(queryBuilder)
                .addAggregation(aggregationBuilder).execute().actionGet();
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

        return resultsMap;
    }

    public Map<String, String> getMaxAggSearchOrderResult(String index, QueryBuilder queryBuilder,
                                                          AggregationBuilder aggregationBuilder, String aggName, String subAggName) {
        Map<String, String> resultsMap = new HashMap<>();
        SearchResponse searchResponse = client.prepareSearch(index).setQuery(queryBuilder)
                .addAggregation(aggregationBuilder).execute().actionGet();
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

        return resultsMap;
    }

}
