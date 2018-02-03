/**
 * Class: Bev.Geolocate
 * 定位功能。
 */
Bev.Geolocate = Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * APIProperty: map
     * {SuperMap.Map} map对象
     */
    map:null,

    /**
     * Property: geoMarker_bev
     * {SuperMap.Layer.Markers} 定位图标图层
     */
    geoMarker_bev:new SuperMap.Layer.Markers("geoMarker_bev"),

    /**
     * Property: geolocateControl
     * {Object} 地理定位控件
     */
    geolocateControl:null,


    /**
     * Constructor: Bev.Geolocate
     * 实例化 Geolocate 类。
     *
     * Parameters:
     * option - {Object} 参数对象
     *
     * Examples:
     * (code)
     *  myGeolocate = new Bev.Geolocate({
     *      "body":$("<div>"),  //{DOMElement} 页面上装载该控件的容器
     *      "map":map           //{SuperMap.Map} 地图对象。
     *  });
     * (end)
     */
    initialize: function(option) {
        var me = this;
        for(var key in option){
            this[key] = option[key];
        }

        this.map.addLayer(this.geoMarker_bev);
        Bev.loader.js([
            "js/ui/jquery.ui.core.js",
            "js/ui/jquery.ui.widget.js",
            "js/ui/jquery.ui.button.js",
            "js/ui/jquery.ui.bevbutton.js"
        ],function(){
            me.create();
        });
        this.createControl();
        this.map.addControl(this.geolocateControl);
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function () {
        var me = this,b1,b2;
        if(this.body){
            b1 = $("<button>地理定位</button>").bevbutton({
                icons:{
                    primary:"glyphicon-screenshot"
                }
            }).click(function () {
                    me.geolocateMe();
                }).appendTo($(this.body));

            /*var btn = b1.button("option","buttonElement");
            var icon = btn.children(".ui-icon");
            icon.addClass("glyphicon");
            icon.addClass("sm-ui-icon");
            icon.addClass("geolocate-icon");
            icon.removeClass("ui-icon-locked");*/
//                    if(this.geolocateIconOffset){
//                        icon.css({
//                            "background":"url("+this.defaultImagePath+") "+this.geolocateIconOffset[0]+" "+this.geolocateIconOffset[1]
//                        });
//                    }

//                    if(this.geolocateIconOffset){
//                        b1.mouseover(function(icon){
//                           return function(){
//                               icon.css({
//                                   "background":"url("+me.hoverImagePath+") "+me.geolocateIconOffset[0]+" "+me.geolocateIconOffset[1]
//                               });
//                           }
//                        }(icon))
//                            .mouseout(function(icon){
//                               return function(){
//                                   icon.css({
//                                       "background":"url("+me.defaultImagePath+") "+me.geolocateIconOffset[0]+" "+me.geolocateIconOffset[1]
//                                   });
//                               }
//                            }(icon));
//                    }

            b2 = $("<button>清除标记</button>").bevbutton({
                icons:{
                    primary:"glyphicon-trash"
                }
            }).click(function () {
                    me.clearGeoMarkers();
                }).appendTo($(this.body));

            /*var btn1 = b2.button("option","buttonElement");
            var icon = btn1.children(".ui-icon");
            icon.addClass("sm-ui-icon");
            icon.addClass("clear-icon");
            icon.removeClass("ui-icon-locked");*/
//                    if(this.clearIconOffset){
//                        icon.css({
//                            "background":"url("+this.defaultImagePath+") "+this.clearIconOffset[0]+" "+this.clearIconOffset[1]
//                        });
//                    }

//                    if(this.clearIconOffset){
//                        b2.mouseover(function(icon){
//                            return function(){
//                                icon.css({
//                                    "background":"url("+me.hoverImagePath+") "+me.clearIconOffset[0]+" "+me.clearIconOffset[1]
//                                });
//                            }
//                        }(icon))
//                            .mouseout(function(icon){
//                                 return function(){
//                                     icon.css({
//                                         "background":"url("+me.defaultImagePath+") "+me.clearIconOffset[0]+" "+me.clearIconOffset[1]
//                                     });
//                                 }
//                            }(icon));
//                    }

           /* window.setTimeout(function(){
                btn&&btn[0].blur();
            },30)*/
        }
    },

    /**
     * APIMethod: geolocateMe
     * 激活定位控件。
     */
    geolocateMe:function () {
        this.geolocateControl.deactivate();
        this.geolocateControl.activate();
    },

    /**
     * Method: createControl
     * 创建定位控件。
     */
    createControl:function () {
        var me = this;
        me.geolocateControl = new SuperMap.Control.Geolocate({
            bind:false,
            geolocationOptions:{
                enableHighAccuracy:false,
                maximumAge:0
            },
            eventListeners:{
                "locationupdated":me.getGeolocationCompleted,
                "locationfailed":me.getGeolocationFailed
            }
        });
    },

    /**
     * Method: getGeolocationCompleted
     * 定位完成执行操作。
     */
    getGeolocationCompleted:function (event) {
        var lonLat = new SuperMap.LonLat(event.point.x, event.point.y);
        var size = new SuperMap.Size(44, 33),
            offset = new SuperMap.Pixel(-(size.w / 2), -size.h),
            icon = new SuperMap.Icon("theme/images/marker.png", size, offset);
        this.map.getLayersByName("geoMarker_bev")[0].addMarker(new SuperMap.Marker(lonLat, icon));
        this.map.setCenter(lonLat);
    },

    /**
     * Method: getGeolocationFailed
     * 定位失败执行操作。
     */
    getGeolocationFailed:function (e) {
        alert("当前状态无法定位");
    },

    /**
     * APIMethod: destroy
     * 销毁。
     */
    destroy:function () {
        this.map.getLayersByName("geoMarker_bev")[0].clearMarkers();
        this.map.removeLayer(this.map.getLayersByName("geoMarker_bev")[0]);
        this.map.removeControl(this.geolocateControl);
    },

    /**
     * APIMethod: clearGeoMarkers
     * 清除定位标记。
     */
    clearGeoMarkers:function(){
        this.map.getLayersByName("geoMarker_bev")[0].clearMarkers();
        this.geolocateControl.deactivate();
    },

    /**
     * APIMethod: deactivate
     * 注销该控件。
     */
    deactivate:function () {
        this.geolocateControl.deactivate();
    },

    CLASS_NAME: "Bev.Geolocate"
});
