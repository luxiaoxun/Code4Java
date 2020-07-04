package com.luxx.service;

import com.luxx.model.PoiData;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SolrIndexService {
    private static Logger log = org.slf4j.LoggerFactory
            .getLogger(SolrIndexService.class);

    private SolrClient solrClient = null;

    private final String DEFAULT_URL = "http://localhost:8983/solr/geo_core";

    public SolrIndexService() {
        solrClient = new HttpSolrClient(DEFAULT_URL);
    }

    // Field Name
    private static final String IDFieldName = "id";
    private static final String AddressFieldName = "address";
    private static final String LatFieldName = "lat";
    private static final String LngFieldName = "lng";
    private static final String GeoFieldName = "geoField";

    // 添加POI数据到Solr索引中
    public void indexPoiDataList(List<PoiData> dataList) {
        Collection<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
        for (PoiData data : dataList) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField(IDFieldName, data.getId());
            doc.addField(AddressFieldName, data.getAddress());
            doc.addField(LatFieldName, data.getLat());
            doc.addField(LngFieldName, data.getLng());
            String posString = data.getLng() + " " + data.getLat();
            doc.addField(GeoFieldName, posString);
            docList.add(doc);
        }
        try {
            solrClient.add(docList);
            solrClient.commit();
        } catch (SolrServerException e) {
            log.error(e.toString());
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public List<PoiData> getDataInCircle(double lng, double lat, double radius) {
        List<PoiData> results = new ArrayList<>();
        ;
        SolrQuery params = new SolrQuery();
        params.set("q", "*:*");
        params.set("fq", "{!geofilt}");        //距离过滤函数 
        String centerStr = String.valueOf(lng) + " " + String.valueOf(lat);
        params.set("pt", centerStr);
        params.set("sfield", GeoFieldName); //经纬度的字段  
        params.set("d", String.valueOf(radius)); //就近 d km 的所有数据    
        params.set("sort", "geodist() asc");  //根据距离排序：由近到远   
        params.set("start", "0");  //记录开始位置  
        params.set("rows", "100");  //查询的行数 
        params.set("fl", "*,_dist_:geodist(),score");

        results = getQueryResult(params);

        return results;
    }

    public List<PoiData> getDataInCircleByAddress(double lng, double lat, double radius, String address) {
        List<PoiData> results = new ArrayList<>();
        SolrQuery params = new SolrQuery();
        params.set("q", AddressFieldName + ":" + address);
        params.set("fq", "{!geofilt}");        //距离过滤函数 
        String centerStr = String.valueOf(lng) + " " + String.valueOf(lat);
        params.set("pt", centerStr);
        params.set("sfield", GeoFieldName); //经纬度的字段  
        params.set("d", String.valueOf(radius)); //就近 d km 的所有数据    
        params.set("sort", "geodist() asc");  //根据距离排序：由近到远   
        params.set("start", "0");  //记录开始位置  
        params.set("rows", "100");  //查询的行数 
        params.set("fl", "*,_dist_:geodist(),score");

        results = getQueryResult(params);

        return results;
    }

    public List<PoiData> getQueryResult(SolrQuery query) {
        List<PoiData> results = new ArrayList<>();
        try {
            QueryResponse response = null;
            response = solrClient.query(query);
            SolrDocumentList list = response.getResults();
            for (SolrDocument solrDocument : list) {
                PoiData data = new PoiData();
                data.setId((long) solrDocument.getFieldValue(IDFieldName));
                data.setAddress((String) solrDocument.getFieldValue(AddressFieldName));
                data.setLat((double) solrDocument.getFieldValue(LatFieldName));
                data.setLng((double) solrDocument.getFieldValue(LngFieldName));
            }
        } catch (IOException e) {
            log.error(e.toString());
        } catch (SolrServerException e) {
            log.error(e.toString());
        }
        return results;
    }

}



