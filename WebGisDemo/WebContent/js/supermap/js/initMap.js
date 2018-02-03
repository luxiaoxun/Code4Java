Bev.inputScript("js/supermap/js/controls/Navbar.js");
Bev.inputScript("js/supermap/js/controls/Menu.js");
Bev.inputScript("js/supermap/js/mapServices.js");
Bev.inputScript("js/supermap/libs/control/BZoom.js");
Bev.inputScript("js/supermap/libs/control/BevLayerSwitcher.js");
Bev.inputScript("js/supermap/js/controls/loadFile/LoadFile.js");
Bev.inputScript("js/supermap/js/controls/loadFile/LoadJSON.js");

var measure, nav, baseToolsMenu, restMenu, geolocate, drawFeature;

var mapUrl = 'http://192.1.114.11:8899/788865972/image?map=${mapName}&type=${type}&x=${x}&y=${y}&z=${z}';

function initDemo() {
	nav = new Bev.Navbar({
		"body" : $("#toolbar")
	});

	baseToolsMenu = new Bev.Menu({
		"body" : $("#toolbar")
	});
	restMenu = new Bev.Menu({
		"body" : $("#toolbar")
	});

	//        nav.addNavItem({
	//            "icon_class":"tool-icon-20",
	//            "title":"基本操作"
	//        });

	//        nav.bindEvent(0,{
	//            "mouseover":function(){
	//                var position=$(this).position();
	//                menu.menuBody.css({"display":"block","position":"absolute","top":position.top+30,"left":position.left}) ;
	//                menu.menuBody.mouseover(function(){
	//                    menu.menuBody.css({"display":"block","position":"absolute","top":position.top+30,"left":position.left}) ;
	//                });
	//            },
	//            "mouseout":function(){
	//                menu.menuBody.css({"display":"none"});
	//                menu.menuBody.mouseout(function(){
	//                    menu.menuBody.css({"display":"none"});
	//                });
	//            }
	//        });

	$("body").addClass("ui-widget-content");
}

function bindMenu2Nav(myNav, myMenu, icon, title) {
	var leftArray = [ 45, 115 ];
	if (myMenu.tree.length > 0) {
		myNav.addNavItem({
			"icon_class" : icon,
			"title" : title
		});
		var index = myNav.navItems.length - 1;
		var menuLeft = leftArray[index];
		myNav.bindEvent(index, {
			"mouseover" : function(menuLeft) {
				return function() {
					var position = $(this).position();
					myMenu.menuBody.css({
						"display" : "block",
						"position" : "absolute",
						"top" : position.top + 30 + "px",
						"left" : menuLeft + "px"
					});
					myMenu.menuBody.mouseover(function() {
						myMenu.menuBody.css({
							"display" : "block"
						});
					});
				}
			}(menuLeft),
			"mouseout" : function() {
				myMenu.menuBody.css({
					"display" : "none"
				});
				myMenu.menuBody.mouseout(function() {
					myMenu.menuBody.css({
						"display" : "none"
					});
				});
			}
		});
	}
}

function addMeasure(isSelect) {
	if (isSelect == "0")
		return;
	var param = {
		"icon_class" : "glyphicon-ruler-both",
		"text" : "量&nbsp;&nbsp;&nbsp;&nbsp;算",
		"events" : {
			"click" : function() {
				Bev.loader.js([ "js/controls/Dialog.js",
						"js/controls/Measure.js" ], function() {
					if (!measure) {//!myMeasure
						var dialog = new Bev.Dialog(null, {
							"text" : "量&nbsp;&nbsp;&nbsp;&nbsp;算"
						});

						var contentBody = dialog.getContentBody();
						measure = new Bev.Measure({
							"body" : contentBody,
							"map" : map
						});
						dialog.on("dialogclose", function() {
							if (measure) {
								measure.destroy();
								measure = null;
							}
						});
					}
				});
			}
		}
	};
	baseToolsMenu.addItem(param);
};

