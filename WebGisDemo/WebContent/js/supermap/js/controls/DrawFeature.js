/**
 * Class: Bev.DrawFeature
 * 绘制要素功能。
 */
Bev.DrawFeature = Bev.Class({

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
     * {SuperMap.Layer.Vector} 矢量要素图层
     */
    drFeVector_bev:new SuperMap.Layer.Vector("drFeVector_bev"),

    /**
     * Property: geolocateControl
     * {Object} 要素绘制控件
     */
    drawFeatureControls:null,


    /**
     * Constructor: Bev.DrawFeature
     * 实例化 DrawFeature 类。
     *
     * Parameters:
     * option - {Object} 参数对象
     *
     * Examples:
     * (code)
     *  myDrawFeature = new Bev.DrawFeature({
     *      "body":$("<div>"),        //{DOMElement} 页面上装载该控件的容器
     *      "map":map                 //{SuperMap.Map} 地图对象。
     *  });
     * (end)
     */
    initialize: function(option) {
        var me = this;
        for(var key in option){
            this[key] = option[key];
            if(key=="body")
            {
                this[key]=$(option[key]);
            }
        }
        this.setMap(this.map);
        Bev.loader.js([
            "js/ui/jquery.ui.core.js",
            "js/ui/jquery.ui.widget.js",
            "js/ui/jquery.ui.bevbutton.js"
        ],function(){
            me.create();
        });
        //this.create();
        //this.createControl();
    },

    /**
     * APIMethod: setMap
     * 设置map参数
     *
     * Parameters:
     * map - {SuperMap.Map} map对象
     */
    setMap:function(map){
        if(map){
            this.map = map;
            this.map.addLayer(this.drFeVector_bev);
            this.createControl();
        }
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function () {
        var me = this,b1,b2,b3,b4;
        b1 = $("<button id='point'>绘制点</button>").bevbutton({
            icons:{
                primary:"glyphicon-draw-point"
            }
        }).click(function (e) {
                me.drawFeature(e);
            }).appendTo($(this.body));

        //var btn1 = b1.button("option","buttonElement");
        //var icon = btn1.children(".ui-icon");
        //icon.addClass("sm-ui-icon");
//                if(this.pointIconOffset){
//                    icon.css({
//                        "background":"url("+this.defaultImagePath+") "+this.pointIconOffset[0]+" "+this.pointIconOffset[1]
//                    });
//                }
//                if(this.pointIconOffset){
//                    b1.mouseover(function(icon){
//                        return function(){
//                            icon.css({
//                                "background":"url("+me.hoverImagePath+") "+me.pointIconOffset[0]+" "+me.pointIconOffset[1]
//                            });
//                        }
//                    }(icon))
//                        .mouseout(function(icon){
//                            return function(){
//                                icon.css({
//                                    "background":"url("+me.defaultImagePath+") "+me.pointIconOffset[0]+" "+me.pointIconOffset[1]
//                                });
//                            }
//                        }(icon));
//                }

        b2 = $("<button id='line'>绘制线</button>").bevbutton({
            icons:{
                primary:"glyphicon-draw-line"
            }
        }).click(function (e) {
                me.drawFeature(e);
            }).appendTo($(this.body));

       // var btn = b2.button("option","buttonElement");
        //var icon = btn.children(".ui-icon");
        //icon.addClass("sm-ui-icon");
//                if(this.lineIconOffset){
//                    icon.css({
//                        "background":"url("+me.defaultImagePath+") "+me.lineIconOffset[0]+" "+me.lineIconOffset[1]
//                    });
//                }
//
//                if(this.lineIconOffset){
//                    b2.mouseover(function(icon){
//                        return function(){
//                            icon.css({
//                                "background":"url("+me.hoverImagePath+") "+me.lineIconOffset[0]+" "+me.lineIconOffset[1]
//                            });
//                        }
//                    }(icon))
//                        .mouseout(function(icon){
//                            return function(){
//                                icon.css({
//                                    "background":"url("+me.defaultImagePath+") "+me.lineIconOffset[0]+" "+me.lineIconOffset[1]
//                                });
//                            }
//                        }(icon));
//                }

        b3 = $("<button id='polygon'>绘制面</button>").bevbutton({
            icons:{
                primary:"glyphicon-draw-polygon"
            }
        }).click(function (e) {
                me.drawFeature(e);
            }).appendTo($(this.body));

        //var btn = b3.button("option","buttonElement");
        //var icon = btn.children(".ui-icon");
        //icon.addClass("sm-ui-icon");
//                if(this.areaIconOffset){
//                    icon.css({
//                        "background":"url("+me.defaultImagePath+") "+me.areaIconOffset[0]+" "+me.areaIconOffset[1]
//                    });
//                }
//
//                if(this.areaIconOffset){
//                    b3.mouseover(function(icon){
//                        return function(){
//                            icon.css({
//                                "background":"url("+me.hoverImagePath+") "+me.areaIconOffset[0]+" "+me.areaIconOffset[1]
//                            });
//                        }
//                    }(icon))
//                        .mouseout(function(icon){
//                            return function(){
//                                icon.css({
//                                    "background":"url("+me.defaultImagePath+") "+me.areaIconOffset[0]+" "+me.areaIconOffset[1]
//                                });
//                            }
//                        }(icon));
//                }

        b4 = $("<button id='clearFeatures'>清除绘制</button>").bevbutton({
            icons:{
                primary:"glyphicon-trash"
            }
        }).click(function () {
                me.clearFeatures();
            }).appendTo($(this.body));

        //var btn = b4.button("option","buttonElement");
        //var icon = btn.children(".ui-icon");
        //icon.addClass("glyphicon");
        //icon.addClass("glyphicon-trash");
        //icon.addClass("sm-ui-icon");
        //icon.removeClass("ui-button-icon-primary");
        //icon.removeClass("ui-icon");
        //icon.removeClass("ui-icon-locked");
//                if(this.clearIconOffset){
//                    icon.css({
//                        "background":"url("+this.defaultImagePath+") "+this.clearIconOffset[0]+" "+this.clearIconOffset[1]
//                    });
//                }
//                if(this.clearIconOffset){
//                    b4.mouseover(function(icon){
//                        return function(){
//                            icon.css({
//                                "background":"url("+me.hoverImagePath+") "+me.clearIconOffset[0]+" "+me.clearIconOffset[1]
//                            });
//                        }
//                    }(icon))
//                        .mouseout(function(icon){
//                            return function(){
//                                icon.css({
//                                    "background":"url("+me.defaultImagePath+") "+me.clearIconOffset[0]+" "+me.clearIconOffset[1]
//                                });
//                            }
//                        }(icon));
//                }

       /* window.setTimeout(function(){
            if(btn1)btn1[0].blur();
        },30)*/
    },

    /**
     * Method: createControl
     * 创建绘制控件。
     */
    createControl:function () {
        var me = this;
        me.drawFeatureControls = {
            point:new SuperMap.Control.DrawFeature(me.drFeVector_bev, SuperMap.Handler.Point, {featureAdded:this.featureAdded}),
            line:new SuperMap.Control.DrawFeature(me.drFeVector_bev, SuperMap.Handler.Path, {featureAdded:this.featureAdded}),
            polygon:new SuperMap.Control.DrawFeature(me.drFeVector_bev, SuperMap.Handler.Polygon, {featureAdded:this.featureAdded})
        };

        for (var key in me.drawFeatureControls) {
            me.map.addControl(me.drawFeatureControls[key]);
        }
    },

    /**
     * Method: drawFeature
     * 激活绘制要素控件。
     */
    drawFeature:function (e) {
        var me = this;
        var value = e.currentTarget.id;
        for (key in me.drawFeatureControls) {
            var control = me.drawFeatureControls[key];
            if (value == key) {
                control.activate();
            } else {
                control.deactivate();
            }
        }
    },

    /**
     * Method: featureAdded
     * 要素添加后取消控件激活。
     */
    featureAdded:function () {
        this.deactivate();
    },

    /**
     * APIMethod: clearFeatures
     * 清除要素。
     */
    clearFeatures:function () {
        this.map.getLayersByName("drFeVector_bev")[0].removeAllFeatures();
    },
    /**
     * APIMethod: destroy
     * 在地图上移除控件。
     */
    destroy:function () {
        this.clearFeatures();
        for (var key in this.drawFeatureControls) {
            var control = this.drawFeatureControls[key];
            if (control.activate) {
                control.deactivate();
            }
            this.map.removeControl(control);
        }
    },

    /**
     * APIMethod: deactivate
     * 注销该控件。
     */
    deactivate:function () {
        var me = this;
        for (var key in me.drawFeatureControls) {
            if (me.drawFeatureControls[key].activate) {
                me.drawFeatureControls[key].deactivate();
            }
        }
    },

    CLASS_NAME: "Bev.DrawFeature"
});
