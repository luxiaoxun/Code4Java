/**
 * Class: Bev.Measure
 * 量算功能。
 */
Bev.Measure = Bev.Class({

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
     * APIProperty: resultDiv
     * {HTMLElement} 结果显示面板
     */
    resultDiv:null,

    /**
     * Property: measureControls
     * {Object} 量算控件
     */
    measureControls:null,

    /**
     * Constructor: Bev.Measure
     * 实例化 Measure 类。
     *
     * Parameters:
     * option -{Object} 参数对象
     * body - {DOMElement} 页面上装载该控件的容器
     * map - {SuperMap.Map} 地图对象。
     *
     * Examples:
     * (code)
     * var myMeasure = new Bev.Measure({
     *     "body":$("<div>"),   //{DOMElement} 页面上装载该控件的容器
     *     "map":map            //{SuperMap.Map} 地图对象。
     * });
     * (end)
     */
    initialize: function(option) {
        var me = this;
        for(var key in option){
            this[key] = option[key];
        }
        Bev.loader.js([
            "js/ui/jquery.ui.core.js",
            "js/ui/jquery.ui.widget.js",
            "js/ui/jquery.ui.button.js",
            "js/ui/jquery.ui.bevbutton.js"
        ],function(){
            me.create();
        });
        me.createControl();
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function () {
        var d1, d2, d3, me = this;
        if(this.body){
            this.body = $(this.body);
            this.resultDiv = d1 = $("<p class='measureResult bv-textarea'></p>").appendTo(this.body);

            d2 = $("<button>长度量算</button>").click(function () {
                me.measureDistance();
            }).appendTo(this.body)
                .bevbutton({
                    icons:{
                        primary:"glyphicon-ruler-straight"
                    }
                });

           /* var btn1 = d2.button("option","buttonElement");
            var icon = btn1.children(".ui-icon");
//                    if(this.distanceIconOffset){
//                        icon.css({
//                            "background":"url("+this.defaultImagePath+") "+this.distanceIconOffset[0]+" "+this.distanceIconOffset[1]
//                        });
//                    }
            icon.addClass("sm-ui-icon");
            icon.addClass("distanceMeasure-icon");
            //icon.removeClass("ui-icon");
            icon.removeClass("ui-icon-locked");*/
            //icon.removeClass("ui-button-icon-primary");

//                    if(this.distanceIconOffset){
//                        d2.mouseover(function(icon){
//                           return function(){
//                               icon.css({
//                                   "background":"url("+me.hoverImagePath+") "+me.distanceIconOffset[0]+" "+me.distanceIconOffset[1]
//                               });
//                           }
//                        }(icon))
//                            .mouseout(function(icon){
//                               return function(){
//                                   icon.css({
//                                       "background":"url("+me.defaultImagePath+") "+me.distanceIconOffset[0]+" "+me.distanceIconOffset[1]
//                                   });
//                               }
//                            }(icon));
//                    }

            d3 = $("<button>面积量算</button>").bevbutton({
                icons:{
                    primary:"glyphicon-ruler-triangle"
                }
            }).click(function () {
                    me.measureArea();
                }).appendTo(this.body);

            /*var btn = d3.button("option","buttonElement");
            var icon = btn.children(".ui-icon");
            icon.addClass("sm-ui-icon");
            icon.addClass("areaMeasure-icon");
            //icon.removeClass("ui-icon");
            icon.removeClass("ui-icon-locked");*/
            //icon.removeClass("ui-button-icon-primary");

//                    if(this.areaIconOffset){
//                        icon.css({
//                            "background":"url("+this.defaultImagePath+") "+this.areaIconOffset[0]+" "+this.areaIconOffset[1]
//                        });
//                    }
//
//                    if(this.areaIconOffset){
//                        d3.mouseover(function(icon){
//                                return function(){
//                                    icon.css({
//                                        "background":"url("+me.hoverImagePath+") "+me.areaIconOffset[0]+" "+me.areaIconOffset[1]
//                                    });
//                                }
//                            }(icon))
//                            .mouseout(function(icon){
//                                return function(){
//                                    icon.css({
//                                        "background":"url("+me.defaultImagePath+") "+me.areaIconOffset[0]+" "+me.areaIconOffset[1]
//                                    });
//                                }
//                            }(icon));
//                    }

            /*window.setTimeout(function(btn1){
                return function(){
                    btn1&&btn1[0].blur();
                }
            }(btn1),30)*/
        }
    },
    /**
     * Method: createControl
     * 创建量算控件。
     */
    createControl:function () {
        var me = this;

        var sketchSymbolizers = {
            "Point":{
                pointRadius:3,
                graphicName:"square",
                fillColor:"#669933",
                fillOpacity:1,
                strokeWidth:2,
                strokeOpacity:1,
                strokeColor:"#aaee77"
            },
            "Line":{
                strokeWidth:3,
                strokeOpacity:1,
                strokeColor:"#aaee77"
            },
            "Polygon":{
                strokeWidth:2,
                strokeOpacity:1,
                strokeColor:"#aaee77",
                fillColor:"white",
                fillOpacity:0.3
            }
        };
        var style = new SuperMap.Style();
        style.addRules([
            new SuperMap.Rule({symbolizer:sketchSymbolizers})
        ]);
        var styleMap = new SuperMap.StyleMap({"default":style});
        this.measureControls = {
            line:new SuperMap.Control.Measure(
                SuperMap.Handler.Path, {
                    persist:true,
                    immediate:true,
                    handlerOptions:{
                        layerOptions:{
                            styleMap:styleMap
                        }
                    }
                }
            ),
            polygon:new SuperMap.Control.Measure(
                SuperMap.Handler.Polygon, {
                    persist:true,
                    immediate:true,
                    handlerOptions:{
                        layerOptions:{
                            styleMap:styleMap
                        }
                    }
                }
            )
        };

        for (var key in this.measureControls) {
            var control = this.measureControls[key];
            control.events.on({
                "measure":function (event) {
                    me.measureCompleted(event);
                }
                //,"measurepartial": handleMeasurements
            });
            this.map.addControl(control);
        }
    },
    /**
     * APIMethod: measureDistance
     * 距离量算。
     */
    measureDistance:function () {
        if (this.measureControls["polygon"].active) {
            this.measureControls["polygon"].deactivate();
        }
        this.measureControls["line"].activate();
        this.clearResult();
    },
    /**
     * APIMethod: measureArea
     * 面积量算。
     */
    measureArea:function () {
        if (this.measureControls["line"].active) {
            this.measureControls["line"].deactivate();
        }
        this.measureControls["polygon"].activate();
        this.clearResult();
    },
    /**
     * Method: measureCompleted
     * 量算完成。
     */
    measureCompleted:function (event) {
        var geometry = event.geometry;
        var units = event.units;
        var order = event.order;
        var measure = event.measure;
        if (order == 1) {
            this.showResult("长度：" + measure.toFixed(3) + units);
        } else {
            this.showResult("面积：" + measure.toFixed(3) + "k㎡");
        }
        this.deactivate();
    },
    /**
     * Method: clearResult
     * 清除结果区。
     */
    clearResult:function(){
        if(this.resultDiv)this.resultDiv.html("");
    },
    /**
     * Method: showResult
     * 在结果区显示量算结果。
     *
     * Parameters:
     * txt - {String} 显示在结果区的内容
     */
    showResult:function(txt){
        if(this.resultDiv)this.resultDiv.html(txt);
    },
    /**
     * APIMethod: clearFeatures
     * 清除量算结果。
     */
    clearFeatures:function () {
        try {
            this.deactivate();
            this.clearResult();
        }
        catch (e) {
        }
    },
    /**
     * APIMethod: destroy
     * 销毁。
     */
    destroy:function () {
        this.clearFeatures();
        for (var key in this.measureControls) {
            var control = this.measureControls[key];

            this.map.removeControl(control);
        }
    },
    /**
     * APIMethod: deactivate
     * 注销该控件。
     */
    deactivate:function () {
        for(var key in this.measureControls) {
            if(this.measureControls[key].activate)
            {
                this.measureControls[key].deactivate();
            }
        }
    },

    CLASS_NAME: "Bev.Measure"
});
