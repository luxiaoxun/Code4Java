<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Map Demo</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" initial-scale=1.0 maximum-scale=1.0>
    <link rel="stylesheet" href="css/leaflet.css"/>

    <!-- [if lte IE 8] -->
    <link rel="stylesheet" href="css/leaflet-ie.css"/>
    <link rel="stylesheet" href="css/locate-ie.css"/>
    <!-- [endif] -->

    <link rel="stylesheet" href="css/mobile.css"/>
    <link rel="stylesheet" href="css/locate.css"/>
    <link rel="stylesheet" href="css/leaflet.draw.css"/>
    <link rel="stylesheet" href="css/leaflet.mouseposition.css"/>
    <link rel="stylesheet" href="css/leaflet-search.css"/>

    <script src="js/leafletjs/leaflet.js"></script>
    <script src="js/leafletjs/leaflet.draw.js"></script>
    <script src="js/leafletjs/proj4js-compressed.js"></script>
    <script src="js/leafletjs/proj4leaflet.js"></script>
    <script src="js/leafletjs/locate.js"></script>
    <script src="js/leafletjs/leaflet.mouseposition.js"></script>
    <script src="js/leafletjs/leaflet-search.js"></script>
    <script src="js/leafletjs/leaflet.animatedmarker.js"></script>
    
    <script src="js/socket.io/socket.io.js"></script>
    <script src="js/jquery-1.7.2.min.js"></script>

    <script src="js/leafletjs/leaflet.draw.cn.js" ></script>
	<script src="js/leafletjs/leaflet.map.js" ></script>

