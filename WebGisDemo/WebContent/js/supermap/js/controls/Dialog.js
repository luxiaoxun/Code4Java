/**
 * Class: Bev.Dialog
 * 窗口面板。
 */
Bev.Dialog = Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * APIProperty: content_body
     * {HTMLElement} 放置内容的容器
     */
    content_body:null,

    /**
     * APIProperty: isHide
     * {Boolean} 是否隐藏
     */
    isHide:false,

    /**
     * Property: content
     * {HTMLElement} 所展现的内容元素
     */
    content:null,

    /**
     * APIProperty: head
     * {Object} 窗口头部符号和文字标题
     * (code)
     * var head = {
     *    "icon":"className",
     *    "text":"标题"
     * }
     * (end)
     */
    head:{
        "icon":"",
        "text":"test"
    },

    /**
     * Property: isFilesLoaded
     * {boolean} 依赖的文件是否加载完成
     *
     * */
    isFilesLoaded:false,

    isDialog:false,

    autoOpen:true,

    height:undefined,

    width:undefined,

    modal:false,

    buttons:null,

    close:null,

    title:null,

    /**
     * Constructor: Bev.Dialog
     * 实例化 Dialog 类。
     *
     * Parameters:
     * content - {HTMLElement} 需要展现的内容
     * head - {Object} 标题参数。
     * isHide - {Boolean} 是否隐藏
     *
     * Examples:
     * (code)
     * var dialog = new Bev.Dialog(null,{
     *    "icon":"measure_16_16",
     *    "text":"量&nbsp;&nbsp;&nbsp;&nbsp;算"
     * });
     * (end)
     */
    initialize: function(content, head,isHide,params) {
        this.content = content;
        this.head = head;
        this.isHide = !!isHide;
        if(params){
            this.autoOpen=params["autoOpen"]||true;
            this.height=params["height"]||undefined;
            this.width=params["width"]||undefined;
            this.modal=params["modal"]||false;
            this.buttons=params["buttons"]||null;
            this.close=params["close"]||null;
            this.title=params["title"]||head.text||null;
        }
        this.create();
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function () {
        var body, content,t = this;

        this.body = body = $("<div title=\""+t.head.text+"\" class=\"dialog\"></div>")
            .css("display","none")
            .appendTo($("body"));
        this.content_body = content = $("<div class=\"jsBev_sample\"></div>");
        content.appendTo(body);
        if (this.content)content.append(this.content);

        this.loadFiles(function(){
            var body = t.body;
            t.body.css("display","block");
            if(!t.isDialog){
                body.dialog({
                    title: t.title,
                    autoOpen: t.autoOpen,
                    height: t.height,
                    width: t.width,
                    modal: t.modal,
                    buttons: t.buttons,
                    close: t.close
                });
                if(t.isHide)body.dialog("hide");
                t.isDialog = true;
            }
        })
    },

    /**
     * APIMethod: getContentBody
     * 获取放置内容的容器。
     *
     * Returns:
     * {HTMLElement}  返回 Dom 对象。
     */
    getContentBody:function () {
        return this.content_body;
    },


    /**
     * APIMethod: show
     * 显示dialog
     */
    show:function () {
        var t = this;
        this.loadFiles(function(){
            if(t.isDialog&&t.isHide){
                t.body.dialog("show");
                t.isHide = false;
            }
        })
    },

    /**
     * APIMethod: hide
     * 隐藏dialog
     */
    hide:function () {
        var t = this;
        this.loadFiles(function(){
            if(t.isDialog&&!t.isHide){
                t.body.dialog("hide");
                t.isHide = true;
            }
        })
    },

    /**
     * APIMethod: destroy
     * 销毁dialog
     */
    destroy:function(){
        this.body.dialog("destroy");
        this.body.remove();
        for(var key in this){
             this[key] = null;
        }
    },

    /**
     * APIMethod: on
     * 给dialog绑定事件。
     *
     * Parameters:
     * event - {String}事件类型
     * (code)
     * //目前提供如下事件类型
     * //dialogbeforeclose
     * //dialogclose
     * //dialogopen
     * (end)
     * fun - {Function} 方法
     *
     * Returns:
     * {HTMLElement}  返回 Dom 对象。
     */
    on:function (event, fun) {
        this.body.on(event, fun);
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
                "js/ui/jquery.ui.mouse.js",
                "js/ui/jquery.ui.draggable.js",
                "js/ui/jquery.ui.bevbutton.js",
                "js/ui/jquery.ui.button.js",
                "js/ui/jquery.ui.dialog.js"
            ],function(){
                t.isFilesLoaded = true;
                if(cb)cb();
            });
        }
        else{
            if(cb)cb();
        }
    },

    CLASS_NAME: "Bev.Dialog"
});
