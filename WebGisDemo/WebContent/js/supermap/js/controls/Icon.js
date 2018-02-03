/**
 * Class: Bev.Icon
 * 图标按钮。
 */
Bev.Icon = Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * APIProperty: config
     * {Object} 初始化所需的参数
     *
     * (code)
     * config:{
     *    "title":"test",
     *    "click":function(){},
     *    "img":"",
     *    "isDisplayTitle":false
     * }
     * (end)
     */
    config:{
        "title":"test",
        "click":function(){},
        "icon_class":"",
        "isDisplayTitle":false
    },

    /**
     * Property: defaultWidth
     * {Number} 默认宽度
     */
    defaultWidth:16,

    /**
     * Property: defaultHeight
     * {Number} 默认高度
     */
    defaultHeight:16,


    /**
     * Constructor: Bev.Icon
     * 实例化 Icon 类。
     *
     * Parameters:
     * body - {HTMLElement} 父容器
     * config - {Array} 初始化参数
     *
     * Examples:
     * (code)
     * var myIcon1 = new Bev.Icon($("#bd_toolbar"),{
     *    "title":"面积量算",
     *    "img":"images/frameimages/measure2.png",
     *    "click":function(){
     *        var m = getMesure();
     *        m.measureArea();
     *    },
     *    "isDisplayTitle":true
     * });
     * (end)
     */
    initialize: function(body1,config) {
        this.body = $(body1);
        if(config)this.config = config;
        this.create();
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function(){
        var b = this.body,c = this.config,c1,c2,c3;
        var t = this;

        if(c){
            c1 = $("<button>")
                .addClass("sm_icon ui-corner-all ui-state-default")
                .css({
                    "height":"auto",
                    "display":"inline-block",
//                            "background":"url("+c.img+")",
                    "margin-right":"10px",
                    "cursor":"pointer",
                    "padding":"3px"
                })
                .click(c.click)
                .mouseover(function(){
                    $(this).addClass("ui-state-hover");
                    t._refreshIcon();
                })
                .mouseout(function(){
                    $(this).removeClass("ui-state-hover");
                    t._refreshIcon();
                })
                .appendTo(this.body);

            this.iconSp = c2 = $("<span>")
                .addClass("sm-ui-icon")
                .css({
                    "height":(this.defaultHeight)+"px",
                    "width":(this.defaultWidth)+"px",
                    "display":"inline-block",
                    "margin-right":"5px"
                })
                .appendTo(c1)
                .addClass("glyphicon")
                .addClass(c.icon_class);

            if(c.isDisplayTitle){
                c3 = $("<span>")
                    .css({
                        "height":(c.height||this.defaultHeight)+"px",
                        "line-height":(c.height||this.defaultHeight)+"px",
                        "display":"inline-block",
                        "white-space": "nowrap",
                        "vertical-align": "top"
                    })
                    .html(c.title)
                    .appendTo(c1);
            }
            else{
                c1.attr("title", c.title);
            }
        }
    },

    _getIsIE8:function(){
        if(!this.isIE8){
            if($.browser){
                if($.browser.msie){
                    if($.browser.version=="8.0"){
                        this.isIE8 = true;
                    }
                }
            }
        }
        return !!this.isIE8;
    },
    _refreshIcon:function(){
        if(this._getIsIE8()){
            var c = this.config;
            if(this.iconSp){
                this.iconSp.removeClass("glyphicon "+c.icon_class);
                this.iconSp.addClass("glyphicon "+c.icon_class);
            }
        }
    },

    CLASS_NAME: "Bev.Icon"
});
