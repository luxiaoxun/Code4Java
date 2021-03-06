<!DOCTYPE html>
<html>
<head>
    <title>Map Demo</title>
    <!--<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"-->
    <!--charset="UTF-8">-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" initial-scale=1.0 maximum-scale=1.0>
    <link rel="stylesheet" href="${request.contextPath}/static/css/leaflet.css"/>

    <!-- [if lte IE 8] -->
    <link rel="stylesheet" href="${request.contextPath}/static/css/leaflet-ie.css"/>
    <link rel="stylesheet" href="${request.contextPath}/static/css/locate-ie.css"/>
    <!-- [endif] -->

    <link rel="stylesheet" href="${request.contextPath}/static/css/mobile.css"/>
    <link rel="stylesheet" href="${request.contextPath}/static/css/locate.css"/>
    <link rel="stylesheet" href="${request.contextPath}/static/css/leaflet.draw.css"/>
    <link rel="stylesheet" href="${request.contextPath}/static/css/leaflet.mouseposition.css"/>
    <link rel="stylesheet" href="${request.contextPath}/static/css/leaflet-search.css"/>

    <script src="${request.contextPath}/static/js/leafletjs/leaflet.js"></script>
    <script src="${request.contextPath}/static/js/leafletjs/leaflet.draw.js"></script>
    <script src="${request.contextPath}/static/js/leafletjs/proj4js-compressed.js"></script>
    <script src="${request.contextPath}/static/js/leafletjs/proj4leaflet.js"></script>
    <script src="${request.contextPath}/static/js/leafletjs/locate.js"></script>
    <script src="${request.contextPath}/static/js/leafletjs/leaflet.mouseposition.js"></script>
    <script src="${request.contextPath}/static/js/leafletjs/leaflet-search.js"></script>

    <script src="${request.contextPath}/static/js/socket.io/socket.io.js"></script>
    <script src="${request.contextPath}/static/js/jquery-1.11.3.min.js"></script>

    <script src="${request.contextPath}/static/js/leafletjs/leaflet.draw.cn.js"></script>
    <script src="${request.contextPath}/static/js/leafletjs/leaflet.map.js"></script>

