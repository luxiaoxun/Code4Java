package com.luxx.gis.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.luxx.gis.client.ElasticSearchClient;
import com.luxx.gis.model.EndpointData;
import com.luxx.gis.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@ConditionalOnProperty(name = "index.es.cluster.address")
public class RedshiftIndexService {
    private static Logger log = LogManager.getLogger(RedshiftIndexService.class);

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    // Index Name
    private static final String Index = "endpoint";

    private static final String DateFormat = "yyyy-MM-dd HH:mm:ss";

    private static final String TimeField = "date_time";

    public void deleteIndex() {
        elasticSearchClient.deleteIndex(Index);
    }

    public void createIndex() {
        elasticSearchClient.createIndex(Index);
    }

    public void createIndexMapping() {
        XContentBuilder contentBuilder = prepareMappingBuilder();
        elasticSearchClient.createIndexMapping(Index, contentBuilder);
    }

    public XContentBuilder prepareMappingBuilder() {
        XContentBuilder contentBuilder = null;
        try {
            contentBuilder = XContentFactory.jsonBuilder();
            contentBuilder.startObject();
            {
                contentBuilder.startObject("properties");
                {
                    contentBuilder.startObject("org_id").field("type", "integer").endObject();
                    contentBuilder.startObject("endpoint_id").field("type", "long").endObject();
                    contentBuilder.startObject("ds_bytes").field("type", "long").endObject();
                    contentBuilder.startObject("ds_max_bps").field("type", "double").endObject();
                    contentBuilder.startObject("ds_avg_bps").field("type", "double").endObject();
                    contentBuilder.startObject("ds_mwt").field("type", "integer").endObject();
                    contentBuilder.startObject("us_bytes").field("type", "long").endObject();
                    contentBuilder.startObject("us_max_bps").field("type", "double").endObject();
                    contentBuilder.startObject("us_avg_bps").field("type", "double").endObject();
                    contentBuilder.startObject("us_mwt").field("type", "integer").endObject();
                    contentBuilder.startObject(TimeField).field("type", "date").field("format", DateFormat).endObject();
                }
                contentBuilder.endObject();
            }
            contentBuilder.endObject();
        } catch (IOException e) {
            log.error(e);
        }

        return contentBuilder;
    }

    public String getIndexDataFromHotspotDataForRedshift(EndpointData data) {
        Map<String, Object> map = new HashMap<>();
        if (data != null) {
            map.put("org_id", data.getOrg_id());
            map.put("endpoint_id", data.getEndpoint_id());
            map.put("ds_bytes", data.getDs_bytes());
            map.put("ds_max_bps", data.getDs_max_bytes());
            map.put("ds_avg_bps", data.getDs_avg_bytes());
            map.put("ds_mwt", data.getDs_mwt());
            map.put("us_bytes", data.getUs_bytes());
            map.put("us_max_bps", data.getUs_max_bytes());
            map.put("us_avg_bps", data.getUs_avg_bytes());
            map.put("us_mwt", data.getUs_mwt());
            map.put(TimeField, data.getDate_time());
        }
        return JsonUtil.objectToJson(map);
    }

    // Index data in bulk
    public void indexHotSpotDataListForRedshift(List<EndpointData> dataList) {
        if (dataList != null && dataList.size() > 0) {
            BulkProcessor bulkRequest = elasticSearchClient.getBulkRequest();
            for (int i = 0; i < dataList.size(); ++i) {
                EndpointData data = dataList.get(i);
                String jsonSource = getIndexDataFromHotspotDataForRedshift(data);
                if (jsonSource != null) {
                    bulkRequest.add(elasticSearchClient.getIndexRequest(Index, jsonSource));
                }
            }
        }
    }

    private QueryBuilder getDateRangeQueryBuilder(String startTime, String endTime) {
        QueryBuilder rangeBuilder = QueryBuilders.matchAllQuery();
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            rangeBuilder = QueryBuilders.rangeQuery(TimeField).from(startTime).to(endTime).format(DateFormat);
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

        resultsMap = elasticSearchClient.getSumAggSearchOrderResult(Index, queryBuilder, termsBuilder, "endpointUsageAgg", "sum_usage");

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

        resultsMap = elasticSearchClient.getMaxAggSearchOrderResult(Index, queryBuilder, termsBuilder, "endpointMaxAgg", "max_bps");
        return resultsMap;
    }

}
