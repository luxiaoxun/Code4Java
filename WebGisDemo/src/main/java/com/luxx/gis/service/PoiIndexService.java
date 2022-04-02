package com.luxx.gis.service;

import com.luxx.gis.client.ElasticSearchClient;
import com.luxx.gis.model.PoiData;
import com.luxx.gis.util.JsonUtil;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.util.QueryBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PoiIndexService {
    private static Logger log = LogManager.getLogger(PoiIndexService.class);

    // Field Name
    private static final String IDFieldName = "id";
    private static final String AddressFieldName = "address";
    private static final String LatFieldName = "lat";
    private static final String LngFieldName = "lng";
    private static final String LocationFieldName = "poiPoint";

    private static final String IndexName = "geo_location";

    private final int maxResultCount = 100;

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    public void clear() {
        elasticSearchClient.deleteIndex(IndexName);
    }

    public void createIndex() {
        elasticSearchClient.createIndex(IndexName);
    }

    public void createIndexMapping() {
        XContentBuilder contentBuilder = prepareMappingBuilder();
        elasticSearchClient.createIndexMapping(IndexName, contentBuilder);
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
                    contentBuilder.startObject(AddressFieldName).field("type", "keyword").endObject();
                    contentBuilder.startObject(LocationFieldName).field("type", "geo_point").endObject();
                }
                contentBuilder.endObject();
            }
            contentBuilder.endObject();
        } catch (IOException e) {
            log.error(e);
        }
        return contentBuilder;
    }

    public boolean indexPoiDataList(List<PoiData> dataList) {
        try {
            if (dataList != null && dataList.size() > 0) {
                BulkProcessor bulkRequest = elasticSearchClient.getBulkRequest();
                for (int i = 0; i < dataList.size(); ++i) {
                    PoiData data = dataList.get(i);
                    String jsonSource = JsonUtil.objectToJson(data);
                    if (jsonSource != null) {
                        bulkRequest.add(elasticSearchClient.getIndexRequest(IndexName, jsonSource));
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    public List<PoiData> searchPoiInRectangle(double minLng, double minLat, double maxLng, double maxLat) {
        List<PoiData> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(IndexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        GeoBoundingBoxQueryBuilder geoBoundingBoxQueryBuilder = QueryBuilders.geoBoundingBoxQuery(LocationFieldName)
                .setCorners(maxLat, minLng, minLat, maxLng);
        sourceBuilder.query(geoBoundingBoxQueryBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = elasticSearchClient.getClient().search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit searchHit : searchHits) {
                Long id = (Long) searchHit.getSourceAsMap().get(IDFieldName);
                String address = (String) searchHit.getSourceAsMap().get(AddressFieldName);
                GeoPoint geoPoint = (GeoPoint) searchHit.getSourceAsMap().get(LocationFieldName);
                PoiData poiData = new PoiData(id, address, geoPoint.getLat(), geoPoint.getLon());
                results.add(poiData);
            }
        } catch (Exception e) {
            log.error("Search Poi in rectangle error: " + e.toString());
        }
        return results;
    }

    public List<PoiData> searchPoiInCircle(double lng, double lat, String distanceKm) {
        List<PoiData> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(IndexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        GeoDistanceQueryBuilder geoDistanceQueryBuilder = QueryBuilders.geoDistanceQuery(LocationFieldName)
                .distance(distanceKm, DistanceUnit.KILOMETERS)
                .point(lat, lng);
        sourceBuilder.query(geoDistanceQueryBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = elasticSearchClient.getClient().search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit searchHit : searchHits) {
                Long id = (Long) searchHit.getSourceAsMap().get(IDFieldName);
                String address = (String) searchHit.getSourceAsMap().get(AddressFieldName);
                GeoPoint geoPoint = (GeoPoint) searchHit.getSourceAsMap().get(LocationFieldName);
                PoiData poiData = new PoiData(id, address, geoPoint.getLat(), geoPoint.getLon());
                results.add(poiData);
            }
        } catch (Exception e) {
            log.error("Search Poi in circle error: " + e.toString());
        }

        return results;
    }

    public List<PoiData> searchPoiInCircleAndAddress(double lng, double lat, String distanceKm, String address) {
        List<PoiData> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(IndexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        GeoDistanceQueryBuilder geoDistanceQueryBuilder = QueryBuilders.geoDistanceQuery(LocationFieldName)
                .distance(distanceKm, DistanceUnit.KILOMETERS)
                .point(lat, lng);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(AddressFieldName, address);
        sourceBuilder.query(geoDistanceQueryBuilder).query(termQueryBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = elasticSearchClient.getClient().search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit searchHit : searchHits) {
                Long id = (Long) searchHit.getSourceAsMap().get(IDFieldName);
                String addr = (String) searchHit.getSourceAsMap().get(AddressFieldName);
                GeoPoint geoPoint = (GeoPoint) searchHit.getSourceAsMap().get(LocationFieldName);
                PoiData poiData = new PoiData(id, addr, geoPoint.getLat(), geoPoint.getLon());
                results.add(poiData);
            }
        } catch (Exception e) {
            log.error("Search Poi in circle and address error: " + e.toString());
        }

        return results;
    }

}
