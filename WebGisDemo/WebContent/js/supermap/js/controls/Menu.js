/**
 * Class: Bev.Menu
 * 菜单控件。
 */
Bev.Menu = Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * APIProperty: tree
     * {Array} 目录结构
     *
     *(code)
     * "tree":[
     *     {
     *          "icon":"imagePath",
     *          "hover_icon":"imagePath"
     *          "text":"标题",
     *          "events":{
     *              "click":function(){},
     *              "mouseover":function(){},
     *              "mouseout":function(){}
     *          }
     *      }
     *  ]
     * (end)
     */
    tree:null,

    /**
     * APIProperty: menuBody
     * {HTMLElement} 内容区域
     */
    menuBody:null,
    itemArray:null,
    /**
     * Property: _events
     * {Array} items的事件列表
     */
    _events:null,

    /**
     * APIProperty: visible
     * {boolean} 设定菜单创建后是否立即显示，默认为false
     *
     * */
    visible:false,

    /**
     * Property: isFilesLoaded
     * {boolean} 依赖的文件是否加载完成
     *
     * */
    isFilesLoaded:false,

    /**
     * Constructor: Bev.Menu
     * 实例化 Menu 类。
     *
     * Parameters:
     * options {Object} 初始化需要的参数
     *
     * Examples:
     * (code)
     * var myMenu = new Bev.Menu({
     *     "body":$("#id"),
     *     "tree":[
     *          {
     *              "icon":"imagePath",
     *              "hover_icon":"imagePath",
     *              "icon_class":"distanceMeasure-icon",
     *              "text":"量&nbsp;&nbsp;&nbsp;&nbsp;算",
     *              "events":{
     *                  "click":function(){}
     *              }
     *          }
     *      ]
     * });
     * (end)
     */
    initialize: function(options) {
        this.tree = [];
        this.itemArray = [];
        this._events = [];

        for(var key in options){
            this[key] = options[key];
        }
        var ul;

        this.menuBody = ul = this.createMenu();

        if (this.body){
            ul.appendTo($(this.body));
        }
        if(!this.visible){
            ul.css("display","none");
        }

        for(var i=0;i<this.tree.length;i++){
            this._events[i] = {
                click:[],
                mouseover:[],
                mouseout:[]
            };
            var event = this.tree[i].events;
            if(event){
                if(event.click){
                    this._events[i].click.push(event.click);
                }
                if(event.mouseover){
                    this._events[i].mouseover.push(event.mouseover);
                }
                if(event.mouseout){
                    this._events[i].mouseout.push(event.mouseout);
                }
            }
        }

        this.bindEvents();
    },

    /**
     * Method: createMenu
     * 创建该控件的dom对象。
     */
    createMenu:function () {
        var ul, li,li_icon,li_text, tree = this.tree, para, itemArr = [], itm;
        //var css=".ui-widget-content a {"+
        //"color: #222222;"+
        //"}";
        //Bev.Util.createStyle(css);
        ul = $("<ul class=\"sm_menu\"></ul>");
        for (var i = 0; i < tree.length; i++) {
            para = tree[i];
            li = $("<li class=\"sm_menu_li\"><a href=\"#\"></a></li>")
                .appendTo(ul);
            li_icon = $("<span class=\"icon16_16\"></span>") //class=\"icon16_16\" style=\"background: url(" + para.icon + ")\"
                .appendTo(li.find("a"));
            if(para.icon_class){
                li_icon.addClass("sm-ui-icon " + para.icon_class);
            }
            if(para.icon){
                li_icon.css({
                    "background":"url(" + para.icon + ")"
                });
            }
            li_text = $("<span class=\"menu_txt\">" + para.text + "</span>")
                .appendTo(li.find("a"));
            itemArr.push({
                "li":li,
                "li_icon":li_icon
            })
            li.children("a").css({"border":"0px solid #fff"});
        }
        this.itemArray = itemArr;

        this.loadFiles(function(ul){
            return function(){
                ul.menu();
            }
        }(ul));

        for (var i = 0; i < itemArr.length; i++) {
            itm = itemArr[i];

            itm.li.css({
                "margin":0,
                "padding":0,
                "float":"none"
            });
            itm.li.children().css({
                "margin":0,
                "padding":"5px 0px 0px 0px",
                "float":"none",
//                        "color":"#2779AA",
                "cursor":"pointer"
            });
        }

        return ul;
    },

    /**
     * APIMethod: addItem
     * 往menu对象中添加条目。
     *
     * Parameters:
     * para - {Object} 参数对象
     * Examples:
     * (code)
     *  var param = {
     *      "icon_class":"draw-icon",
     *      "text":"绘&nbsp;&nbsp;&nbsp;&nbsp;制",
     *      "events":{//点击菜单中的一栏，创建一个绘制功能面板
     *      "click":function () {
     *          if (!myDrawFeature) {
     *              var dialog = new Bev.Dialog(null, {
     *                  "icon":Bev.Util.getImgPath("icon_circle_16_16.png"),
     *                  "icon_offsetx":152,
     *                  "icon_offsety":5,
     *                  "text":"绘&nbsp;&nbsp;&nbsp;&nbsp;制"
     *              });
     *              var contentBody = dialog.getContentBody();
     *              myDrawFeature = new Bev.DrawFeature({
     *                  "body":contentBody,
     *                  "map":map
     *              });
     *              dialog.on("dialogclose", function () {
     *                  if (myDrawFeature) {
     *                      myDrawFeature.destroy();
     *                      myDrawFeature = null;
     *                  }
     *              });
     *              myWidgetControl.addWidget(dialog);
     *          }
     *     }
     * };
     * myMenu.addItem(param);
     *
     * (end)
     */
    addItem:function(para){
        var ul = $(this.menuBody),li,li_icon,li_text;
        //ul.css("margin","6px");
        this.tree.push(para);
        this.removeEvents();
        li = $("<li class=\"sm_menu_li\"><a href=\"#\"></a></li>")
            .appendTo(ul);
        li_icon = $("<span class=\"icon16_16\"></span>") //class=\"icon16_16\" style=\"background: url(" + para.icon + ")\"
            .appendTo(li.find("a"));
        if(para.icon_class){
            li_icon.addClass("glyphicon");
            li_icon.addClass("sm-ui-icon " + para.icon_class);
        }
        if(para.icon){
            li_icon.css({
                "background":"url(" + para.icon + ")"
            });
        }
        li_text = $("<span class=\"menu_txt\">" + para.text + "</span>")
            .appendTo(li.find("a"));
        this.itemArray.push({
            "li":li,
            "li_icon":li_icon
        })
        li.children("a").css({"border":"0px solid #fff"});

        this.loadFiles(function(ul){
            return function(){
                ul.menu("refresh");
            }
        }(ul));

        li.css({
            "margin":0,
            "padding":0,
            "float":"none"
        });
        li.children().css({
            "margin":0,
            "padding":"5px 0px 0px 0px",
            "float":"none",
//                        "color":"#2779AA",
            "cursor":"pointer"
        });

        var events = {
            click:[],
            mouseover:[],
            mouseout:[]
        };
        var paraEvent = para.events;
        if(paraEvent){
            if(paraEvent.click){
                events.click.push(paraEvent.click);
            }
            if(paraEvent.mouseover){
                events.mouseover.push(paraEvent.mouseover);
            }
            if(paraEvent.mouseout){
                events.mouseout.push(paraEvent.mouseout);
            }
        }
        this._events.push(events);
        this.bindEvents();
    },
    /**
     * APIMethod: addEventToItem
     * 绑定事件到item上。
     *
     * Parameters:
     * index - {<Number>} item在menu上的index
     * eventName - {<String>} 事件名称
     * method - {<Method>} 方法
     */
    addEventToItem:function (index,eventName,method){
        if(index<this._events.length){
            if(this._events[index][eventName]){
                this._events[index][eventName].push(method);
            }
        }
    },
    /**
     * APIMethod: removeEventFromItem
     * 从item上删除事件。
     *
     * Parameters:
     * index - {<Number>} item在menu上的index
     * eventName - {<String>} 事件名称
     * method - {<Method>} 方法
     */
    removeEventFromItem:function (index,eventName,method){
        if(index<this._events.length){
            var events = this._events[index][eventName];
            if(events){
                for(var i=0;i<events.length;i++){
                    if(events[i]===method){
                        events.splice(i,1);
                    }
                }
            }
        }
    },
    /**
     * Method: bindEvents
     * 绑定事件。
     */
    bindEvents:function () {
        var itmArr = this.itemArray, itm, me=this;

        for (var i = 0; i < itmArr.length; i++) {
            itm = itmArr[i].li;
            itm.click(function (clicks) {
                return function(){
                    for(var i=0;i<clicks.length;i++){
                        try{
                            clicks[i]();
                        }
                        catch(e){
                            console.log(e);
                        }
                    }
                    return false;
                }
            }(me._events[i].click));

            itm.mouseover(function (mouseovers,i) {
                return function () {
                    if(me.tree[i]&&me.tree[i].hover_icon){
                        me.itemArray[i].li_icon.css({
                            "background":"url("+me.tree[i].hover_icon+")"
                        });
                    }
                    for(var j=0;j<mouseovers.length;j++){
                        try{
                            mouseovers[j]();
                        }
                        catch(e){
                            console.log(e);
                        }
                    }
                }
            }(me._events[i].mouseover,i));

            itm.mouseout(function (mouseouts,i) {
                return function () {
                    if(me.tree[i]&&me.tree[i].hover_icon){
                        me.itemArray[i].li_icon.css({
                            "background":"url("+me.tree[i].icon+")"
                        });
                    }

                    for(var j=0;j<mouseouts.length;j++){
                        try{
                            mouseouts[j]();
                        }
                        catch(e){
                            console.log(e);
                        }
                    }
                }
            }(me._events[i].mouseout,i));
        }
    },
