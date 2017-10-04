package com.es.jdbc.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.es.jdbc.service.MysqlIndexService;

public class MysqlIndexServiceTest {

    public void TestGetDataBetweenTime() {
        MysqlIndexService indexService = new MysqlIndexService();
        // CollectTime:[2014-12-06T00:00:00Z TO 2014-12-10T21:31:55Z]
        // "2014-12-16T07:37:21.000Z"
        String startTime = "2014-12-06 00:00:00";
        // String endTime = "2014-12-11 21:31:55";
        String endTime = "2014-12-13 00:00:00";
        long s = System.currentTimeMillis();
        List<Integer> results = indexService.getSearchResultBetweenTime(startTime, endTime);
        if (results != null) {
            System.out.println("Size: " + results.size());
        }
        long e = System.currentTimeMillis();
        System.out.println("Time: " + (e - s));

        indexService.close();
    }

    public void TestDateFacet() {
        MysqlIndexService indexService = new MysqlIndexService();
        long s = System.currentTimeMillis();
        String startTime = "2015-01-01 00:00:00";
        String endTime = "2015-12-30 21:31:55";
        List<String> deviceList = new ArrayList<String>();
        deviceList.add("1011");
        deviceList.add("1001");
        deviceList.add("1007");
        Map<String, String> response = new HashMap<>();
        response = indexService.getDeviceDateFacetDistributeInfo(startTime, endTime, null, "Month");
        if (response != null) {
            for (String key : response.keySet()) {
                System.out.println(key + " : " + response.get(key));
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("Time: " + (e - s));
        indexService.close();
    }

    public void TestDeviceFacet() {
        MysqlIndexService indexService = new MysqlIndexService();
        long s = System.currentTimeMillis();
        String startTime = "2014-12-06 00:00:00";
        String endTime = "2014-12-10 21:31:55";
        List<String> deviceList = new ArrayList<String>();
        deviceList.add("1011");
        deviceList.add("1001");
        deviceList.add("1007");
        Map<String, String> response = new HashMap<>();
        response = indexService.getDeviceDistributedInfo(startTime, endTime, null);
        if (response != null) {
            // System.out.println("Search Results: "+response.size());
            for (String key : response.keySet()) {
                System.out.println(key + " : " + response.get(key));
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("Time: " + (e - s));
        indexService.close();
    }

    public void TestOwnAreaFacet() {
        MysqlIndexService indexService = new MysqlIndexService();
        long s = System.currentTimeMillis();
        String startTime = "2014-12-06 00:00:00";
        String endTime = "2014-12-10 21:31:55";
        List<String> deviceList = new ArrayList<String>();
        deviceList.add("1011");
        deviceList.add("1001");
        deviceList.add("1007");
        Map<String, String> response = new HashMap<>();
        // response = indexService.getOwnAreaDistributeInfo(startTime, endTime, null,"上海",null);
        // response = indexService.getOwnAreaDistributeInfo(startTime, endTime, null,"河北","唐山");
        response = indexService.getOwnAreaDistributeInfo(startTime, endTime, null, null, null);
        if (response != null) {
            // System.out.println("Search Results: "+response.size());
            int sum = 0;
            int count = 0;
            for (String key : response.keySet()) {
                System.out.println(key + " : " + response.get(key));
                sum += Integer.parseInt(response.get(key));
                ++count;
            }
            System.out.println("Count: " + count);
            System.out.println("Sum: " + sum);
        }

        long e = System.currentTimeMillis();
        System.out.println("Time: " + (e - s));
        indexService.close();
    }

    public void TestTelOperFieldFacet() {
        MysqlIndexService indexService = new MysqlIndexService();
        long s = System.currentTimeMillis();
        String startTime = "2014-12-06 00:00:00";
        String endTime = "2014-12-10 21:31:55";
        List<String> deviceList = new ArrayList<String>();
        deviceList.add("1011");
        deviceList.add("1001");
        deviceList.add("1007");
        Map<String, String> response = new HashMap<>();
        response = indexService.getTeleOperDistributeInfo(startTime, endTime, null);
        if (response != null) {
            // System.out.println("Search Results: "+response.size());
            int sum = 0;
            for (String key : response.keySet()) {
                System.out.println(key + " : " + response.get(key));
                sum += Integer.parseInt(response.get(key));
            }
            System.out.println("Sum: " + sum);
        }

        long e = System.currentTimeMillis();
        System.out.println("Time: " + (e - s));
        indexService.close();
    }

}
