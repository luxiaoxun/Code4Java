package com.luxx.index.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luxx.index.client.ElasticSearchClient;
import com.luxx.index.model.HotspotData;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "index.es.cluster.address")
public class MysqlIndexService {
    private static Logger log = LogManager.getLogger(MysqlIndexService.class);

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    // Index Name
    private static final String Index = "hotspot";
    // Type Name
    private static final String Type = "hotspotdata";

    // Field Name
    private static final String TimeFieldName = "CollectTime";
    private static final String IDFieldName = "id";
    private static final String IMSIFieldName = "IMSI";
    private static final String IMEIFieldName = "IMEI";
    private static final String DeviceIDFieldName = "DeviceID";
    private static final String OwnAreaFieldName = "OwnArea";
    private static final String TeleOperFieldName = "TeleOper";
    private static final String SeqNumFieldName = "SeqNum";

    private static final String DateFormat = "yyyy-MM-dd HH:mm:ss";

    public void close() {
        elasticSearchClient.close();
    }

    public void deleteIndex() {
        elasticSearchClient.deleteIndex(Index);
    }

    public void createIndex() {
        elasticSearchClient.createIndex(Index);
    }

    public void defineIndexTypeMapping() {
        XContentBuilder mapBuilder = prepareMappingBuilder();
        elasticSearchClient.defineIndexTypeMapping(Index, Type, mapBuilder);
    }

    public XContentBuilder prepareMappingBuilder() {
        XContentBuilder mapBuilder = null;
        try {
            mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject().startObject(Type).startObject("properties").startObject(IDFieldName)
                    .field("type", "long").field("include_in_all", "false").endObject().startObject(SeqNumFieldName)
                    .field("type", "long").field("include_in_all", "false").endObject().startObject(IMSIFieldName)
                    .field("type", "text").field("index", "not_analyzed").field("include_in_all", "false").endObject()
                    .startObject(IMEIFieldName).field("type", "text").field("index", "not_analyzed")
                    .field("include_in_all", "false").endObject().startObject(DeviceIDFieldName).field("type", "text")
                    .field("index", "not_analyzed").field("include_in_all", "false").endObject()
                    .startObject(OwnAreaFieldName).field("type", "text").field("index", "not_analyzed")
                    .field("include_in_all", "false").endObject().startObject(TeleOperFieldName).field("type", "text")
                    .field("index", "not_analyzed").field("include_in_all", "false").endObject()
                    .startObject(TimeFieldName).field("type", "date").field("format", DateFormat)
                    .field("include_in_all", "false").endObject().endObject().endObject().endObject();
        } catch (IOException e) {
            log.error(e);
        }

        return mapBuilder;
    }