function addGeolocate(isSelect) {
	if (isSelect == "0")
		return;
	var param = {
		"icon_class" : "glyphicon-screenshot",
		"text" : "定&nbsp;&nbsp;&nbsp;&nbsp;位",
		"events" : {
			"click" : function() {
				Bev.loader.js([ "js/controls/Dialog.js",
						"js/controls/Geolocate.js" ], function() {
					if (!geolocate) {
						var dialog = new Bev.Dialog(null, {
							"text" : "定&nbsp;&nbsp;&nbsp;&nbsp;位"
						});

						var contentBody = dialog.getContentBody();
						geolocate = new Bev.Geolocate({
							"body" : contentBody,
							"map" : map
						});
						dialog.on("dialogclose", function() {
							if (geolocate) {
								geolocate.destroy();
								geolocate = null;
							}
						});
					}
				});
			}
		}
	};
	baseToolsMenu.addItem(param);
};

function addDraw(isSelect) {
	if (isSelect == "0")
		return;
	var param = {
		"icon_class" : "glyphicon-edit",
		"text" : "绘&nbsp;&nbsp;&nbsp;&nbsp;制",
		"events" : {//点击菜单中的一栏，创建一个绘制功能面板
			"click" : function() {
				Bev.loader.js([ "js/controls/Dialog.js",
						"js/controls/DrawFeature.js" ], function() {
					if (!drawFeature) {
						var dialog = new Bev.Dialog(null, {
							"text" : "绘&nbsp;&nbsp;&nbsp;&nbsp;制"
						});

						var contentBody = dialog.getContentBody();
						drawFeature = new Bev.DrawFeature({
							"body" : contentBody,
							"map" : map
						});
						dialog.on("dialogclose", function() {
							if (drawFeature) {
								drawFeature.destroy();
								drawFeature = null;
							}
						});
					}
				});
			}
		}
	};
	baseToolsMenu.addItem(param);
}

function addQueryControl(isSelect, options) {
	if (isSelect == "0")
		return;
	var param = {
		"icon_class" : "glyphicon-search",
		"text" : "查&nbsp;&nbsp;&nbsp;&nbsp;询",
		"events" : {//点击菜单中的一栏，创建一个绘制功能面板
			"click" : function() {
				Bev.loader.js(
						[ "js/controls/Dialog.js", "js/controls/Query.js" ],
						function() {
							if (!Bev.query) {
								var dialog = new Bev.Dialog(null, {
									"text" : "查&nbsp;&nbsp;&nbsp;&nbsp;询"
								});

								if (!Bev.queryResultDom) {
									Bev.queryResultDom = $("<div>").attr({
										"id" : "queryResult"
									}).css({
										"position" : "absolute",
										"bottom" : "5px",
										"left" : "5px",
										"background" : "#fff"
									}).appendTo($("#canvas"));
								}

								Bev.queryResultDom.html("");

								var contentBody = dialog.getContentBody();
								var param = {
									"body" : contentBody,
									"map" : window.map,
									"datasetName" : options.datasetName,
									"queryField" : options.queryField,
									"url" : options.url,
									"resultBody" : Bev.queryResultDom
								};
								var rf = options.resultTableFields;
								if (rf && rf != "") {
									param.resultTableFields = rf;
								}
								var ct = options.resultTableColumnTitles;
								if (ct && ct != "") {
									param.resultTableColumnTitles = ct;
								}
								Bev.query = new Bev.Query(param);
								dialog.on("dialogclose", function() {
									if (Bev.query) {
										Bev.query.destroy();
										Bev.query = null;
									}
								});

								// myWidgetControl.addWidget(dialog);
							}
						});
			}
		}
	};
	restMenu.addItem(param);
}

