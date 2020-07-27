package com.luxx.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.luxx.client.ElasticSearchClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import com.luxx.model.EndpointData;

public class RedshiftIndexService {
    private static Logger log = LogManager.getLogger(RedshiftIndexService.class);

    private ElasticSearchClient client = ElasticSearchClient.getInstance();

    // Index Name
    private static final String Index = "endpoint";
    // Type Name
    private static final String Type = "endpointdata";

    private static final String DateFormat = "yyyy-MM-dd HH:mm:ss";

    public void close() {
        client.close();
    }

    public void deleteIndex() {
        client.deleteIndex(Index);
    }

    public void createIndex() {
        client.createIndex(Index);
    }

    public void defineIndexTypeMapping() {
        XContentBuilder mapBuilder = prepareMappingBuilder();
        client.defineIndexTypeMapping(Index, Type, mapBuilder);
    }

    public XContentBuilder prepareMappingBuilder() {
        XContentBuilder mapBuilder = null;
        try {
            mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject().startObject(Type).startObject("properties").startObject("org_id")
                    .field("type", "integer").field("include_in_all", "false").endObject().startObject("endpoint_id")
                    .field("type", "long").field("include_in_all", "false").endObject().startObject("ds_bytes")
                    .field("type", "long").field("include_in_all", "false").endObject().startObject("ds_max_bps")
                    .field("type", "double").field("include_in_all", "false").endObject().startObject("ds_avg_bps")
                    .field("type", "double").field("include_in_all", "false").endObject().startObject("ds_mwt")
                    .field("type", "integer").field("include_in_all", "false").endObject().startObject("us_bytes")
                    .field("type", "long").field("include_in_all", "false").endObject().startObject("us_max_bps")
                    .field("type", "double").field("include_in_all", "false").endObject().startObject("us_avg_bps")
                    .field("type", "double").field("include_in_all", "false").endObject().startObject("us_mwt")
                    .field("type", "integer").field("include_in_all", "false").endObject().startObject("date_time")
                    .field("type", "date").field("include_in_all", "false").field("format", DateFormat).endObject().endObject()
                    .endObject().endObject();
        } catch (IOException e) {
            log.error(e);
        }

        return mapBuilder;
    }

    public String getIndexDataFromHotspotDataForRedshift(EndpointData data) {
        String jsonString = null;
        if (data != null) {
            try {
                XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
                jsonBuilder.startObject().field("org_id", data.getOrg_id()).field("endpoint_id", data.getEndpoint_id())
                        .field("ds_bytes", data.getDs_bytes()).field("ds_max_bps", data.getDs_max_bytes())
                        .field("ds_avg_bps", data.getDs_avg_bytes()).field("ds_mwt", data.getDs_mwt())
                        .field("us_bytes", data.getUs_bytes()).field("us_max_bps", data.getUs_max_bytes())
                        .field("us_avg_bps", data.getUs_avg_bytes()).field("us_mwt", data.getUs_mwt())
                        .field("date_time", data.getDate_time()).endObject();
                jsonString = jsonBuilder.string();
            } catch (IOException e) {
                log.error(e);
            }
        }

        return jsonString;
    }

    // Index data in bulk
    public void indexHotSpotDataListForRedshift(List<EndpointData> dataList) {
        if (dataList != null) {
            int size = dataList.size();
            if (size > 0) {
                BulkRequestBuilder bulkRequest = client.getBulkRequest();
                for (int i = 0; i < size; ++i) {
                    EndpointData data = dataList.get(i);
                    String jsonSource = getIndexDataFromHotspotDataForRedshift(data);
                    if (jsonSource != null) {
                        bulkRequest.add(client.getIndexRequest(Index, Type, jsonSource));
                    }
                }

                BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                if (bulkResponse.hasFailures()) {
                    Iterator<BulkItemResponse> iter = bulkResponse.iterator();
                    while (iter.hasNext()) {
                        BulkItemResponse itemResponse = iter.next();
                        if (itemResponse.isFailed()) {
                            log.error(itemResponse.getFailureMessage());
                        }
                    }
                }
            }
        }
    }

    private QueryBuilder getDateRangeQueryBuilder(String startTime, String endTime) {
        QueryBuilder rangeBuilder = QueryBuilders.matchAllQuery();

        if (startTime != null && endTime != null) {
            rangeBuilder = QueryBuilders.rangeQuery("date_time").from(startTime).to(endTime).format(DateFormat);
        }

        return rangeBuilder;
    }

    // Get top N endpoint_id based on sum of usage(ds_bytes) from startTime to endTime
    public Map<String, String> getTopNEndpointUsage(String startTime, String endTime, int orgId, int topN) {
        Map<String, String> resultsMap = new HashMap<>();

        QueryBuilder orgIdQueryBuilder = QueryBuilders.termQuery("org_id", orgId);
        QueryBuilder endpointIdFilterBuilder = QueryBuilders.termQuery("endpoint_id", 0);
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(orgIdQueryBuilder).must(rangeBuilder)
                .mustNot(endpointIdFilterBuilder);
        TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("endpointUsageAgg").field("endpoint_id")
                .size(topN);
        termsBuilder.subAggregation(AggregationBuilders.sum("sum_usage").field("ds_bytes"));
        termsBuilder.order(BucketOrder.aggregation("sum_usage", false));

        resultsMap = client.getSumAggSearchOrderResult(Index, queryBuilder, termsBuilder, "endpointUsageAgg", "sum_usage");

        return resultsMap;
    }

    // Get endpoint_id and max_bps based on query from startTime to endTime
    public Map<String, String> getEndpointMaxbps(String startTime, String endTime, int orgId) {
        Map<String, String> resultsMap = new HashMap<>();

        QueryBuilder orgIdQueryBuilder = QueryBuilders.termQuery("org_id", orgId);
        QueryBuilder endpointIdFilterBuilder = QueryBuilders.termQuery("endpoint_id", 0);
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);
        QueryBuilder maxBpsRangeBuilder = QueryBuilders.rangeQuery("ds_max_bps").from(4000000);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(orgIdQueryBuilder).must(rangeBuilder)
                .mustNot(endpointIdFilterBuilder).must(maxBpsRangeBuilder);
        TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("endpointMaxAgg").field("endpoint_id").size(Integer.MAX_VALUE);
        termsBuilder.subAggregation(AggregationBuilders.max("max_bps").field("ds_max_bps"));

        resultsMap = client.getMaxAggSearchOrderResult(Index, queryBuilder, termsBuilder, "endpointMaxAgg", "max_bps");

        return resultsMap;
    }

}