    //https://stackoverflow.com/questions/27427613/elasticsearch-java-api-putmapping-from-json-file-error
    public static XContentBuilder builderFromJson(String json) throws JsonParseException, JsonMappingException, IOException {
        Map<String, Object> map = new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {
        });
        return XContentFactory.jsonBuilder().map(map);
    }

    // Build json data
    public String getIndexDataFromHotspotData(HotspotData data) {
        String jsonString = null;
        if (data != null) {
            try {
                XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
                jsonBuilder.startObject().field(IDFieldName, data.getId()).field(SeqNumFieldName, data.getSeqNum())
                        .field(IMSIFieldName, data.getImsi()).field(IMEIFieldName, data.getImei())
                        .field(DeviceIDFieldName, data.getDeviceID()).field(OwnAreaFieldName, data.getOwnArea())
                        .field(TeleOperFieldName, data.getTeleOper()).field(TimeFieldName, data.getCollectTime())
                        .endObject();
                jsonString = jsonBuilder.string();
            } catch (IOException e) {
                log.error(e);
            }
        }

        return jsonString;
    }

    // Index data in bulk
    public void indexHotSpotDataList(List<HotspotData> dataList) {
        if (dataList != null) {
            int size = dataList.size();
            if (size > 0) {
                BulkRequestBuilder bulkRequest = elasticSearchClient.getBulkRequest();
                for (int i = 0; i < size; ++i) {
                    HotspotData data = dataList.get(i);
                    String jsonSource = getIndexDataFromHotspotData(data);
                    if (jsonSource != null) {
                        bulkRequest.add(elasticSearchClient.getIndexRequest(Index, Type, jsonSource));
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

    // Build query based on device List
    private QueryBuilder getDeviceQueryBuilder(List<String> deviceList) {
        QueryBuilder deviceQueryBuilder = QueryBuilders.matchAllQuery();
        if (deviceList != null && deviceList.size() > 0) {
            deviceQueryBuilder = QueryBuilders.termsQuery(DeviceIDFieldName, deviceList);
        }
        return deviceQueryBuilder;
    }

    // Build date time range query
    private QueryBuilder getDateRangeQueryBuilder(String startTime, String endTime) {
        QueryBuilder rangeBuilder = QueryBuilders.matchAllQuery();
        if (startTime != null && endTime != null) {
            rangeBuilder = QueryBuilders.rangeQuery(TimeFieldName).from(startTime).to(endTime).format(DateFormat);
        }

        return rangeBuilder;
    }

    // Get result from startTime to endTime
    public List<Integer> getSearchResultBetweenTime(String startTime, String endTime) {
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);
        return elasticSearchClient.getSearchDataByScrolls(Index, Type, rangeBuilder, 1000000);
    }

    // Get count histogram based on date from startTime to endTime
    public Map<String, String> getDeviceDateFacetDistributeInfo(String startTime, String endTime,
                                                                List<String> deviceList, final String gap) {

        Map<String, String> resultsMap = new HashMap<>();
        QueryBuilder deviceQueryBuilder = getDeviceQueryBuilder(deviceList);
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(deviceQueryBuilder).must(rangeBuilder);

        DateHistogramAggregationBuilder dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                .dateHistogramInterval(DateHistogramInterval.YEAR);
        switch (gap) {
            case "Quarter":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .dateHistogramInterval(DateHistogramInterval.QUARTER);
                break;
            case "Month":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .dateHistogramInterval(DateHistogramInterval.MONTH);
                break;
            case "Week":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .dateHistogramInterval(DateHistogramInterval.WEEK);
                break;
            case "Day":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .dateHistogramInterval(DateHistogramInterval.DAY);
                break;
        }

        resultsMap = elasticSearchClient.getDateHistogramAggSearchResult(Index, queryBuilder, dateHistogramAggBuilder, "dateAgg");
        return resultsMap;
    }

    // Get count aggregation based on device from startTime to endTime
    public Map<String, String> getDeviceDistributedInfo(String startTime, String endTime, List<String> deviceList) {

        Map<String, String> resultsMap = new HashMap<>();

        QueryBuilder deviceQueryBuilder = getDeviceQueryBuilder(deviceList);
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(deviceQueryBuilder).must(rangeBuilder);

        TermsAggregationBuilder termsAggBuilder = AggregationBuilders.terms("DeviceIDAgg").size(Integer.MAX_VALUE)
                .field(DeviceIDFieldName);
        resultsMap = elasticSearchClient.getAggSearchResult(Index, queryBuilder, termsAggBuilder, "DeviceIDAgg");
        return resultsMap;
    }

    // Get count aggregation based on OwnArea from startTime to endTime
    public Map<String, String> getOwnAreaDistributeInfo(String startTime, String endTime, List<String> deviceList,
                                                        String provinceName, String cityName) {
        Map<String, String> resultsMap = new HashMap<>();

        QueryBuilder deviceQueryBuilder = getDeviceQueryBuilder(deviceList);
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);

        QueryBuilder areaFieldQueryBuilder = QueryBuilders.matchAllQuery();
        if (provinceName != null && !provinceName.isEmpty()) {
            if (cityName != null && !cityName.isEmpty()) {
                areaFieldQueryBuilder = QueryBuilders.termQuery(OwnAreaFieldName, provinceName + " " + cityName);
            } else {
                areaFieldQueryBuilder = QueryBuilders.prefixQuery(OwnAreaFieldName, provinceName);
            }
        }

        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(deviceQueryBuilder).must(rangeBuilder)
                .must(areaFieldQueryBuilder);

        TermsAggregationBuilder termsAggBuilder = AggregationBuilders.terms("OwnFieldAgg").size(Integer.MAX_VALUE)
                .field(OwnAreaFieldName);

        resultsMap = elasticSearchClient.getAggSearchResult(Index, queryBuilder, termsAggBuilder, "OwnFieldAgg");
        return resultsMap;
    }

    // Get count aggregation based on TeleOper from startTime to endTime
    public Map<String, String> getTeleOperDistributeInfo(String startTime, String endTime, List<String> deviceList) {
        Map<String, String> resultsMap = new HashMap<>();

        QueryBuilder deviceQueryBuilder = getDeviceQueryBuilder(deviceList);
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);

        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(deviceQueryBuilder).must(rangeBuilder);

        TermsAggregationBuilder termsAggBuilder = AggregationBuilders.terms("TelOperFieldAgg").size(Integer.MAX_VALUE)
                .field(TeleOperFieldName);

        resultsMap = elasticSearchClient.getAggSearchResult(Index, queryBuilder, termsAggBuilder, "TelOperFieldAgg");
        return resultsMap;
    }
}