//            bindEvents:function () {
//                var itmArr = this.itemArray, itm, me=this,trrArr = this.tree, eventArr;
//
//                for (var i = 0; i < itmArr.length; i++) {
//                    itm = itmArr[i].li;
//                    eventArr = trrArr[i].events;
//                    if (eventArr.click) {
//                        itm.click(function (ck) {
//                            return function () {
//                                ck();
//                                return false;
//                            }
//                        }(eventArr.click));
//                    }
//                    if (eventArr.mouseover||trrArr[i].hover_icon) {
//                        itm.mouseover(function (mo,i) {
//                            return function () {
//                                if(mo)mo();
//                                if(me.tree[i]&&me.tree[i].hover_icon){
//                                     me.itemArray[i].li_icon.css({
//                                         "background":"url("+me.tree[i].hover_icon+")"
//                                     });
//                                }
//                            }
//                        }(eventArr.mouseover,i))
//                    }
//                    if (eventArr.mouseout||trrArr[i].hover_icon) {
//                        itm.mouseout(function (mo,i) {
//                            return function () {
//                                if(mo)mo();
//                                if(me.tree[i]&&me.tree[i].hover_icon){
//                                    me.itemArray[i].li_icon.css({
//                                        "background":"url("+me.tree[i].icon+")"
//                                    });
//                                }
//                            }
//                        }(eventArr.mouseout,i))
//                    }
//                }
//            },
    /**
     * Method: removeEvents
     * 绑定事件。
     */
    removeEvents:function () {
        var itmArr = this.itemArray, itm;

        for (var i = 0; i < itmArr.length; i++) {
            itm = itmArr[i].li;
            itm.unbind();
        }
    },

    /**
     * Method: getItems
     * 获取菜单中的栏目组成的数组。
     *
     * Returns:
     * {Array<HTMLElement>}  返回 Dom 对象数组。
     */
    getItems:function () {
        var itms = [], itmArr = this.itemArray;

        for (var i = 0; i < itmArr.length; i++) {
            itms.push(itmArr[i].li);
        }

        return itms;
    },

    /**
     * Method: loadFiles
     * 加载依赖的脚本文件。
     *
     * Parameters:
     * cb - {Method} 加载完成后触发该方法
     */
    loadFiles:function (cb) {
        var t = this;
        if(!this.isFilesLoaded){
            Bev.loader.js([
                "js/ui/jquery.ui.core.js",
                "js/ui/jquery.ui.widget.js",
                "js/ui/jquery.ui.position.js",
                "js/ui/jquery.ui.menu.js"
            ],function(){
                t.isFilesLoaded = true;
                if(cb)cb();
            });
        }
        else{
            if(cb)cb();
        }
    },

    CLASS_NAME: "Bev.Menu"
});
