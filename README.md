# Code4Java
Repository for my java projects 

## 1. NettyMqService
How to implement a message queue service with Netty and RabbitMQ.
##### 中文详情(Chinese Details):
* [Message Queue Service based on Netty and RabbitMQ](http://www.cnblogs.com/luxiaoxun/p/4257105.html)

## 2. IndexSearchService
1. How to use Elasticsearch.
2. How to use Solr or Lucene to index and query data.
3. How to use Lucene to index and query POI(GEO data). 
##### 中文详情(Chinese Details):
* [Lucene index and query POI data](http://www.cnblogs.com/luxiaoxun/p/5020247.html)
* [Solr index and query MYSQL data](http://www.cnblogs.com/luxiaoxun/p/4442770.html)
* [Solr index and query GEO data](http://www.cnblogs.com/luxiaoxun/p/4477591.html)
* [Elasticsearch index and query data](http://www.cnblogs.com/luxiaoxun/p/4869509.html)
* [SQL to Elasticsearch Java Code](http://www.cnblogs.com/luxiaoxun/p/6826211.html)

## 3. MapHttpService
A simple Http Map Service providing tile image for Map. 

## 4. WebGisDemo
A simple web GIS page based on [leaflet](https://github.com/Leaflet/Leaflet).
##### 中文详情([Chinese Details](http://www.cnblogs.com/luxiaoxun/p/5022333.html))
How to use WebGisDemo with MapHttpService:
1. Download map tile image with [MapDownloader](https://github.com/luxiaoxun/MapDownloader)
2. Start MapHttpService with right data source, an example:
   >port=8899  
   >database.type=0 # Use SQLite  
   >database.sqlite.path=E:\\GIS\\MapDownloader\\MapCache\\TileDBv5\\en\\Data.gmdb
3. Start WebGISDemo: http://localhost:9090/map/map

![map](https://github.com/luxiaoxun/Code4Java/blob/master/WebGisDemo/picture/map-demo.png)

