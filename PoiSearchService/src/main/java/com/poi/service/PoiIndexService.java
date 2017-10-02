package com.poi.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
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
import org.slf4j.Logger;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Shape;

public class PoiIndexService {

	private static Logger log = org.slf4j.LoggerFactory
			.getLogger(PoiIndexService.class);

	private String indexPath = "D:/IndexPoiData";
	private IndexWriter indexWriter = null;
	
	private IKAnalyzer analyzer = new IKAnalyzer(true);
	//private SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(true);

	private DirectoryReader ireader = null;
	private Directory directory = null;
	
	// Spatial index and search
	private SpatialContext ctx;
	private SpatialStrategy strategy;

	// Field Name
	private static final String IDFieldName = "id";
	private static final String AddressFieldName = "address";
	private static final String LatFieldName = "lat";
	private static final String LngFieldName = "lng";
	private static final String GeoFieldName = "geoField";
	
	private final int maxResultCount = 100;

	public PoiIndexService() throws IOException {
		init();
	}

	public PoiIndexService(String indexPath) throws IOException {
		this.indexPath = indexPath;
		init();
	}
	
	protected void init() throws IOException {
		directory = new SimpleFSDirectory(Paths.get(indexPath));
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		indexWriter = new IndexWriter(directory, config);

		// Typical geospatial context
		// These can also be constructed from SpatialContextFactory
		ctx = SpatialContext.GEO;
		int maxLevels = 11; // results in sub-meter precision for geohash
		// This can also be constructed from SpatialPrefixTreeFactory
		SpatialPrefixTree grid = new GeohashPrefixTree(ctx, maxLevels);
		strategy = new RecursivePrefixTreeStrategy(grid, GeoFieldName);
	}
	
	private IndexSearcher getIndexSearcher() {
		try {
			if (ireader == null) {
				ireader = DirectoryReader.open(directory);
			} else {
				// if the index was changed since the provided reader was
				// opened, open and return a new reader;
				// else return null
				DirectoryReader directoryReader = DirectoryReader
						.openIfChanged(ireader);
				if (directoryReader != null) {
					ireader.close(); // 关闭原reader
					ireader = directoryReader; // 赋予新reader
				}
			}
			return new IndexSearcher(ireader);
		}
		catch (Exception e) {
			log.error(e.toString());
		}
		return null; // 发生异常则返回null
	}

	public boolean indexPoiData(PoiData data) {
		try {
			if(data!=null){
				Document doc = new Document();
				doc.add(new LongField(IDFieldName, data.getId(), Field.Store.YES));
				doc.add(new DoubleField(LatFieldName, data.getLat(), Field.Store.YES));
				doc.add(new DoubleField(LngFieldName, data.getLng(), Field.Store.YES));
				doc.add(new TextField(AddressFieldName, data.getAddress(), Field.Store.YES));
				Point point = ctx.makePoint(data.getLng(),data.getLat());
				for (Field f : strategy.createIndexableFields(point)) {
			        doc.add(f);
			    }
				//doc.add(new StoredField(strategy.getFieldName(), point.getX()+" "+point.getY()));
				indexWriter.addDocument(doc);
			}
			return false;
		} catch (Exception e) {
			log.error(e.toString());
			return false;
		}
	}
	