</head>
<body>
	<div id="map"
		style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;"
		class="leaflet-container leaflet-fade-anim" tabindex="0">
		<div class="leaflet-control-container">
			<div class="leaflet-top leaflet-left">
				<div class="leaflet-control-zoom leaflet-bar leaflet-control">
					<a class="leaflet-control-zoom-in"
						href="#" title="Zoom in">+</a>
					<a class="leaflet-control-zoom-out"
						href="#" title="Zoom out">-</a>
				</div>
				<div class="leaflet-draw leaflet-control">
					<div class="leaflet-draw-section">
						<div class="leaflet-draw-toolbar leaflet-bar leaflet-draw-toolbar-top">
							<a class="leaflet-draw-draw-polyline"
								href="#" title="线条"></a> 
							<a class="leaflet-draw-draw-polygon"
								href="#" title="多边形"></a>
							<a class="leaflet-draw-draw-rectangle"
								href="#" title="矩形"></a>
								<a class="leaflet-draw-draw-circle"
								href="#" title="圆形"></a>
								<a class="leaflet-draw-draw-marker"
								href="#" title="图标"></a>
						</div>
						<ul class="leaflet-draw-actions"></ul>
					</div>
					<div class="leaflet-draw-section">
						<div class="leaflet-draw-toolbar leaflet-bar">
							<a class="leaflet-draw-edit-edit leaflet-disabled"
								href="#" title="没有图形可以编辑"></a>
								<a class="leaflet-draw-edit-remove leaflet-disabled"
								href="#" title="没有图形可以删除"></a>
						</div>
						<ul class="leaflet-draw-actions"></ul>
					</div>
				</div>
			</div>
			<div class="leaflet-top leaflet-right"></div>
			<div class="leaflet-bottom leaflet-left"></div>
		</div>
	</div>

	<script type="text/javascript">
		var mapCenter = new L.LatLng(32.1280, 118.7742); //南京
		var map = new L.Map('map', {
			center : mapCenter,
			zoom : 9,
			minZoom : 1,
			maxZoom : 18,
			layers : [ amapNormalLayer ]
		});

		var baseLayers = {
			"高德普通地图" : amapNormalLayer,
			"高德卫星地图" : amapSateliteLayer,
			"谷歌普通地图" : googleNormalLayer,
			"谷歌卫星地图" : googleSateliteLayer
		};
		L.control.layers(baseLayers).addTo(map);

		L.control.mousePosition({
			separator : ','
		}).addTo(map);

		/* var wmsLayer = L.tileLayer.wms("http://localhost:8080/geowebcache/service/wms", {
		layers: 'ARCGIS-Demo',
		format: 'image/png'
		});
		wmsLayer.addTo(map); */
		
		var testMarker = new L.Marker(mapCenter,{
			title:"aaa"
		});
		map.addLayer(testMarker);
		

		// searchLayer is a L.LayerGroup contains searched markers
		var searchLayer = new L.LayerGroup();
		// this searchLayer will be added to map
		var searchControl = new L.Control.Search({layer: searchLayer,position:'topright',initial:false}); 
		map.addControl(searchControl);
		
		var drawnItems = new L.FeatureGroup();
		map.addLayer(drawnItems);

		var CustomMarkerIcon = L.Icon.extend({
			options : {
				//shadowUrl : null,
				iconAnchor : new L.point(12.5, 40),
				iconSize : new L.point(25, 41),
				iconUrl : 'images/marker-icon.png',
				//shadowSize: [20,20],
				popupAnchor: [0,-45]
			}
		});

		var drawControl = new L.Control.Draw({
			draw : {
				position : 'topleft',
				rectangle : {},
				polygon : {
					allowIntersection : false,
					drawError : {
						color : '#b00b00',
						timeout : 1000
					},
					shapeOptions : {
						color : '#0000ff'
					},
					showArea : true
				},
				polyline : {
					metric : true,
					shapeOptions : {
						color : '#ff0000'
					}
				},
				circle : {
					shapeOptions : {
						color : '#662d91'
					}
				},
				marker : {
					icon : new CustomMarkerIcon()
				}
			},
			edit : {
				featureGroup : drawnItems
			}
		});
		map.addControl(drawControl);

		var SearchMarkerIcon = L.Icon.extend({
			options : {
				shadowUrl : null,
				iconAnchor : new L.point(12.5, 40),
				iconSize : new L.point(25, 41),
				iconUrl : 'images/marker-icon.png'
			}
		});
		
		function createMarker(data){
			var latlng = new L.LatLng(data.lat,data.lng);
			var marker = new L.Marker(
					latlng, 
					{
						icon : new SearchMarkerIcon(),
						title : data.address
					});
			return marker;
		}
		
		function addDataToSearchlayer(dataList){
			for ( var i = 0; i < dataList.length; ++i) {
				var marker = createMarker(dataList[i]);
				searchLayer.addLayer(marker);
			}
			searchControl.setLayer(searchLayer);
		}
		
		map.on('draw:created', function(e) {
			var type = e.layerType;
			var layer = e.layer;
			if (type === 'marker') {
				//layer.title = "A new marker!";
				
				layer.bindPopup(new L.popup().setContent("A new marker!"));
				//layer.bindPopup("A new marker");
				drawnItems.addLayer(layer);
			}
			if(type === "polyline"){
				drawnItems.addLayer(layer);
				var line = layer;
				var animatedMarker = L.animatedMarker(line.getLatLngs(), {
					  distance: 300,  // meters
					  interval: 2000, // milliseconds
					  onEnd: function() {
						    // blow up this marker
						    map.removeLayer(this);
						  }
					});
				map.addLayer(animatedMarker);
			}
			if (type === "rectangle") {
				searchLayer.clearLayers();
				var rect = layer;
				//var latlngs = [].slice.apply(rect._latlngs);
				var latlngs = rect._latlngs;
				$.ajax({
					url : "getDataInRectangle",
					data : {
						latlngs : JSON.stringify(latlngs)
					},
					type : "get",
					dataType : "json",
					success : function(result) {
						if (result != null && result.msg == "ok") {
							if (result.data != null) {
								addDataToSearchlayer(result.data);
							}
						}
					}
				});
			}
			if (type === 'circle') {
				searchLayer.clearLayers();
				var circle = layer;
				var radius = circle._mRadius;
				var latlng = circle._latlng;
				$.ajax({
					url : "getDataInCircle",
					data : {
						radius : radius,
						lat : latlng.lat,
						lng : latlng.lng
					},
					type : "get",
					dataType : "json",
					success : function(result) {
						if (result != null && result.msg == "ok") {
							if (result.data != null) {
								addDataToSearchlayer(result.data);
							}
						}
					}
				});
			}
		});

		map.on('draw:edited', function(e) {
			var layers = e.layers;
			layers.eachLayer(function(layer) {
				//do something with each edited layer
			});
		});

		map.on('draw:deleted', function(e) {
			var layers = e.layers;
			layers.eachLayer(function(layer) {
				//do something with each deleted layer
			});
		});
	</script>

</body>

</html>
