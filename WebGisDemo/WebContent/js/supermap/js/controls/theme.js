/**
 * Class: Bev.Theme
 * 主题类,设置框架的主题。
 */
Bev.Theme = {};
Bev.Theme.themeName = "cupertino";
Bev.Theme.currentTheme = "";
/**
 * APIMethod: set
 * 设置主题
 *
 * Parameters:
 * themeName - {String} 主题名称
 *
 * Examples:
 * (code)
 * Bev.Theme.set("base");
 * //可以设置的主题如下:
 * //cupertino
 * //base
 * //eggplant
 * //flick
 * //overcast
 * //pepper-grinder
 * //redmond
 * //smoothness
 * //start
 * //ui-darkness
 * //vader
 * (end)
 */
Bev.Theme.set = function(themeName) {
	var path;
	if (!themeName)
		themeName = Bev.Theme.themeName;
	Bev.Theme.currentTheme = themeName;
	path = [ "js/supermap/uithemes/" + themeName + "/jquery.ui.all.css" ];
	path.push("js/supermap/uithemes/" + themeName + "/jquery.ui.theme.css");//jquery.ui.theme
	path.push("js/supermap/css/" + themeName + "/theme.css");
	path.push("js/supermap/css/icon.css");
	/*Bev.loader.css(path,function(){
	    Bev.Theme.setStyle(themeName);
	    Bev.Theme.setFontColor(themeName);
	    $(window).resize();
	});*/
	Bev.loader.css(path);

	//Bev.Theme.setIconStyle(themeName);
}
Bev.Theme.setIconStyle = function(themeName) {
	var path = "js/supermap/css/icons/" + themeName + ".css";
	Bev.loader.css(path);
}

/**
 * APIMethod: getCurrentTheme
 * 获取当前的主题名
 *
 * Returns:
 * currentTheme - {string}当前的主题名
 * */
Bev.Theme.getCurrentTheme = function() {
	if (Bev.Theme.currentTheme == "")
		return Bev.Theme.themeName;
	return Bev.Theme.currentTheme;
}
/**
 * APIMethod: createStyle
 * 动态创建css样式
 *
 * Parameters:
 * css - {String} css样式
 */
Bev.Theme.createStyle = function(css) {
	if (document.all) {
		window.style = css;
		document.createStyleSheet("javascript:style");
	} else {
		var style = document.createElement('style');
		style.type = 'text/css';
		style.innerHTML = css;
		document.getElementsByTagName('head').item(0).appendChild(style);
	}
};
/**
 * Method: setFontColor
 * 对几个个别的主题的文字颜色做处理
 *
 * Parameters:
 * theme - {String} 主题名称
 */
Bev.Theme.setFontColor = function(theme) {
	var themesArr = "south-street,black-tie,eggplant,excite-bike,flick,pepper-grinder,vader";

	var para = {
		"black-tie" : "#3472AC",
		"eggplant" : "#3472AC",
		"vader" : "#3472AC"
	};
	if (themesArr.indexOf(theme) >= 0) {
		var cssTxt = ".tab_txt,.menu_txt,.dialog_title_txt {" + "color: "
				+ (para[theme] || "#0D1314") + "}";
		Bev.Theme.createStyle(cssTxt);
	}
}
/**
 * Method: setStyle
 * 对一些样式做特别处理
 */
Bev.Theme.setStyle = function() {
	var cssTxt, borderCss;

	$("#head").addClass("bev-banner").css({
		//        "opacity":0.9,
		//        "filter":"alpha(opacity=90)",
		"fontWeight" : "normal"
	});

	$("#left_back").addClass("ui-widget-overlay");
	$("#toolbar_back").addClass("ui-widget-overlay");
	try {
		//var testbtn = document.getElementById("hideBtn");
		//var border = this.getStyleProperty(document.getElementById("hideBtn"),"border");//$("#hideBtn").css("border");
		$("#bd_left").addClass("ui-state-default").css({
			"border-top" : "0px solid",
			"border-left" : "0px solid",
			"border-bottom" : "0px solid",
			"font-weight" : "normal"
		//                "background":"none repeat scroll 0 0 transparent",
		//                "color":"transparent"
		});

		$("#bd_toolbar").addClass("ui-state-default").css({
			"border-top" : "0px solid",
			"border-left" : "0px solid",
			"border-right" : "0px solid",
			"font-weight" : "normal"
		//                "background":"none repeat scroll 0 0 transparent",
		//                "color":"transparent"
		});
		//            $("#bd_toolbar").css({
		//                "border-bottom":border
		//            });
		$("#hideBtn").css({
			"opacity" : 0.8,
			"filter" : "alpha(opacity=80)"
		})
	} catch (e) {
	}

	borderCss = $(".ui-widget-header").css("border");
	cssTxt = ".widgetControl {" + "border: " + borderCss + "}";
	Bev.Theme.createStyle(cssTxt);
}