function addMapPrint(isSelect) {
	if (isSelect == "0")
		return;

	var t = this;
	var id = "mapContainer";
	var param = {
		"icon_class" : "glyphicon-mapPrint",
		"text" : "打印地图",
		"events" : {
			"click" : function() {
				var broz = SuperMap.Browser.name;
				if (broz == 'msie' && parseInt(SuperMap.Browser.version) < 9) {
					alert("ie9版本以下部分打印功能不支持");
					return;
				}
				var printWindow = window.open("");
				var strInnerHTML = document.getElementById(id).innerHTML;

				var strHeader = "<!DOCTYPE html><html><head><META HTTP-EQUIV='pragma' CONTENT='no-cache'><META HTTP-EQUIV='Cache-Control' CONTENT='no-cache, must-revalidate'><META HTTP-EQUIV='expires' CONTENT='Wed, 26 Feb 1997 08:21:57 GMT'><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' /><meta name='apple-mobile-web-app-capable' content='yes' /><title>地图打印</title>";
				var strCSS = "<link href='css/mapPrint.css' rel='stylesheet' />";
				var strScript = "<script src='js/jquery.js'><\/script><script type = 'text/javascript'>"
						+ "\n"
						+ "function printDiv(){$('.newuiPrint').css({'display':'none'});window.print();$('.newuiPrint').css({'display':'block'});}<\/script>";
				var strBody = "</head><body><div class='print-header'><div class='superD'><h3>地图</h3></div><div id='"
						+ id
						+ "' >"
						+ strInnerHTML
						+ "</div><div id='superft'><div class='printClose'>"
						+ "<span class='newuiPrint' onclick = 'printDiv()'></span></div></div></div></body></html>";

				var strHTML = strHeader + strCSS + strScript + strBody;
				printWindow.document.write(strHTML);
				printWindow.document.close();
				function onloadHTML() {
					var strDOM = printWindow.document.getElementById(id).children[0].children;
					for ( var i = 0, length = strDOM.length; i < length; i++) {
						var idStr = strDOM[i].id;
						if (idStr.indexOf("SuperMap.Control.ScaleLine") == -1
								&& idStr.indexOf("SuperMap.Map") == -1) {
							strCss = strDOM[i].style.cssText;
							strCss = strCss + "display: none;";
							strDOM[i].style.cssText = strCss;
						}
					}

					var canvasPrint = printWindow.document
							.getElementsByTagName("canvas");
					var canvasMap = document.getElementsByTagName("canvas");
					for ( var i = 0, length = canvasPrint.length; i < length; i++) {
						//                        pasteCanvas(canvasMap[i],canvasPrint[i]);
					}
				}
				if (broz == 'firefox') {
					printWindow.onload = onloadHTML;
				} else if (broz == 'safari' || broz == 'chrome'
						|| broz == 'msie') {
					window.setTimeout(onloadHTML, 50);
				}
			}
		}
	};
	baseToolsMenu.addItem(param);
}

function parseTempData(jsonObject) {
	var colors = [ "#ccfffa", "#a3f8b4", "#79f26f", "#2dd90b", "#8aee1e",
			"#c5ef0f", "#fff100", "#fcbd10", "#fb8722", "#d53b3b" ];
	var result = SuperMap.REST.Recordset.fromJson(jsonObject);
	if (result && result.features) {
		var len = result.features.length;
		var style = {
			strokeColor : "#cccccc",
			strokeWidth : 1,
			fillOpacity : "0.5"
		};
		var styles = [];
		for ( var i = 0; i < colors.length; i++) {
			var style0 = {
				strokeColor : style.strokeColor,
				strokeWidth : style.strokeWidth,
				fillColor : colors[i],
				fillOpacity : style.fillOpacity
			};
			styles.push(style0);
		}
		var features = [];
		for (i = 0; i < len; i++) {
			var feature = result.features[i];
			var data = feature.attributes;
			var value = data['DMAXVALUE'];
			if (value < -24) {
				feature.style = styles[0];
			} else if (value < -18) {
				feature.style = styles[1];
			} else if (value < -12) {
				feature.style = styles[2];
			} else if (value < -6) {
				feature.style = styles[3];
			} else if (value < 0) {
				feature.style = styles[4];
			} else if (value < 6) {
				feature.style = styles[5];
			} else if (value < 12) {
				feature.style = styles[6];
			} else if (value < 18) {
				feature.style = styles[7];
			} else if (value < 24) {
				feature.style = styles[8];
			} else {
				feature.style = styles[9];
			}
			features.push(feature);
		}
	}
	;
	return features;
}