</head>
<body>
<div id="map"
     style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;"
     class="leaflet-container leaflet-fade-anim" tabindex="0">
    <div class="leaflet-map-pane"
         style="-webkit-transform: translate3d(0px, 0px, 0px);">
        <div class="leaflet-tile-pane">
            <div class="leaflet-layer">
                <!--<div class="leaflet-tile-container"></div>-->
                <!--<div class="leaflet-tile-container leaflet-zoom-animated"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20102.png" style="height: 256px; width: 256px; left: 646px; top: 124px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20102(1).png" style="height: 256px; width: 256px; left: 390px; top: 124px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20101.png" style="height: 256px; width: 256px; left: 646px; top: -132px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20103.png" style="height: 256px; width: 256px; left: 646px; top: 380px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20101(1).png" style="height: 256px; width: 256px; left: 390px; top: -132px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20103(1).png" style="height: 256px; width: 256px; left: 390px; top: 380px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20102(2).png" style="height: 256px; width: 256px; left: 134px; top: 124px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20102(3).png" style="height: 256px; width: 256px; left: 902px; top: 124px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20103(2).png" style="height: 256px; width: 256px; left: 902px; top: 380px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20101(2).png" style="height: 256px; width: 256px; left: 902px; top: -132px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20103(3).png" style="height: 256px; width: 256px; left: 134px; top: 380px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20101(3).png" style="height: 256px; width: 256px; left: 134px; top: -132px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20102(4).png" style="height: 256px; width: 256px; left: 1158px; top: 124px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20102(5).png" style="height: 256px; width: 256px; left: -122px; top: 124px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20101(4).png" style="height: 256px; width: 256px; left: 1158px; top: -132px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20103(4).png" style="height: 256px; width: 256px; left: -122px; top: 380px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20101(5).png" style="height: 256px; width: 256px; left: -122px; top: -132px;"><img class="leaflet-tile leaflet-tile-loaded" src="./Leaflet Draw_files/20103(5).png" style="height: 256px; width: 256px; left: 1158px; top: 380px;"></div>-->
            </div>
        </div>
        <div class="leaflet-objects-pane">
            <div class="leaflet-shadow-pane"></div>
            <div class="leaflet-overlay-pane"></div>
            <div class="leaflet-marker-pane"></div>
            <div class="leaflet-popup-pane"></div>
        </div>
    </div>
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
        center: mapCenter,
        zoom: 9,
        minZoom: 1,
        maxZoom: 18,
        layers: [amapNormalLayer]
    });

    var baseLayers = {
        "高德普通地图": amapNormalLayer,
        "高德卫星地图": amapSateliteLayer,
        "谷歌普通地图": googleNormalLayer,
        "谷歌卫星地图": googleSateliteLayer
    };
    L.control.layers(baseLayers).addTo(map);

    L.control.mousePosition({
        separator: ','
    }).addTo(map);

    /* var wmsLayer = L.tileLayer.wms("http://localhost:8080/geowebcache/service/wms", {
    layers: 'ARCGIS-Demo',
    format: 'image/png'
    });
    wmsLayer.addTo(map); */

    // searchLayer is a L.LayerGroup contains searched markers
    var searchLayer = new L.LayerGroup();
    var searchControl = new L.Control.Search({layer: searchLayer, position: 'topright', initial: false});
    map.addControl(searchControl);

    var drawnItems = new L.FeatureGroup();
    map.addLayer(drawnItems);

    var CustomMarkerIcon = L.Icon.extend({
        options: {
            //shadowUrl : null,
            iconAnchor: new L.point(12.5, 40),
            iconSize: new L.point(25, 41),
            iconUrl: '${request.contextPath}/static/images/marker-icon.png',
            //shadowSize: [20,20],
            popupAnchor: [0, -45]
        }
    });

    var drawControl = new L.Control.Draw({
        draw: {
            position: 'topleft',
            rectangle: {},
            polygon: {
                allowIntersection: false,
                drawError: {
                    color: '#b00b00',
                    timeout: 1000
                },
                shapeOptions: {
                    color: '#0000ff'
                },
                showArea: true
            },
            polyline: {
                metric: true,
                shapeOptions: {
                    color: '#00ff00'
                }
            },
            circle: {
                shapeOptions: {
                    color: '#662d91'
                }
            },
            marker: {
                icon: new CustomMarkerIcon()
            }
        },
        edit: {
            featureGroup: drawnItems
        }
    });
    map.addControl(drawControl);

    var SearchMarkerIcon = L.Icon.extend({
        options: {
            shadowUrl: null,
            iconAnchor: new L.point(12.5, 40),
            iconSize: new L.point(25, 41),
            iconUrl: '${request.contextPath}/static/images/marker-icon.png'
        }
    });

    function createMarker(data) {
        var latlng = new L.LatLng(data.lat, data.lng);
        var marker = new L.Marker(
            latlng,
            {
                icon: new SearchMarkerIcon(),
                title: data.address
            });
        return marker;
    }

    function addDataToSearchlayer(dataList) {
        for (var i = 0; i < dataList.length; ++i) {
            var marker = createMarker(dataList[i]);
            searchLayer.addLayer(marker);
        }
        searchControl.setLayer(searchLayer);
    }

    map.on('draw:created', function (e) {
        var type = e.layerType;
        var layer = e.layer;
        if (type === 'marker') {
            layer.bindPopup(new L.popup().setContent("A new marker!"));
            //layer.bindPopup("A new marker");
            drawnItems.addLayer(layer);
        }
        if (type === "polygon") {
            searchLayer.clearLayers();
            drawnItems.addLayer(layer);
        }
        if (type === "rectangle") {
            searchLayer.clearLayers();
            drawnItems.addLayer(layer);
            var rect = layer;
            //var latlngs = [].slice.apply(rect._latlngs);
            var latlngs = rect._latlngs;
            $.ajax({
                url: "/station/dataInRectangle",
                data: JSON.stringify({
                    points: latlngs
                }),
                type: "POST",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (result) {
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
            drawnItems.addLayer(layer);
            var circle = layer;
            var radius = circle._mRadius;
            var latlng = circle._latlng;
            $.ajax({
                url: "/station/dataInCircle",
                data: JSON.stringify({
                    radius: radius,
                    lat: latlng.lat,
                    lng: latlng.lng
                }),
                type: "POST",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (result) {
                    if (result != null && result.msg == "ok") {
                        if (result.data != null) {
                            addDataToSearchlayer(result.data);
                        }
                    }
                }
            });
        }
    });

    map.on('draw:edited', function (e) {
        var layers = e.layers;
        layers.eachLayer(function (layer) {
            //do something with each edited layer
        });
    });

    map.on('draw:deleted', function (e) {
        var layers = e.layers;
        layers.eachLayer(function (layer) {
            //do something with each deleted layer
        });
    });
</script>

</body>

</html>
