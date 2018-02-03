<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<link rel="stylesheet" href="js/supermap/css/initMap.css">
<link rel="stylesheet" type="text/css" media="screen,projection" href="js/supermap/css/jquery-sticklr.css" />

<script type="text/javascript" src="js/supermap/js/jquery.js"></script>
<script type="text/javascript" src="js/supermap/js/bevInclude.js"></script>
<script type="text/javascript" src="js/supermap/libs/SuperMap.Include.js"></script>
<script type="text/javascript" src="js/supermap/iClientJavaScriptPlottingSymbols-min.js"></script>
<script type="text/javascript" src="js/supermap/js/initMap.js"></script>
<script type="text/javascript" src="js/supermap/js/jquery-sticklr.js"></script>

<style type="text/css">
#menuPlotting {
	left: 40px;
}
</style>

<script type="text/javascript" src="js/supermap/PlottingMenu.js"></script>

<script type="text/javascript">
var map, plottingLayer, plottingEdit;
var drawGeoPoint,drawGeoMultiPoint,
        drawGeoPolyline,drawGeoArc,drawGeoFreeline,drawGeoBizer2,drawGeoBizer3,drawGeoBizerN,drawGeoCardinalCurve,
        drawGeoCircle,drawGeoEllipse,drawGeoSector,drawGeoLune,drawGeoRectangle,drawGeoPolygon,drawGeoFreePolygon,drawGeoGatheringPlace,drawGeoRoundedRect,drawGeoCloseCurve,
        drawGeoCurveFlag,drawGeoRectFlag,drawGeoTriangleFlag,
        drawGeoStraightArrow,drawGeoDiagonalArrow,drawGeoDoubleArrow,drawGeoDoveTailStraightArrow,drawGeoDoveTailDiagonalArrow,
        drawGeoPolylineArrow,drawGeoParallelSearch,drawGeoSectorSearch,drawGeoBezierCurveArrow,drawGeoCardinalCurveArrow;

	function init() {
		Bev.Theme.set("bev-base");

		var mapControlList = getMapControlList();
		var option = {
			controls : mapControlList || [],
			units : 'degrees',
			projection : "EPSG:4326",
			displayProjection : "EPSG:4326",
			allOverlays : true
		};
		var layer = new SuperMap.Layer.CloudLayer({
			useCanvas : false
		});
		layer.url = mapUrl;
		map = new SuperMap.Map("mapContainer", option);
		map.addLayer(layer);
		map.setCenter(new SuperMap.LonLat(13221007.03437, 3769109.86499), 9);

		plottingLayer = new SuperMap.Layer.Vector("plottingLayer");
		map.addLayer(plottingLayer);
		addMenuPlotting();
		
		var markerLayer = new SuperMap.Layer.Markers("markers");
		map.addLayer(markerLayer);
		
		var point = new SuperMap.LonLat(118.7837,32.0588);
		var point1 = point.transform("EPSG:4326","EPSG:3857");
		//console.log(point1);
		
		//标记图层上添加标记
		var size = new SuperMap.Size(44,33);
		var offset = new SuperMap.Pixel(-(size.w/2), -size.h);
		var icon = new SuperMap.Icon('js/supermap/theme/images/marker.png',size,offset);
		var marker = new SuperMap.Marker(point1,icon);
		marker.sm_capital="A Marker";
		markerLayer.addMarker(marker);
		
		//点击marker弹出popup
		marker.events.on({
		   "click":openInfoWin,
		   "scope": marker
		});	
	}
	
	function openInfoWin(){
	    var marker = this;
	    var lonlat = marker.getLonLat();
	    var contentHTML = "<div style='font-size:.8em; opacity: 0.8; overflow-y:hidden;'>";
	    contentHTML += "<div>"+marker.sm_capital+"</div></div>";
	    var popup = new SuperMap.Popup.FramedCloud("popwin",new SuperMap.LonLat(lonlat.lon,lonlat.lat),null,contentHTML,null,true);
	    map.addPopup(popup);
	}

	function getMapControlList() {
		var mapControls = [];
		var mousePosition = new SuperMap.Control.MousePosition({emptyString:"0,0",displayProjection : "EPSG:4326"});
	    mousePosition.numDigits=4;
	    mapControls.push(mousePosition);
		mapControls.push(new SuperMap.Control.BZoom());
		mapControls.push(new SuperMap.Control.ScaleLine());
		mapControls.push(new SuperMap.Control.Navigation({
			dragPanOptions : {
				enableKinetic : true
			}
		}));
		//var param = {"position":"left","offsetY":100,"offsetX":6};
		//var param = {"position":"right"};
		//mapControls.push(new SuperMap.Control.BevLayerSwitcher(param));
		return mapControls;
	}
</script>

</head>

<body onload="init()" style="position: absolute;height: 100%;width: 100%;">

