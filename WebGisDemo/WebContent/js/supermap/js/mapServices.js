
function createMap(key, options) {

	getLayerFromOption(key, options, function(a) {
		var layers = a.layer;
		if (!layers.length) {
			layers = [ layers ];
		}

		getOverLay(function(overlays) {
			layers = layers.concat(overlays);
			var projection = a.projection;

			newMap(projection, layers);
		});
	});

	function newMap(projection, layers) {
		var option = {
			controls : window.mapControlList || [],
			units : 'm',
			allOverlays : true
		};
		if (projection) {
			option.projection = projection;
		}
		var map = new SuperMap.Map('mapContainer', option);
		map.addLayers(layers);
		var center = window.center;
		map.setCenter(center, window.zoom);

		var maxExtent = map.getMaxExtent();
		if (!maxExtent.contains(center.lon, center.lat)) {
			map.zoomToMaxExtent();
		}

		window.map = map;
	}

	function getOverLay(callback) {
		var option = window.overlayParams;

		if (option.length) {
			for ( var i = 0; i < option.length; i++) {
				var layerOption = option[i];

				var type = layerOption.type;
				var overlays = [];
				var count = 0;
				getLayerFromOption(type, layerOption, function(a) {
					var layers = a.layer;
					if (!layers.length) {
						layers = [ layers ];
					}
					overlays = overlays.concat(layers);
					count++;

					if (count == option.length) {
						callback && callback(overlays);
					}
				});
			}
		} else {
			callback && callback([]);
		}
	}

	function getLayerFromOption(key, options, callback) {
		//var layers;
		if (key == "supermapCloud") {//SuperMap云服务
			//createSuperMapCloud();
			var a = getSuperMapCloud();
			callback && callback(a);
		} else if (key == "iserver") {//SuperMap iServer Java服务
			getIserverLayer(function(callback) {
				return function(a) {
					callback && callback(a);
				};
			}(callback), options);
		} else if (key == "google") {//Google地图
			//createGoogleMap();
		} else if (key == "osm") {//OpenStreet Map
			getOSMLayer(function(callback) {
				return function(a) {
					callback && callback(a);
				};
			}(callback));
		} else if (key == "tianditu") {//天地图
			getTiandituLayer(function(callback) {
				return function(a) {
					callback && callback(a);
				};
			}(callback));
		} else if (key == "arcgis") {//ArcGis Online
			getArcGisLayer(function(callback) {
				return function(a) {
					callback && callback(a);
				};
			}(callback));
		} else if (key == "baidu") {//百度地图
			getBaiduLayer(function(callback) {
				return function(a) {
					callback && callback(a);
				};
			}(callback));
		} else if (key == "bing") {//Bing 地图
			getBingLayer(function(callback) {
				return function(a) {
					callback && callback(a);
				};
			}(callback));
		} else if (key == "wmts") {//WMTS图层
			var a = getWMTSLayer(options);
			callback && callback(a);
		}
	}

	function getSuperMapCloud() {
		var projection = "EPSG:3857";
		var layer = new SuperMap.Layer.CloudLayer({
			useCanvas : false
		});
		layer.url = 'http://192.1.114.11:8899/788865972/image?map=${mapName}&type=${type}&x=${x}&y=${y}&z=${z}';

		return {
			"projection" : projection,
			"layer" : layer
		};
	}

	function getIserverLayer(callback, option) {
		var url = option.url;
		var layerName = option.layerName;
		var layer = new SuperMap.Layer.TiledDynamicRESTLayer(layerName, url, {
			transparent : false,
			cacheEnabled : true,
			redirect : true
		}, {
			maxResolution : "auto",
			useCanvas : false
		});
		layer.events.on({
			"layerInitialized" : function(callback, layer) {
				return function() {
					callback && callback({
						"layer" : layer
					});
				};
			}(callback, layer)
		});
	}

	function getOSMLayer(callback) {
		LazyLoad.js([ "libs/layer/OSM.js" ], function(callback) {
			return function() {
				var layer0 = new SuperMap.Layer.OSM("osmLayer");
				callback && callback({
					"layer" : layer0,
					"projection" : "EPSG:3857"
				});
			};
		}(callback));
	}

	function getTiandituLayer(callback) {
		LazyLoad.js("libs/layer/Tianditu.js", function(callback) {
			return function() {
				var layer1 = new SuperMap.Layer.Tianditu({
					"layerType" : "vec"
				});//img,ter
				var layer2 = new SuperMap.Layer.Tianditu({
					"layerType" : "vec",
					"isLabel" : true
				});

				callback && callback({
					"layer" : [ layer1, layer2 ],
					"projection" : "EPSG:3857"
				});
			};
		}(callback));
	}

	function getArcGisLayer(callback) {
		LazyLoad
				.js(
						"libs/layer/ArcGIS93Rest.js",
						function(callback) {
							return function() {
								var projection = "EPSG:3857";
								var url = "http://www.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer/export";
								var layer0 = new SuperMap.Layer.ArcGIS93Rest(
										"ArcGIS93Rest", url, {
											layers : "show:0,1,2,4"
										}, {
											projection : projection
										});

								callback && callback({
									"layer" : layer0,
									"projection" : projection
								});
							};
						}(callback));
	}

	function getBaiduLayer(callback) {
		LazyLoad.js("libs/layer/Baidu.js", function(callback) {
			return function() {
				var layer0 = new SuperMap.Layer.Baidu();

				callback && callback({
					"layer" : layer0
				});
			};
		}(callback));
	}

	function getBingLayer(callback) {
		LazyLoad
				.js(
						"libs/layer/Bing.js",
						function(callback) {
							return function() {
								var apiKey = "AqTGBsziZHIJYYxgivLBf0hVdrAk9mWO5cQcb8Yux8sW5M8c8opEC2lZqKR1ZZXf";
								var projection = "EPSG:3857";
								var road = new SuperMap.Layer.Bing({
									name : "Road",
									key : apiKey,
									type : "Road"
								});
								var hybrid = new SuperMap.Layer.Bing({
									name : "Hybrid",
									key : apiKey,
									type : "AerialWithLabels"
								});
								var aerial = new SuperMap.Layer.Bing({
									name : "Aerial",
									key : apiKey,
									type : "Aerial"
								});

								callback && callback({
									"layer" : [ road, hybrid, aerial ],
									"projection" : projection
								});
							};
						}(callback));
	}

	function getWMTSLayer(option) {
		var url = "", wmtsLayerName = "", wmtsMatrixSet = "", wmtsFormat = "", resolutions = [], requestEncoding = "", layer;
		if (option) {
			url = option.url;
			layer = option.layer;
			wmtsLayerName = option.wmtsLayerName;
			wmtsMatrixSet = option.wmtsMatrixSet;
			wmtsFormat = option.wmtsFormat;
			var resolutionStr = option.resolution;
			var resolutionStrs = resolutionStr.split(",");
			resolutions = [];
			for ( var i = 0; i < resolutionStrs.length; i++) {
				resolutions.push(parseFloat(resolutionStrs[i]));
			}
			requestEncoding = option.requestEncoding;
		} else {
			return;
		}

		//新建图层
		var layer0 = new SuperMap.Layer.WMTS({
			name : wmtsLayerName,
			url : url,
			layer : layer,
			style : "default",
			matrixSet : wmtsMatrixSet,
			format : wmtsFormat,
			resolutions : resolutions,
			//matrixIds:matrixIds,
			opacity : 1,
			requestEncoding : requestEncoding
		});

		return {
			"layer" : layer0
		};
	}
}

function createGoogleMap() {
	LazyLoad.js([ "http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false",
			"libs/layer/SphericalMercator.js", "libs/layer/EventPane.js",
			"libs/layer/FixedZoomLevels.js", "libs/layer/Google.js",
			"libs/layer/Google.v3.js" ], function() {
		var map = new SuperMap.Map("map", {
			controls : window.mapControlList || []
		});

		//初始化google的四种图层
		var gphy = new SuperMap.Layer.Google("Google Physical", {
			type : google.maps.MapTypeId.TERRAIN
		});
		var gmap = new SuperMap.Layer.Google("Google Streets", // the default
		{
			numZoomLevels : 20
		});
		var ghyb = new SuperMap.Layer.Google("Google Hybrid", {
			type : google.maps.MapTypeId.HYBRID,
			numZoomLevels : 20
		});
		var gsat = new SuperMap.Layer.Google("Google Satellite", {
			type : google.maps.MapTypeId.SATELLITE,
			numZoomLevels : 22
		});

		map.addLayers([ gphy, gmap, ghyb, gsat ]);
		//设置地图中心点，显示地图
		map.setCenter(window.center, window.zoom);
		window.map = map;
	});
}