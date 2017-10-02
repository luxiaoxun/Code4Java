package com.poi.test;

import java.io.IOException;

import com.poi.service.PoiIndexService;
import org.junit.Test;

public class PoiIndexServiceTest {

	@Test
	public void TestSearchPoiData() throws IOException{
		PoiIndexService service = new PoiIndexService();
		service.searchPoiData("南淮路100号");
		service.stop();
	}
	
	@Test
	public void TestSearchPoiDataById() throws IOException{
		PoiIndexService service = new PoiIndexService();
		service.searchPoiDataById(6011);
	}
	
	@Test
	public void TestSearchPoiDataByGeo() throws IOException{
		PoiIndexService service = new PoiIndexService();
		service.searchPoiInCircle(118.227985, 39.410722, 5);
	}
	
	@Test
	public void TestSearchPoiDataByGeoAndAddress() throws IOException{
		PoiIndexService service = new PoiIndexService();
		service.searchPoiByRangeAndAddress(118.227985, 39.410722, 5, "鼓楼");
	}
}

