/**
 * Class: Bev.Util
 * Bev静态方法。
 */
Bev.Util=Bev.Util||{};
/**
 * APIMethod: getSize
 * 获取dom元素的像素大小
 *
 * Parameters:
 * dom - {HTMLElement} dom元素
 */
Bev.Util.getSize=function(dom){
    var a = dom.clone();

    a.css({
        "left":"-5000px",
        "position":"absolute"
    })
        .appendTo($("body"));

    var w = a.width();
    var h = a.height();

    a.remove();

    return {"w":w,"h":h};
};
Bev.Util._imgName=[
    "tool_16_16.png",
    "tool_20_20.png",
    "clear_16_16.png",
    "clear_20_20.png",
    "draw_16_16.png",
    "draw_20_20.png",
    "drawarea_16_16.png",
    "drawarea_20_20.png",
    "drawline_16_16.png",
    "drawline_20_20.png",
    "drawpoint_16_16.png",
    "drawpoint_20_20.png",
    "geolocate_16_16.png",
    "geolocate_20_20.png",
    "measureArea_16_16.png",
    "measureArea_20_20.png",
    "measureDistance_16_16.png",
    "measureDistance_20_20.png",
    "mesure_16_16.png",
    "mesure_20_20.png",
    "resizemap.png",
    "zoomin_14_14.png",
    "zoomout_14_14.png"
];
/**
 * APIMethod: getImgNames
 * 获取内置的图片名称数组
 */
Bev.Util.getImgNames=function(){
    return this._imgName;
};
/**
 * APIMethod: getImgPath
 * 根据图片名称获取图片路径
 */
Bev.Util.getImgPath=function(imgName){
    return "img/" + Bev.Theme.getCurrentTheme() + "/" + imgName;
};
/**
 * APIMethod: createStyle
 * 动态创建css样式
 *
 * Parameters:
 * css - {String} css样式
 */
Bev.Util.createStyle = function(css){
    if(document.all){
        //window.style=css;
        //document.createStyleSheet("javascript:style");

        var style = document.createElement('style');
        style.type = 'text/css';
        style.styleSheet.cssText=css;
        document.getElementsByTagName('head').item(0).appendChild(style);
    }else{
        var style = document.createElement('style');
        style.type = 'text/css';
        style.innerHTML=css;
        document.getElementsByTagName('head').item(0).appendChild(style);
    }
};

