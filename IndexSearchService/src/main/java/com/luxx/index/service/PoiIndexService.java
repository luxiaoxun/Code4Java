package com.luxx.index.service;

import com.luxx.index.model.PoiData;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class PoiIndexService {
    private static Logger log = LogManager.getLogger(PoiIndexService.class);

    private String indexPath = "D:/IndexPoiData";
    private IndexWriter indexWriter = null;

    //    private IKAnalyzer ikAnalyzer = new IKAnalyzer(true);
    private SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(true);

    private DirectoryReader directoryReader = null;
    private Directory directory = null;

    // Spatial index and search
    private SpatialContext spatialContext;
    private ShapeFactory shapeFactory;
    private SpatialStrategy spatialStrategy;

    // Field Name
    private static final String IDFieldName = "id";
    private static final String AddressFieldName = "address";
    private static final String LatFieldName = "lat";
    private static final String LngFieldName = "lng";
    private static final String GeoFieldName = "geoField";

    private final int maxResultCount = 100;

    @PostConstruct
    public void init() {
        try {
            directory = new SimpleFSDirectory(Paths.get(indexPath));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(directory, config);

            // Typical geo spatial context
            // These can also be constructed from SpatialContextFactory
            spatialContext = SpatialContext.GEO;
            shapeFactory = spatialContext.getShapeFactory();
            int maxLevels = 11; // results in sub-meter precision for geohash
            // This can also be constructed from SpatialPrefixTreeFactory
            SpatialPrefixTree grid = new GeohashPrefixTree(spatialContext, maxLevels);
            spatialStrategy = new RecursivePrefixTreeStrategy(grid, GeoFieldName);
        } catch (Exception ex) {
            log.error("PoiIndexService init exception: " + ex.toString());
        }
    }

    private IndexSearcher getIndexSearcher() {
        try {
            if (directoryReader == null) {
                directoryReader = DirectoryReader.open(directory);
            } else {
                DirectoryReader directoryReader = DirectoryReader.openIfChanged(this.directoryReader);
                if (directoryReader != null) {
                    this.directoryReader.close(); // 关闭原reader
                    this.directoryReader = directoryReader; // 赋予新reader
                }
            }
            return new IndexSearcher(directoryReader);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    public boolean indexPoiDataList(List<PoiData> dataList) {
        try {
            if (dataList != null && dataList.size() > 0) {
                List<Document> docs = new ArrayList<>();
                for (PoiData data : dataList) {
                    Document doc = new Document();
                    doc.add(new StoredField(IDFieldName, data.getId()));
                    doc.add(new StoredField(LatFieldName, data.getLat()));
                    doc.add(new StoredField(LngFieldName, data.getLng()));
                    doc.add(new TextField(AddressFieldName, data.getAddress(), Field.Store.YES));
                    Point point = shapeFactory.pointXY(data.getLng(), data.getLat());
                    for (Field f : spatialStrategy.createIndexableFields(point)) {
                        doc.add(f);
                    }
                    docs.add(doc);
                }
                indexWriter.addDocuments(docs);
                indexWriter.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    private void printDocs(ScoreDoc[] scoreDocs, IndexSearcher indexSearcher) {
        if (scoreDocs != null) {
            log.info("Total count：" + scoreDocs.length);
            for (ScoreDoc scoreDoc : scoreDocs) {
                try {
                    Document hitDoc = indexSearcher.doc(scoreDoc.doc);
                    log.info(hitDoc.get(IDFieldName));
                    log.info(hitDoc.get(LngFieldName));
                    log.info(hitDoc.get(LatFieldName));
                    log.info(hitDoc.get(AddressFieldName));
                } catch (IOException e) {
                    log.error(e.toString());
                }
            }
        }
    }

    private List<PoiData> getDataFromSearchResult(ScoreDoc[] scoreDocs, IndexSearcher indexSearcher) {
        List<PoiData> datas = new ArrayList<>();
        if (scoreDocs != null) {
            log.info("Total count：" + scoreDocs.length);
            for (ScoreDoc scoreDoc : scoreDocs) {
                try {
                    Document hitDoc = indexSearcher.doc(scoreDoc.doc);
                    PoiData data = new PoiData();
                    data.setId(Long.parseLong((hitDoc.get(IDFieldName))));
                    data.setLng(Double.parseDouble(hitDoc.get(LngFieldName)));
                    data.setLat(Double.parseDouble(hitDoc.get(LatFieldName)));
                    data.setAddress(hitDoc.get(AddressFieldName));
                    datas.add(data);
                } catch (IOException e) {
                    log.error(e.toString());
                }
            }
        }

        return datas;
    }

    private int doQuery(Query query, IndexSearcher indexSearcher) {
        TopDocs hits = null;
        try {
            hits = indexSearcher.search(query, maxResultCount);
        } catch (IOException e) {
            log.error(e.toString());
        }
        if (hits != null) {
            ScoreDoc[] scoreDocs = hits.scoreDocs;
            printDocs(scoreDocs, indexSearcher);
            return scoreDocs.length;
        }
        return 0;
    }

    public int searchPoiDataByAddress(String address) {
        IndexSearcher indexSearcher = getIndexSearcher();
        if (indexSearcher != null) {
            QueryBuilder builder = new QueryBuilder(analyzer);
            Query query = builder.createPhraseQuery(AddressFieldName, address);
            return doQuery(query, indexSearcher);
        }
        return 0;
    }

    public List<PoiData> searchPoiInRectangle(double minLng, double minLat, double maxLng, double maxLat) {
        List<PoiData> results = new ArrayList<>();
        IndexSearcher indexSearcher = getIndexSearcher();
        if (indexSearcher != null) {
            Point lowerLeftPoint = shapeFactory.pointXY(minLng, minLat);
            Point upperRightPoint = shapeFactory.pointXY(maxLng, maxLat);
            Shape rect = shapeFactory.rect(lowerLeftPoint, upperRightPoint);

            SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, rect);
            Query query = spatialStrategy.makeQuery(args);
            TopDocs docs = null;
            try {
                docs = indexSearcher.search(query, maxResultCount);
            } catch (IOException e) {
                log.error(e.toString());
            }

            if (docs != null) {
                ScoreDoc[] scoreDocs = docs.scoreDocs;
                //printDocs(scoreDocs, indexSearcher);
                results = getDataFromSearchResult(scoreDocs, indexSearcher);
            }
        }

        return results;
    }

    public List<PoiData> searchPoiInCircle(double lng, double lat, double radius) {
        List<PoiData> results = new ArrayList<>();
        IndexSearcher indexSearcher = getIndexSearcher();
        if (indexSearcher != null) {
            Point pt = shapeFactory.pointXY(lng, lat);
            Shape circle = shapeFactory.circle(pt, DistanceUtils.dist2Degrees(
                    radius, DistanceUtils.EARTH_MEAN_RADIUS_KM));
            SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, circle);
            Query query = spatialStrategy.makeQuery(args);
            // the distance (in km)
            DoubleValuesSource valueSource = spatialStrategy.makeDistanceValueSource(pt, DistanceUtils.DEG_TO_KM);
            Sort distSort = null;
            TopDocs docs = null;
            try {
                // false = asc dist
                distSort = new Sort(valueSource.getSortField(false)).rewrite(indexSearcher);
                docs = indexSearcher.search(query, maxResultCount, distSort);
            } catch (IOException e) {
                log.error(e.toString());
            }

            if (docs != null) {
                ScoreDoc[] scoreDocs = docs.scoreDocs;
                //printDocs(scoreDocs, indexSearcher);
                results = getDataFromSearchResult(scoreDocs, indexSearcher);
            }
        }

        return results;
    }

    public List<PoiData> searchPoiInCircleAndAddress(double lng, double lat, double radius, String address) {
        List<PoiData> results = new ArrayList<>();
        IndexSearcher indexSearcher = getIndexSearcher();
        if (indexSearcher != null) {
            Point pt = shapeFactory.pointXY(lng, lat);
            Shape circle = shapeFactory.circle(pt, DistanceUtils.dist2Degrees(
                    radius, DistanceUtils.EARTH_MEAN_RADIUS_KM));
            SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, circle);
            Query geoQuery = spatialStrategy.makeQuery(args);

            QueryBuilder builder = new QueryBuilder(analyzer);
            Query phraseQuery = builder.createPhraseQuery(AddressFieldName, address);
            BooleanQuery.Builder boolBuilder = new BooleanQuery.Builder();
            boolBuilder.add(phraseQuery, Occur.SHOULD);
            boolBuilder.add(geoQuery, Occur.MUST);

            Query query = boolBuilder.build();
            TopDocs docs = null;
            try {
                docs = indexSearcher.search(query, maxResultCount);
            } catch (IOException e) {
                log.error(e.toString());
            }

            if (docs != null) {
                ScoreDoc[] scoreDocs = docs.scoreDocs;
                //printDocs(scoreDocs, indexSearcher);
                results = getDataFromSearchResult(scoreDocs, indexSearcher);
            }
        }
        return results;
    }

    @PreDestroy
    public void close() {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }

        if (directoryReader != null) {
            try {
                directoryReader.close();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
    }

    public void clear() {
        if (indexWriter != null) {
            try {
                log.info("Delete all exist data");
                indexWriter.deleteAll();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
    }

}
