package com.luxx.index.test;

import com.luxx.index.service.RedshiftIndexService;

import java.util.Map;

public class RedshiftIndexServiceTest {

    public static void test1(RedshiftIndexService indexService) {
        long s = System.currentTimeMillis();
        Map<String, String> resultsMap = indexService.getTopNEndpointUsage("2017-1-1 00:00:00", "2017-3-1 00:00:00",
                7564, 5);
        System.out.println("Endpoint id : sum of usage");
        for (String key : resultsMap.keySet()) {
            System.out.println(key + " : " + resultsMap.get(key));
        }
        long e = System.currentTimeMillis();
        System.out.println("Time: " + (e - s));
    }

    public static void test2(RedshiftIndexService indexService) {
        long s = System.currentTimeMillis();
        Map<String, String> resultsMap = indexService.getEndpointMaxbps("2017-1-1 00:00:00", "2017-3-1 00:00:00", 7564);
        System.out.println("Endpoint id : max bps");
        for (String key : resultsMap.keySet()) {
            System.out.println(key + " : " + resultsMap.get(key));
        }
        long e = System.currentTimeMillis();
        System.out.println("Time: " + (e - s));
    }

    public static void main(String[] args) {
        RedshiftIndexService indexService = new RedshiftIndexService();

        test1(indexService);
        test2(indexService);

    }

}