	public boolean indexPoiDataList(List<PoiData> dataList) {
		try {
			if (dataList != null && dataList.size() > 0) {
				List<Document> docs = new ArrayList<>();
				for (PoiData data : dataList) {
					Document doc = new Document();
					doc.add(new LongField(IDFieldName, data.getId(), Field.Store.YES));
					doc.add(new DoubleField(LatFieldName, data.getLat(), Field.Store.YES));
					doc.add(new DoubleField(LngFieldName, data.getLng(), Field.Store.YES));
					doc.add(new TextField(AddressFieldName, data.getAddress(), Field.Store.YES));
					Point point = ctx.makePoint(data.getLng(),data.getLat());
					for (Field f : strategy.createIndexableFields(point)) {
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
	
	private void printDocs(ScoreDoc[] scoreDocs,IndexSearcher indexSearcher) {
		if (scoreDocs != null) {
			System.out.println("总数：" + scoreDocs.length);
			for (int i = 0; i < scoreDocs.length; i++) {
				try {
					Document hitDoc = indexSearcher.doc(scoreDocs[i].doc);
					System.out.print(hitDoc.get(IDFieldName));
					System.out.print(" " + hitDoc.get(LngFieldName));
					System.out.print(" " + hitDoc.get(LatFieldName));
					System.out.println(" " + hitDoc.get(AddressFieldName));
				} catch (IOException e) {
					log.error(e.toString());
				}
			}
		}
	}
	
	private List<PoiData> getPoiDatasFromDoc(ScoreDoc[] scoreDocs,IndexSearcher indexSearcher){
		List<PoiData> datas = new ArrayList<>();
		if (scoreDocs != null) {
			//System.out.println("总数：" + scoreDocs.length);
			for (int i = 0; i < scoreDocs.length; i++) {
				try {
					Document hitDoc = indexSearcher.doc(scoreDocs[i].doc);
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
	
	private int doQuery(Query query,IndexSearcher indexSearcher){
		TopDocs hits = null;
		try {
			hits = indexSearcher.search(query, maxResultCount);
		} catch (IOException e) {
			log.error(e.toString());
		}
		if (hits != null) {
			ScoreDoc[] scoreDocs = hits.scoreDocs;
			printDocs(scoreDocs,indexSearcher);
			return scoreDocs.length;
		}
		return 0;
	}

	public int searchPoiData(String queryText) {
		IndexSearcher indexSearcher = getIndexSearcher();
		if(indexSearcher != null){
			QueryBuilder builder = new QueryBuilder(analyzer);
			Query query = builder.createPhraseQuery(AddressFieldName, queryText);
			return doQuery(query,indexSearcher);
		}
		
		return -1;
	}
	
	public int searchPoiDataById(long id) {
		IndexSearcher indexSearcher = getIndexSearcher();
		if(indexSearcher != null){
			Query query = NumericRangeQuery.newLongRange(IDFieldName, id, id, true, true);
			return doQuery(query,indexSearcher);
		}
		
		return -1;
	}
	
	public List<PoiData> searchPoiInRectangle(double minLng, double minLat,
			double maxLng, double maxLat) {
		List<PoiData> results = new ArrayList<>();
		IndexSearcher indexSearcher = getIndexSearcher();
		if (indexSearcher != null) {
			Point lowerLeftPoint = ctx.makePoint(minLng, minLat);
			Point upperRightPoint = ctx.makePoint(maxLng, maxLat);
			Shape rect = ctx.makeRectangle(lowerLeftPoint, upperRightPoint);
			SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, rect);
			Query query = strategy.makeQuery(args);
			TopDocs docs = null;
			try {
				docs = indexSearcher.search(query, maxResultCount);
			} catch (IOException e) {
				log.error(e.toString());
			}

			if (docs != null) {
				ScoreDoc[] scoreDocs = docs.scoreDocs;
				//printDocs(scoreDocs, indexSearcher);
				results = getPoiDatasFromDoc(scoreDocs, indexSearcher);
			}
		}

		return results;
	}

	public List<PoiData> searchPoiInCircle(double lng, double lat, double radius) {
		List<PoiData> results = new ArrayList<>();
		IndexSearcher indexSearcher = getIndexSearcher();
		if (indexSearcher != null) {
			Shape circle = ctx.makeCircle(lng, lat, DistanceUtils.dist2Degrees(
					radius, DistanceUtils.EARTH_MEAN_RADIUS_KM));
			SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
					circle);
			Query query = strategy.makeQuery(args);
			Point pt = ctx.makePoint(lng, lat);
			ValueSource valueSource = strategy.makeDistanceValueSource(pt,
					DistanceUtils.DEG_TO_KM);// the distance (in km)
			Sort distSort = null;
			TopDocs docs = null;
			try {
				// false = asc dist
				distSort = new Sort(valueSource.getSortField(false))
						.rewrite(indexSearcher);
				docs = indexSearcher.search(query, maxResultCount, distSort);
			} catch (IOException e) {
				log.error(e.toString());
			}

			if (docs != null) {
				ScoreDoc[] scoreDocs = docs.scoreDocs;
				//printDocs(scoreDocs, indexSearcher);
				results = getPoiDatasFromDoc(scoreDocs, indexSearcher);
			}
		}

		return results;
	}
	
	public List<PoiData> searchPoiByRangeAndAddress(double lng, double lat,
			double range, String address) {
		List<PoiData> results = new ArrayList<>();
		IndexSearcher indexSearcher = getIndexSearcher();
		if (indexSearcher != null) {
			SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
					ctx.makeCircle(lng, lat, DistanceUtils.dist2Degrees(range,
							DistanceUtils.EARTH_MEAN_RADIUS_KM)));
			Query geoQuery = strategy.makeQuery(args);

			QueryBuilder builder = new QueryBuilder(analyzer);
			Query addQuery = builder.createPhraseQuery(AddressFieldName,
					address);

			BooleanQuery.Builder boolBuilder = new BooleanQuery.Builder();
			boolBuilder.add(addQuery, Occur.SHOULD);
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
				results = getPoiDatasFromDoc(scoreDocs, indexSearcher);
			}
		}
		return results;
	}

	public void stop() {
		if (indexWriter != null) {
			try {
				indexWriter.close();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
		
		if(ireader!=null){
			try {
				ireader.close();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
	}
	
	public void clear(){
		if (indexWriter != null) {
			try {
				indexWriter.deleteAll();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
	}

}
