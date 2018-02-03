/**
 * Class: Bev.DataTable
 * 表格控件。
 *
 * Examples:
 * (code)
 * Bev.loader.js("js/controls/DataTable.js",function(){
 *     var myDataGrid = new Bev.DataTable({
 *         "body":document.getElementById("div"),
 *         "data":[
 *             ["小王","16","男","87"],        //数据
 *             ["小李","17","男","89"]
 *         ],
 *         "columnTitles":["姓名","年龄","性别","成绩"],
 *         "click":function(info){
 *              //your code
 *         },
 *         "onrefresh":function(datas){
 *             //your code
 *         },
 *         "mouseover":function(info){
 *             //your code
 *         },
 *         "mouseout":function(info){
 *             //your code
 *         }
 *     });
 *     myDataGrid.setHighlight(2);
 *     var data1 = myDataGrid.getDataOfCurPage();
 *     var data2 = myDataGrid.getAllData();
 * });
 * (end)
 */
Bev.DataTable = Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,
    /**
     * APIProperty: data
     * {Array<String>} 表格中要显示的数据,二维数组
     */
    data:null,
    /**
     * APIProperty: ColumnTitles
     * {Array<String>} 每一列的标题
     */
    columnTitles:null,

    /**
     * APIProperty: mouseover
     * {Function} mouseover事件
     */
    mouseover:null,
    /**
     * APIProperty: mouseout
     * {Function} mouseout事件
     */
    mouseout:null,
    /**
     * APIProperty: click
     * {Function} click事件
     */
    click:null,
    /**
     * APIProperty: onrefresh
     * {Function} 表格刷新事件，当翻页或是其他操作导致表格刷新时，会触发该事件，并返回当前显示的数据
     */
    onrefresh:null,
    /**
     * APIProperty: isDisplayMenu
     * {Boolean}  是否显示左上角的菜单
     */
    isDisplayMenu:true,
    /**
     * APIProperty: isDisplaySearch
     * {Boolean}  是否显示搜索框
     */
    isDisplaySearch:true,
    /**
     * APIProperty: isSimplePagination
     * {Boolean}  表格底部页码是否显示简版，简版只有“上一页”和“下一页”两个按钮
     */
    isSimplePagination:false,
    /**
     * APIProperty: isDisplayPagination
     * {Boolean}  是否显示右下角的页码
     */
    isDisplayPagination:true,
    /**
     * APIProperty: isDisplayTableInfo
     * {Boolean}  是否显示左下角表格信息
     */
    isDisplayTableInfo:true,
    /**
     * APIProperty: scrollHeight
     * {String}  垂直滚动高度，默认为"150px"，也可以设置为"disabled"
     */
    scrollHeight:"150px",
    /**
     * APIProperty: scrollWidth
     * {String}  水平滚动宽度，默认为"disabled"，也可以设置为数值，如："600px"
     */
    scrollWidth:"disabled",
    /**
     * Property: isInit
     * {Boolean} 是否已经初始化
     */
    isInit:false,
    /**
     * Property: table
     * {HTMLElement} 表格对象
     */
    table:null,
    /**
     * Property: userColumnTitles
     * {Array<String>} 用户设置的标题数组
     */
    userColumnTitles:null,

    /**
     * Property: isFilesLoaded
     * {Boolean} 依赖文件是否加载完成
     */
    isFilesLoaded:false,

    /**
     * Constructor: Bev.DataTable
     * 实例化 DataTable 类。
     *
     * Parameters:
     * options - {Object} 参数
     */
    initialize: function(options) {
        var me = this;
        for(var key in options){
            this[key] = options[key];
        }

        Bev.loader.css("css/demo_table_jui.css");

        this.loadFiles(function(){
            me.create();
            window.setTimeout(function(){
                me.isInit = true;
                if(!me.isDisplayMenu){
                    $(me.body).find(".dataTables_length").css({"display":"none"});
                }
                if(!me.isDisplaySearch){
                    $(me.body).find(".dataTables_filter").css({"display":"none"});
                }
                if(!me.isDisplayPagination){
                    $(me.body).find(".dataTables_paginate").css({"display":"none"});
                }
                if(!me.isDisplayTableInfo){
                    $(me.body).find(".dataTables_info").css({"display":"none"});
                }
            },100);
        });
    },

    /**
     * APIMethod: setHighlight
     * 设置某条数据高亮
     *
     * Parameters:
     * id - {Number} 索引
     */
    setHighlight:function(id){
        var me=this,tb = this.table;

        this.loadFiles(function(){
            var tds,td,tr,trs;

            trs = tb.find("tr");
            for(var i=0;i<trs.length;i++){
                tr = $(trs[i]);
                tds = tr.children("td");
                td = tds[0];
                id1 = $(td).html();
                if(id1==id+""){
                    me.highlight(tr);
                    break;
                }
            }
        });

    },

    /**
     * APIMethod: setUnHighlight
     * 设置某条数据高亮
     *
     * Parameters:
     * id - {Number} 索引
     */
    setUnHighlight:function(id){
        var me=this,tb = this.table;

        this.loadFiles(function(){
            var tds,td,tr,trs;

            trs = tb.find("tr");
            for(var i=0;i<trs.length;i++){
                tr = $(trs[i]);
                tds = tr.children("td");
                td = tds[0];
                id1 = $(td).html();
                if(id1==id+""){
                    me.unHighlight(tr);
                    break;
                }
            }
        });

    },

    /**
     * APIMethod: getDataOfCurPage
     * 获取当前页所显示的数据
     */
    getDataOfCurPage:function(){
        return this.getData(true);
    },

    /**
     * APIMethod: getAllData
     * 获取所有数据
     */
    getAllData:function(){
        return this.getData(false);
    },

    /**
     * APIMethod: clear
     * 清空表格
     */
    clear:function(){
        var t = this;

        this.loadFiles(function(){
            if(t.table)t.table.fnClearTable();
            t.data = null;
            if(t.userColumnTitles&&t.columnTitles)t.columnTitles = t.userColumnTitles.concat([]);
        });
    },

    /**
     * APIMethod: close
     * 关闭
     */
    close:function(){
        var t = this;

        this.loadFiles(function(){
            if(t.table){
                t.table.fnClose();
            }
        });
    },

    /**
     * APIMethod: add
     * 往表格中添加数据
     *
     * Parameters:
     * data - {Array} 新添加的数据，可以是一维数组，也可以是二维数组
     */
    add:function(data){
        var me = this;

        this.loadFiles(function(data){
            return function(){
                var vl;

                if(!(data[0] instanceof Array)){
                    data = [data];
                }

                var startIndex = 0;
                if(me.data){
                    startIndex = me.data.length;
                }
                vl = me.setDataId(me.userColumnTitles.concat([]),data,startIndex);
                if(me.data){
                    me.data = me.data.concat(vl[1]);
                }
                else{
                    me.data = vl[1];
                }

                if(me.table){
                    me.table.fnAddData(data);
                    me.registerEvernt();
                }
            }
        }(data));
    },

    /**
     * Method: getData
     * 获取数据
     *
     * Parameters:
     * isCurPage - {Boolean}  是否是当前页
     */
    getData:function(isCurPage){
        var datas=[],me=this,tb = this.table,tds,td,tr,trs;

        if(isCurPage)trs = tb.children("tbody").children("tr");
        else trs = tb.$("tr");
        for(var i=0;i<trs.length;i++){
            tr = $(trs[i]);
            tds = tr.children("td");
            var data = [];
            for(var j=0;j<tds.length;j++){
                data.push($(tds[j]).html());
            }
            datas.push(data);
        }

        return datas;
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function(){
        var me=this,tb,colParam,cts,vl;

        colParam = [];
        me.userColumnTitles = me.columnTitles.concat([]);
        vl = me.setDataId(me.columnTitles,me.data);
        cts = me.columnTitles = vl[0];
        me.data = vl[1];
        for(var i=0;i<cts.length;i++){
            colParam.push({
                "sTitle":cts[i],
                "sClass":"center"
            });
        }
        me.table = tb = $("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\"></table>");
        tb.appendTo($(me.body));
        $(me.body).css({
            "font-size":"13px",
            "font-family":"none"
        });
        tb.dataTable({
            "sScrollY":me.scrollHeight,
            "sScrollX":me.scrollWidth,
            "bJQueryUI":true,
            "sPaginationType": me.isSimplePagination?"two_button":"full_numbers",
            "aaData":me.data,
            "aoColumns":colParam,
            "oLanguage": {
                "sLengthMenu": "每页显示 _MENU_ 条",
                //"sZeroRecords": "Nothing found - sorry",
                "sInfo": "从 _START_ 到 _END_ 共 _TOTAL_ 条",
                "sInfoEmpty": "从 0 到 0 共 0 条",
                //"sInfoFiltered": "(filtered from _MAX_ total records)",
                "sSearch":"搜索:",
                "oPaginate": {
                    "sFirst": "首页",
                    "sLast":"末页",
                    "sPrevious":"上一页",
                    "sNext":"下一页"
                }
            },
            "fnFooterCallback": function ( nRow, aaData, iStart, iEnd, aiDisplay ) {
                if(me.onrefresh&&me.isInit){
//                            var data=[];
//                            if(aaData&&aaData.length>0){
//                                data = aaData.slice(iStart,iEnd);
//                                me.onrefresh(data);
//                            }
                    window.setTimeout(function(){
                        var data = me.getDataOfCurPage();
                        me.onrefresh(data);
                    },30)
                }
            }
        });

        me.registerEvernt();
    },
    /**
     * Method: highlight
     * 设置高亮
     *
     * Parameters:
     * tr - {HTMLElement}  表格的tr对象
     */
    highlight:function(tr){
        var me = this;

        me.table.$('tr.tb_click').removeClass("tb_click");
        me.table.$('td.highlighted').removeClass('highlighted');
        tr.addClass("tb_click");
        tr.children("td").addClass('highlighted');
    },
    /**
     * Method: unHighlight
     * 取消高亮
     *
     * Parameters:
     * tr - {HTMLElement}  表格的tr对象
     */
    unHighlight:function(tr){
        var me = this;

        tr.removeClass("tb_click");
        tr.children("td").removeClass('highlighted');
    },
    /**
     * Method: getInformation
     * 从tr上获取信息。
     */
    getInformation:function(tr){
        var info=[],tds,td;

        tds = tr.children("td");
        for(var i=0;i<tds.length;i++){
            td = tds[i];
            info.push($(td).html());
        }

        return info;
    },
    /**
     * Method: setDataId
     * 设置data上的id信息。
     */
    setDataId:function(columnTitles,data,startIndex){
        var me = this,idIndex=null,t1;
        if(columnTitles&&data){
            for(var i=0;i<columnTitles.length;i++){
                if(me.isId(columnTitles[i])){
                    idIndex = i;
                }
            }
            if(!startIndex)startIndex=0;
            for(var i=0;i<data.length;i++){
                t1 = data[i];

                if(idIndex==null){
                    data[i].unshift(i+1+startIndex);
                }
                else{
                    var id = t1.splice(idIndex,1);
                    t1.unshift(id);
                    data[i] = t1;
                }
            }
            if(idIndex==null){
                columnTitles.unshift("SMID");
                idIndex = 0;
            }
        }

        return [columnTitles,data];
    },
    /**
     * Method: isId
     * 判断该字段是否是id字段。
     */
    isId:function(str){
        if(str){
            str = str.toLowerCase();
        }
        if(str=="id"||str=="smid"){
            return true;
        }
        else{
            return false;
        }
    },
    /**
     * Method: registerEvernt
     * 注册事件
     */
    registerEvernt:function(){
        var tb = this.table,me = this;
        tb.$("tr").hover(
            function(){
                $(this).children("td").addClass('highlighted');
                $(this).css({
                    "cursor":"pointer"
                });
                if(me.mouseover)me.mouseover(me.getInformation($(this)));
            },
            function(){
                if(!$(this).hasClass("tb_click")){
                    $(this).children("td.highlighted").removeClass('highlighted');
                }
                if(me.mouseout)me.mouseout(me.getInformation($(this)));
            }
        ).click(function(){
                me.highlight($(this));
                if(me.click)me.click(me.getInformation($(this)));
            });
    },

    /**
     * Method: loadFiles
     * 加载依赖文件
     */
    loadFiles:function(cb){
        var t = this;
        if(!this.isFilesLoaded){
            Bev.loader.js("js/ui/jquery.dataTables.js",function(cb){
                t.isFilesLoaded = true;
                window.setTimeout(function(cb){
                    return function(){
                        cb();
                    }
                }(cb),100);
            }(cb));
        }
        else{
            cb();
        }
    },

    CLASS_NAME: "Bev.DataTable"
});
