var googleNormalUrl = 'http://127.0.0.1:8899/1818940751/{z}/{x}/{y}';
var googleNormalLayer = new L.TileLayer(googleNormalUrl, {
	minZoom : 1,
	maxZoom : 18,
	attribution : '谷歌普通地图'
});

var googleSateliteUrl = 'http://127.0.0.1:8899/47626774/{z}/{x}/{y}';
var googleSateliteLayer = new L.TileLayer(googleSateliteUrl, {
	minZoom : 1,
	maxZoom : 18,
	attribution : '谷歌卫星地图'
});

var baiduNormalUrl = 'http://127.0.0.1:8899/1082287436/{z}/{x}/{y}';
var baiduNormalLayer = new L.TileLayer(baiduNormalUrl, {
	minZoom : 1,
	maxZoom : 18,
	attribution : '百度普通地图'
});

var amapNormalUrl = 'http://127.0.0.1:8899/788865972/{z}/{x}/{y}';
var amapNormalLayer = new L.TileLayer(amapNormalUrl, {
	minZoom : 1,
	maxZoom : 18,
	attribution : '高德普通地图'
});

var amapSateliteUrl = 'http://127.0.0.1:8899/1542757547/{z}/{x}/{y}';
var amapSateliteLayer = new L.TileLayer(amapSateliteUrl, {
	minZoom : 1,
	maxZoom : 18,
	attribution : '高德卫星地图'
});

var amapSateliteAnnoUrl = 'http://127.0.0.1:8899/1447870524/{z}/{x}/{y}';
var amapSateliteAnnoLayer = new L.TileLayer(amapSateliteAnnoUrl, {
	minZoom : 1,
	maxZoom : 18,
	attribution : '高德卫星地图（标注）'
});




