/**
 * Class: Bev.Query
 * 查询控件。
 */
Bev.Query = Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * APIProperty: resultBody
     * {HTMLElement} 查询结果容器
     */
    resultBody:null,

    /**
     * APIProperty: resultTableFields
     * {Array<String>} 显示结果的字段名称数组，例如：resultTableFields = ["SMID","NAME","AGE"];
     */
    resultTableFields:null,

    /**
     * APIProperty: resultTableColumnTitles
     * {Array<String>} 结果表格的列标题，例如：resultTableColumnTitles = ["ID","姓名","年龄"];
     */
    resultTableColumnTitles:null,

    /**
     * APIProperty: datasetName
     * {String} 查询数据集名称
     */
    datasetName:null,

    /**
     * APIProperty: queryField
     * {String} 查询字段名称,例如：attributeFilter:"{queryField} like '%{keyword}%'"
     */
    queryField:null,

    /**
     * APIProperty: map
     * {SuperMap.Map} 地图对象
     */
    map:null,

    /**
     * APIProperty: url
     * {String} 查询资源url
     */
    url:null,

    /**
     * APIProperty: resultTableScrollHeight
     * {String}  查询结果表格垂直滚动高度，默认为"150px"，也可以设置为"disabled"
     */
    resultTableScrollHeight:"150px",
    /**
     * APIProperty: resultTableScrollWidth
     * {String}  水平滚动宽度，默认为"disabled"，也可以设置为数值，如："600px"
     */
    resultTableScrollWidth:"disabled",

    /**
     * Property: inputArea
     * {HTMLElement} 输入框
     */
    inputArea:null,

    /**
     * Property: submitButton
     * {HTMLElement} 提交按钮
     */
    submitButton:null,

    /**
     * Property: markerLayer
     * {SuperMap.Layer.Markers} marker图层
     */
    markerLayer:null,

    /**
     * Property: dataTable
     * {Bev.DataTable} 查询结果表格
     */
    dataTable:null,

    /**
     * Property: markerHashMap
     * {Object} 存储marker的对象
     */
    markerHashMap:{},

    /**
     * Property: startIndex
     * {int} 查询起始记录数
     */
    startIndex:0,

    /**
     * Property: queryCount
     * {int} 期望查询返回的记录数
     */
    queryCount:0,

    /**
     * Property: popup
     * {SuperMap.Popup.FramedCloud} 弹出窗口
     */
    popup:null,

    /**
     * Property: featuresMap
     * {Object} 存储feature的对象，id为索引
     */
    featuresMap:null,

    /**
     * Property: boundsSelect
     * {HTMLElement} 查询范围选择框
     */
    boundsSelect:null,

    /**
     * Property: viewerMode
     * {String} 查询视图模式，当前视图(cur)还是全部(all)
     */
    viewerMode:"cur",

    /**
     * Constructor: Bev.Query
     * 实例化 Query 类。
     *
     * Parameters:
     * body - {HTMLElement} 父容器
     *
     * Examples:
     * (code)
     * var myQuery = new Bev.Query({
	 *     "body":$("#divid")
	 * });
     * (end)
     */
    initialize: function(options) {
        var t = this;
        for(var key in options){
            this[key] = options[key];
        }

        if(this.resultTableFields!=null){
            this.resultTableFields = this.resultTableFields.split(",");
        }
        if(this.resultTableColumnTitles!=null){
            this.resultTableColumnTitles = this.resultTableColumnTitles.split(",");
        }

        Bev.loader.js("js/ui/jquery.ui.bevbutton.js",function(){
            t.create();
        });
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function(){
        var t = this;

        if(this.body){
            var d1;

            d1 = $("<div>")
                .css({
                    "margin":"0px 10px 0px 10px"
                })
                .appendTo(this.body);

            this.inputArea = $("<input>")
                .attr({
                    type:"text",
                    title:"输入查询关键字，可以为空"
                })
                .addClass("bv-textarea queryInput")
                .css({
                    "height":"20px",
                    "border-radius":"0px",
                    "margin-right":"5px",
                    "width":"200px",
                    "text-align":"left"
                })
                .keyup(function(e){
                    if(e.keyCode==13){
                        var keyword = t.inputArea.attr("value");
                        t.queryByBounds(keyword);
                    }
                })
                .appendTo(d1);

            this.submitButton = $("<button>查询</button>")
                .bevbutton()
                .click(function(e){
                    var keyword = t.inputArea.attr("value");
                    t.queryByBounds(keyword);
                })
                .appendTo(d1);

            this.submitButton = $("<button>清除</button>")
                .bevbutton()
                .click(function(e){
                    t.clear();
                })
                .appendTo(d1);

            d1 = $("<div>")
                .css({
                    "margin":"10px 10px 0px 10px"
                })
                .appendTo(this.body);

            this.boundsSelect = $("<select>")
                .appendTo(d1)
                .change(function(){
                    var value = $(this).attr("value");
                    t.viewerMode = value;
                });

            var option1 = $("<option>")
                .html("查询当前范围")
                .attr({
                    "value":"cur"
                })
                .appendTo(this.boundsSelect);

            var option1 = $("<option>")
                .html("查询全部范围")
                .attr({
                    "value":"all"
                })
                .appendTo(this.boundsSelect);
        }
    },

    /**
     * APIMethod: clear
     * 清除查询结果
     */
    clear:function(){
        if(this.markerLayer)this.markerLayer.clearMarkers();
        if(this.dataTable){
            this.dataTable.clear();
        }
        this.markerHashMap = {};
        this.featuresMap = {};
        this.removePopup();

        if(this.resultBody){
            this.resultBody.css({
                "display":"none"
            });
        }
    },

    /**
     * Method: queryByBounds
     * 距离查询
     */
    queryByBounds:function(keyword){
        var t = this;

        if(!this.datasetName){
            alert("查询字段为空。");
            return;
        }
        if(!this.url){
            alert("查询REST资源url为空。");
            return;
        }

        if(!this.markerLayer){
            this.markerLayer = new SuperMap.Layer.Markers("Markers",{});
            this.map.addLayer(this.markerLayer);
        }

        this.clear();

        if(this.viewerMode=="all"){
            var queryBounds = this.map.getMaxExtent();
        }
        else{
            var queryBounds = this.map.getExtent();
        }

        var queryParam, queryByBoundsParams, queryService;
        var param1 = {name: this.datasetName};
        if(keyword){
            param1.attributeFilter = this.queryField +" like '%"+keyword+"%'";
        }
        queryParam = new SuperMap.REST.FilterParameter(param1);//FilterParameter设置查询条件，name是必设的参数，（图层名称格式：数据集名称@数据源别名）
        queryByBoundsParams = new SuperMap.REST.QueryByBoundsParameters({queryParams: [queryParam], bounds: queryBounds});//queryParams查询过滤条件参数数组。bounds查询范围
        queryService = new SuperMap.REST.QueryByBoundsService(this.url, {
            eventListeners: {
                "processCompleted": function(queryEventArgs){
                    t.processCompleted(queryEventArgs);
                },
                "processFailed": function(e){
                    t.processFailed(e);
                }
            }
        });
        queryService.processAsync(queryByBoundsParams);
    },

    /**
     * Method: processCompleted
     * 查询成功
     */
    processCompleted:function(queryEventArgs){
        if(this.resultBody)this.resultBody.css("display","block");
        this.featuresMap = {};
        var i, j, result = queryEventArgs.result;
        for(i = 0;i < result.recordsets.length; i++) {
            var fs = result.recordsets[i].features;
            if(fs.length==0){
                alert("未查询到结果");
            }
            for(j = 0; j < fs.length; j++) {
                var f = fs[j];
                var marker = this.createMarker(f);

                var id = f.attributes.SMID;
                this.featuresMap[id] = f;
            }

            this.createTable(result.recordsets[i].features);
        }
    },

    /**
     * Method: createPopup
     * 创建弹出窗口
     */
    createPopup:function(feature,row,columnTitles){
        if(row){

        }
        else{

        }
        var tp = this.getDataFromFeature(feature);

        var row = tp.row;
        var columnTitles = tp.columnTitles;

        var point = feature.geometry;

        var contentHTML = "<div style=\'font-size:12px; opacity: 0.8; overflow-y:hidden;\'>";
        for(var i=0;i<row.length;i++){
            var title = columnTitles[i];
            var value = row[i];

            contentHTML += "<div><span>"+title+"：</span><span>"+value+"</span></div>";
        }
        contentHTML += "</div>";

        var size = new SuperMap.Size(0, 30);
        var offset = new SuperMap.Pixel(0, -size.h);
        var icon = new SuperMap.Icon("img/marker.png", size, offset);

        this.popup = new SuperMap.Popup.FramedCloud("popwin",
            new SuperMap.LonLat(point.x,point.y),
            null,
            contentHTML,
            icon,
            true);
        this.map.addPopup(this.popup);
    },

    /**
     * Method:removePopup
     * 移除弹出窗口
     */
    removePopup:function(){
        if(this.popup){
            this.map.removePopup(this.popup);
            this.popup.destroy();
            this.popup = null;
        }
    },

    /**
     * Method: createMarker
     * 创建marker对象
     */
    createMarker:function(feature){
        var t = this;

        var point = feature.geometry;
        var index = feature.attributes.SMID;
        var size = new SuperMap.Size(24, 34),
            offset = new SuperMap.Pixel(-(size.w/2), -size.h),
            icon = new SuperMap.Icon("img/marker.png", size, offset);

        var marker = new SuperMap.Marker(new SuperMap.LonLat(point.x, point.y), icon);
        this.markerLayer.addMarker(marker);
        marker.bev_index = index;
        marker.isFocus = false;
        this.markerHashMap["m"+index] = marker;

        marker.events.on({
            "mouseover":function(){
//                if(this.isFocus||this.isDestroy){
//                    return;
//                }
//                console.log("over"+this.bev_index);
                //t.markerFocus(this);
            },
            "mouseout":function(){
//                if(!this.isFocus||this.isDestroy){
//                    return;
//                }
//                console.log("out"+this.bev_index);
                //t.markerBlur(this);
            },
            "click":function(){
                var index = this.bev_index;
                var f = t.featuresMap[index];

                t.removePopup();
                t.createPopup(f);
            },
            "scope": marker
        });

        return marker;
    },

    /**
     * Method: removeMarker
     * 移除marker对象
     */
    removeMarker:function(marker){
        this.markerLayer.removeMarker(marker);
        marker.isDestroy = true;
    },

    /**
     * Method: markerFocus
     * 选中marker状态
     */
    markerFocus:function(marker){
        var t = this;

        var lonlat = marker.getLonLat();
        var index = marker.bev_index;
        this.removeMarker(marker);

        var size = new SuperMap.Size(24, 34),
            offset = new SuperMap.Pixel(-(size.w/2), -size.h),
            icon = new SuperMap.Icon("img/marker1.png", size, offset);

        var newMarker = new SuperMap.Marker(lonlat, icon);
        this.markerLayer.addMarker(newMarker);
        newMarker.bev_index = index;
        marker.isFocus = true;
        this.dataTable.setHighlight(index);

        this.markerHashMap["m"+index] = newMarker;

        newMarker.events.on({
            "mouseout":function(){
                if(!this.isFocus||this.isDestroy){
                    return;
                }
                console.log("out"+this.bev_index);
                t.markerBlur(this);
            },
            "mouseover":function(){
                if(this.isFocus||this.isDestroy){
                    return;
                }
                console.log("over"+this.bev_index);
                t.markerFocus(this);
            },
            "click":function(){
                var index = this.bev_index;
                var f = t.featuresMap[index];

                t.removePopup();
                t.createPopup(f);
            },
            "scope": newMarker
        });

        return newMarker;
    },

    /**
     * Method: markerBlur
     * 选中marker状态
     */
    markerBlur:function(marker){
        var t = this;
        if(marker.isFocus==false){
            return;
        }

        var lonlat = marker.getLonLat();
        var index = marker.bev_index;
        this.removeMarker(marker);

        var size = new SuperMap.Size(24, 34),
            offset = new SuperMap.Pixel(-(size.w/2), -size.h),
            icon = new SuperMap.Icon("img/marker.png", size, offset);

        var newMarker = new SuperMap.Marker(lonlat, icon);
        this.markerLayer.addMarker(newMarker);
        newMarker.bev_index = index;
        this.dataTable.setUnHighlight(index);
        newMarker.events.on({
            "mouseover":function(){
                if(this.isFocus||this.isDestroy){
                    return;
                }
                console.log("over"+this.bev_index);
                t.markerFocus(this);
            },
            "mouseout":function(){
                if(!this.isFocus||this.isDestroy){
                    return;
                }
                console.log("out"+this.bev_index);
                t.markerBlur(this);
            },
            "scope": newMarker
        });

        this.markerHashMap["m"+index] = newMarker;

        return newMarker;
    },

    /**
     * Method: getDataArrayFromFeatureArray
     * 从feature数组获取数据
     */
    getDataArrayFromFeatureArray:function(fs){
        var data = [];
        var columnTitles = [];

        if(this.resultTableFields){
            if(this.resultTableColumnTitles){
                columnTitles = this.resultTableColumnTitles;
            }
            else{
                columnTitles = this.resultTableFields;
            }
            for(var i=0;i<fs.length;i++){
                var f = fs[i];
                var attr = f.attributes;
                var row = [];
                for(var j=0;j<this.resultTableFields.length;j++){
                    row.push(attr[this.resultTableFields[j]]);
                }
                data.push(row);
            }
        }
        else{
            if(this.resultTableColumnTitles){
                columnTitles = this.resultTableColumnTitles;
            }
            else{
                var attr = fs[0].attributes;
                for(var key in attr){
                    columnTitles.push(key);
                }

                for(var i=0;i<fs.length;i++){
                    var f = fs[i];
                    var attr = f.attributes;
                    var row = [];
                    for(var j=0;j<columnTitles.length;j++){
                        row.push(attr[columnTitles[j]]);
                    }
                    data.push(row);
                }
            }
        }

        return {
            "data":data,
            "columnTitles":columnTitles
        };
    },

    /**
     * Method: getDataFromFeature
     * 从feature获取数据
     */
    getDataFromFeature:function(f){
        var row = [];
        var columnTitles = [];

        if(this.resultTableFields){
            if(this.resultTableColumnTitles){
                columnTitles = this.resultTableColumnTitles;
            }
            else{
                columnTitles = this.resultTableFields;
            }
            var attr = f.attributes;
            var row = [];
            for(var j=0;j<this.resultTableFields.length;j++){
                row.push(attr[this.resultTableFields[j]]);
            }
        }
        else{
            if(this.resultTableColumnTitles){
                columnTitles = this.resultTableColumnTitles;
            }
            else{
                var attr = f.attributes;
                for(var key in attr){
                    columnTitles.push(key);
                }
                var attr = f.attributes;
                var row = [];
                for(var j=0;j<columnTitles.length;j++){
                    row.push(attr[columnTitles[j]]);
                }
            }
        }

        return {
            "row":row,
            "columnTitles":columnTitles
        };
    },

    /**
     * Method: createTable
     * 创建查询结果表格
     */
    createTable:function(fs){
        var t = this;
        if(!fs||fs.length==0){
             return;
        }

        var tp = this.getDataArrayFromFeatureArray(fs);

        var data = tp.data;
        var columnTitles = tp.columnTitles;

        if(!this.dataTable){
            Bev.loader.js(["js/controls/DataTable.js"],function(data,columnTitles){
                return function(){
                    t.dataTable = new Bev.DataTable({
                        "body": t.resultBody,
                        "data":data,
                        "columnTitles":columnTitles,
                        "click":function(info){
                            var id = info[0];
                            var f = t.featuresMap[id];
                            var point = f.geometry;
                            map.panTo(new SuperMap.LonLat(point.x,point.y));
                            t.removePopup();
                            t.createPopup(f);
                        },
                        "onrefresh":function(datas){
                            //your code
                        },
                        "mouseover":function(info){
                            var index = info[0];

                            //t.markerFocus(t.markerHashMap["m"+index]);
                        },
                        "mouseout":function(info){
                            var index = info[0];

                            //t.markerBlur(t.markerHashMap["m"+index]);
                        },
                        "isDisplayMenu":false,
                        "isDisplaySearch":false,
                        "isDisplayTableInfo":false,
                        "scrollHeight": t.resultTableScrollHeight,
                        "scrollWidth": t.resultTableScrollWidth
                    });
                    // myDataGrid.setHighlight(2);
                    //var data1 = myDataGrid.getDataOfCurPage();
                    //var data2 = myDataGrid.getAllData();

                    t.tableCloseBtn();
                }
            }(data,columnTitles));
        }
        else{
            t.dataTable.add(data);
        }
    },

    /**
     * Method: tableCloseBtn
     * 关闭表格的按钮
     */
    tableCloseBtn:function(){
        var t = this;

        window.setTimeout(function(){
            if(t.resultBody){
                var d1 = t.resultBody.find(".dataTables_wrapper").find("div")[0];
                $(d1).css("text-align","right");

                var btn = $("<button>")
                    .bevbutton({
                        icons: {
                            primary: "glyphicon-remove-sign"
                        },
                        text: false
                    })
                    .css({
                        //"position":"relative",
                        "width":"18px",
                        "height":"18px"
                        //"right":"5px"
                        //"zIndex":9
                    })
                    .click(function(){
                        if(t.resultBody){
                            t.resultBody.css({
                                "display":"none"
                            });
                        }
                    })
                    .appendTo($(d1));
            }
        },300);
    },

    /**
     * Method: processFailed
     * 查询失败
     */
    processFailed:function(e){
        alert(e.error.errorMsg);
    },

    /**
     * Method: destroy
     * 销毁该控件
     */
    destroy:function(){

    },

    CLASS_NAME: "Bev.Query"
});

