/**
 * Class: Bev.Accordion
 * 手风琴控件。
 */
Bev.Accordion = Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * APIProperty: html
     * {Array} html内容
     *
     * (code)
     * html:[
     *    {
	 *        "title":"name1",
	 *        "body":$("<p>this is a simple text;this is a simple text;this is a simple text;this is a simple text;this is a simple text;</p>")
	 *    },
     *    {
	 *        "title":"name2",
	 *        "body":$("<p>this is a simple text;this is a simple text;this is a simple text;this is a simple text;this is a simple text;</p>")
	 *    }
     * ]
     * (end)
     */
    html:null,

    /**
     * APIProperty: isRoundedCorner
     * {Boolean} 是否是圆角，默认为true
     */
    isRoundedCorner:true,

    /**
     * Property: isFilesLoaded
     * {boolean} 依赖的文件是否加载完成
     *
     * */
    isFilesLoaded:false,

    /**
     * Constructor: Bev.Accordion
     * 实例化 Accordion 类。
     *
     * Parameters:
     * body - {HTMLElement} 父容器
     * config - {Array} 初始化参数
     *
     * Examples:
     * (code)
     * var myAccordion = new Bev.Accordion({
	 *     "body":$("#divid"),
	 *     "html":[
	 *        {
	 *            "title":"查询",
	 *            "body":$("<p>this is a examples</p><br><p>this is a examples</p><br><p>this is a examples</p>")
	 *        }
	 *    ]
	 * });
     * (end)
     */
    initialize: function(options) {
        for(var key in options){
            this[key] = options[key];
        }
        this.create();
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function(){
        var c,c1, b,t=this;
        c = this.html;
        b = $(this.body);
        if(c){
            b.empty();
            for(var i=0;i< c.length;i++){
                c1 = c[i];
                this.addDoms(c1.title,$(c1.body),b);
            }
        }

        this.loadFiles(function(){
            $(t.body).accordion();
            t.setStyle();
        });
       // b.accordion();
        //this.setStyle();
    },

    /**
     * Method:setStyle
     * 设置手风琴的样式
     *
     */
    setStyle:function(){
        var t = this;
        window.setTimeout(function(){
            var b = $(t.body);
            b.find("h3").next().css("height","auto");
            if(!t.isRoundedCorner){
                var css = {
                    "-moz-border-radius-topleft":"0px",
                    "-webkit-border-top-left-radius":"0px",
                    "-khtml-border-top-left-radius":"0px",
                    "border-top-left-radius":"0px",

                    "-moz-border-radius-topright":"0px",
                    "-webkit-border-top-right-radius":"0px",
                    "-khtml-border-top-right-radius":"0px",
                    "border-top-right-radius":"0px",

                    "-moz-border-radius-bottomleft":"0px",
                    "-webkit-border-bottom-left-radius":"0px",
                    "-khtml-border-bottom-left-radius":"0px",
                    "border-bottom-left-radius":"0px",

                    "-moz-border-radius-bottomright":"0px",
                    "-webkit-border-bottom-right-radius":"0px",
                    "-khtml-border-bottom-right-radius":"0px",
                    "border-bottom-right-radius":"0px"
                };
                b.find("div").css(css);
                b.find("h3").css(css);
            }
        },20);
    },

    /**
     * APIMethod: addItem
     * 往手风琴控件添加项。
     *
     * Parameters:
     * keyValue - {Object} 所要添加的项
     *
     * Examples:
     * (code)
     * var keyValue = {
    *     "title":"量算"，
    *     "body":$("<button>量算</button>")
    * };
     * myAccordion.addItem(keyValue);
     * (end)
     */
    addItem:function(keyValue){
        var t = this;

        var b = $(this.body);
        if(this.isFilesLoaded){
            b.accordion("destroy");
        }

        this.addDoms(keyValue.title,$(keyValue.body),b);

        this.loadFiles(function(){
            $(t.body).accordion();
            t.setStyle();
        })
        //b.accordion();//"refresh"
        //this.setStyle();
    },

    /**
     * Method:addDoms
     * 添加dom到传进来的div里面
     *
     */
    addDoms:function(title,body,div){
        $("<h3>")
            .html(title)
            .appendTo(div);

        $("<div>")
            .append(body)
            .appendTo(div);
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
                "js/ui/jquery.ui.accordion.js"
            ],function(){
                t.isFilesLoaded = true;
                if(cb)cb();
            });
        }
        else{
            if(cb)cb();
        }
    },

    CLASS_NAME: "Bev.Accordion"
});

