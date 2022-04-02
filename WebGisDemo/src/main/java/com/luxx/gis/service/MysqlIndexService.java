package com.luxx.gis.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.luxx.gis.client.ElasticSearchClient;
import com.luxx.gis.model.HotspotData;

import com.luxx.gis.util.JsonUtil;
import org.elasticsearch.action.bulk.BulkProcessor;
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
                    contentBuilder.startObject(IDFieldName).field("type", "long").endObject();
                    contentBuilder.startObject(SeqNumFieldName).field("type", "long").endObject();
                    contentBuilder.startObject(IMSIFieldName).field("type", "keyword").endObject();
                    contentBuilder.startObject(IMEIFieldName).field("type", "keyword").endObject();
                    contentBuilder.startObject(DeviceIDFieldName).field("type", "keyword").endObject();
                    contentBuilder.startObject(OwnAreaFieldName).field("type", "keyword").endObject();
                    contentBuilder.startObject(TeleOperFieldName).field("type", "keyword").endObject();
                    contentBuilder.startObject(TimeFieldName).field("type", "date").field("format", DateFormat).endObject();
                }
                contentBuilder.endObject();
            }
            contentBuilder.endObject();
        } catch (IOException e) {
            log.error(e);
        }
        return contentBuilder;
    }

    // Build json data
    public String getIndexDataFromHotspotData(HotspotData data) {
        Map<String, Object> map = new HashMap<>();
        if (data != null) {
            map.put(IDFieldName, data.getId());
            map.put(SeqNumFieldName, data.getSeqNum());
            map.put(IMSIFieldName, data.getImsi());
            map.put(IMEIFieldName, data.getImei());
            map.put(DeviceIDFieldName, data.getDeviceID());
            map.put(OwnAreaFieldName, data.getOwnArea());
            map.put(TeleOperFieldName, data.getTeleOper());
            map.put(TimeFieldName, data.getCollectTime());
        }
        return JsonUtil.objectToJson(map);
    }

    // Index data in bulk
    public void indexHotSpotDataList(List<HotspotData> dataList) {
        if (dataList != null && dataList.size() > 0) {
            BulkProcessor bulkRequest = elasticSearchClient.getBulkRequest();
            for (int i = 0; i < dataList.size(); ++i) {
                HotspotData data = dataList.get(i);
                String jsonSource = getIndexDataFromHotspotData(data);
                if (jsonSource != null) {
                    bulkRequest.add(elasticSearchClient.getIndexRequest(Index, jsonSource));
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
        return elasticSearchClient.getSearchDataByScrolls(Index, rangeBuilder);
    }

    // Get count histogram based on date from startTime to endTime
    public Map<String, String> getDeviceDateFacetDistributeInfo(String startTime, String endTime,
                                                                List<String> deviceList, final String gap) {
        Map<String, String> resultsMap = new HashMap<>();
        QueryBuilder deviceQueryBuilder = getDeviceQueryBuilder(deviceList);
        QueryBuilder rangeBuilder = getDateRangeQueryBuilder(startTime, endTime);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(deviceQueryBuilder).must(rangeBuilder);

        DateHistogramAggregationBuilder dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                .fixedInterval(DateHistogramInterval.YEAR);
        switch (gap) {
            case "Quarter":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .fixedInterval(DateHistogramInterval.QUARTER);
                break;
            case "Month":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .fixedInterval(DateHistogramInterval.MONTH);
                break;
            case "Week":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .fixedInterval(DateHistogramInterval.WEEK);
                break;
            case "Day":
                dateHistogramAggBuilder = AggregationBuilders.dateHistogram("dateAgg").field(TimeFieldName)
                        .fixedInterval(DateHistogramInterval.DAY);
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