<div id="canvas" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;">
    <div id="plotting" style="z-index: 999">
        <ul id="menuPlotting" class="sticklr">
            <li>
                <a class="glyphicon plotting-lyphicon-draw-point" title="绘制点标"></a>
                <ul>
                    <li class="sticklr-title"><span>绘制点标</span></li>
                    <li><input type="button" value="点" onclick="plottingDrawGeoPoint()"
                               style="width:70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="多点" onclick="plottingDrawGeoMultiPoint()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                </ul>
            </li>
            <li>
                <a class="glyphicon plotting-glyphicon-draw-line" title="绘制线标"></a>
                <ul>
                    <li class="sticklr-title"><span>绘制线标</span></li>
                    <li><input type="button" value="折线" onclick="plottingdrawGeoPolyline()"
                               style="width:70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="圆弧" onclick="plottingDrawGeoArc()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="自由线" onclick="plottingDrawGeoFreeline()"
                               style="width: 70px;height: 25px ;margin:0 auto;"></li>
                    <li><input type="button" value="贝塞尔2次曲线" onclick="plottingDrawGeoBizer2()"
                               style="width: 100px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="贝塞尔3次曲线" onclick="plottingDrawGeoBizer3()"
                               style="width: 100px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="贝塞尔N次曲线" onclick="plottingDrawGeoBizerN()"
                               style="width: 100px;height: 25px ;margin:0 auto;">  </li>
                    <li><input type="button" value="Cardinal曲线" onclick="plottingDrawGeoCardinalCurve()"
                               style="width: 100px;height: 25px ;margin:0 auto;"> </li>
                </ul>
            </li>
            <li>
                <a class="glyphicon plotting-glyphicon-draw-polygon" title="绘制面标"></a>
                <ul>
                    <li class="sticklr-title"><span>绘制面标</span></li>
                    <li><input type="button" value="圆" onclick="plottingdrawGeoCircle()"
                               style="width:70px;height: 25px ;margin:0 auto;">  </li>
                    <li><input type="button" value="椭圆" onclick="plottingDrawGeoEllipse()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="扇形" onclick="plottingDrawGeoSector()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="弓形" onclick="plottingDrawGeoLune()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="矩形" onclick="plottingDrawGeoRectangle()"
                               style="width: 70px;height: 25px ;margin:0 auto;"></li>
                    <li><input type="button" value="多边形" onclick="plottingDrawGeoPolygon()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="手绘面" onclick="plottingDrawGeoFreePolygon()"
                               style="width: 70px;height: 25px ;margin:0 auto;"></li>
                    <li><input type="button" value="聚集区" onclick="plottingDrawGeoGatheringPlace()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="圆角矩形" onclick="plottingDrawGeoRoundedRect()"
                               style="width: 70px;height: 25px ;margin:0 auto;"></li>
                    <li><input type="button" value="闭合曲线" onclick="plottingDrawGeoCloseCurve()"
                               style="width: 70px;height: 25px ;margin:0 auto;"></li>
                </ul>
            </li>
            <li>
                <a class="glyphicon plotting-glyphicon-draw-flag" title="绘制旗标"></a>
                <ul>
                    <li class="sticklr-title"><span>绘制旗标</span></li>
                    <li><input type="button" value="曲线旗标" onclick="plottingdrawGeoCurveFlag()"
                               style="width:70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="直角旗标" onclick="plottingDrawGeoRectFlag()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="三角旗标" onclick="plottingDrawGeoTriangleFlag()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                </ul>
            </li>
            <li>
                <a class="glyphicon plotting-glyphicon-draw-arrow" title="绘制箭标"></a>
                <ul>
                    <li class="sticklr-title"><span>面状箭标</span></li>
                    <li><input type="button" value="直箭头" onclick="plottingdrawGeoStraightArrow()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="斜箭头" onclick="plottingDrawGeoDiagonalArrow()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="双箭头" onclick="plottingDrawGeoDoubleArrow()"
                               style="width: 70px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="燕尾直箭头" onclick="plottingDrawGeoDoveTailStraightArrow()"
                               style="width: 80px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="燕尾斜箭头" onclick="plottingDrawGeoDoveTailDiagonalArrow()"
                               style="width: 80px;height: 25px ;margin:0 auto;"> </li>
                </ul>
                <ul>
                    <li class="sticklr-title"><span>线状箭标</span></li>
                    <li><input type="button" value="折线箭头"
							onclick="plottingdrawGeoPolylineArrow()"
							style="width: 70px; height: 25px; margin: 0 auto;"> </li>
                    <li><input type="button" value="平行搜寻区" onclick="plottingDrawGeoParallelSearch()"
                               style="width: 80px;height: 25px ;margin:0 auto;"></li>
                    <li><input type="button" value="扇形搜寻区" onclick="plottingDrawGeoSectorSearch()"
                               style="width: 80px;height: 25px ;margin:0 auto;"> </li>
                    <li><input type="button" value="贝塞尔曲线箭头" onclick="plottingDrawGeoBezierCurveArrow()"
                               style="width: 105px;height: 25px ;margin:0 auto;"></li>
                    <li><input type="button" value="Cardinal曲线箭头" onclick="plottingDrawGeoCardinalCurveArrow()"
                               style="width: 110px;height: 25px ;margin:0 auto;"></li>
                </ul>
            </li>
            <li>
                <a  class="glyphicon plotting-glyphicon-draw-deactivate notArrow" title="取消绘制" onclick="PlottingDrawCancel()"></a>
            </li>
            <li>
                <a  class="glyphicon plotting-glyphicon-draw-removeAll notArrow" title="清除" onclick="PlottingClear()"></a>
            </li>
        </ul>
    </div>
    <div id="mapContainer"></div>
    <span id="toolbar"></span>
</div>
</body>

</html>

