/**
 * Class: Bev.Tooltip
 * 提示信息窗口。
 */

Bev.Tooltip=Bev.Class({
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
     *    "position":["center","top"],//["横向位置(left,center,right)""纵向位置"(top,center,bottom)]
     *    "offset":{"x":0,"y":0}  //偏移量
     * }
     * (end)
     */
    config:{
        "position":["center","top"],
        "offset":{"x":0,"y":0}
    },

    /**
     * APIProperty: tooltip
     * {HTMLElement} tooltip Dom对象
     */
    tooltip:null,

    /**
     * Constructor: Bev.Tooltip
     * 实例化 Tooltip 类。
     *
     * Parameters:
     * body - {HTMLElement} 父容器
     * config - {Array} 初始化参数
     *
     * Examples:
     * (code)
     * myTooltip = new Bev.Tooltip($("#bd_right"),{
     *    "position":["center","top"],
     *    "offset":{"x":0,"y":40}
     * });
     * myTooltip.show(txt);
     * myTooltip.close();
     * (end)
     */
    initialize:function (body1,config) {
        var me=this;
        me.body = $(body1);
        if(config)me.config = config;

    },

    /**
     * APIMethod: show
     * 显示提示信息
     *
     * Parameters:
     * txt - {String} 要提示的内容
     */
    show:function(txt){
        this.create(txt);
    },

    /**
     * APIMethod: close
     * 关闭提示信息
     */
    close:function(){
        if(this.tooltip){
            this.tooltip.remove();
            this.tooltip = null;
//                    this.config = {
//                        "position":["center","top"],
//                        "offset":{"x":0,"y":0}
//                    },
//                    this.body = null;
        }
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     *
     * Parameters:
     * txt - {String} 要提示的内容
     */
    create:function(txt){
        var a,b = $(this.body),c = this.config, t,me = this;

        if(c){
            var t = $("<div>")
                .addClass("ui-corner-all")
                .css({
                    "position":"absolute",
                    "z-index":9999,
                    "left":"0px",
                    "top":"0px",
                    "background":"#fff",
                    "border":"1px solid #ddd",
                    "padding":"5px"
                })

            a = $("<span>")
                .css({
                    "display":"inline-block",
                    "margin-right":"10px"
                })
                .html(txt)
                .appendTo(t);

            Bev.loader.js([
                "js/ui/jquery.ui.core.js",
                "js/ui/jquery.ui.widget.js",
                "js/ui/jquery.ui.button.js",
                "js/ui/jquery.ui.bevbutton.js"
            ],function(t){
                return function(){
                    a = $("<button>")
                        .html("close")
                        .bevbutton({
                            icons: {
                                primary: "glyphicon-remove-circle"
                            },
                            text: false
                        })
                        .appendTo(t)
                        .click(function(){
                            me.close();
                        });
                }
            }(t));



            this.position(t);
            t.appendTo($(this.body));

            this.tooltip = t;
        }
    },
    /**
     * Method: position
     * 定位控件对象。
     *
     * Parameters:
     * dom - {HTMLElement} 该控件的dom对象
     */
    position:function(dom){
        var b=$(this.body),size,c = this.config, p,s={};

        size = Bev.Util.getSize(dom);
        p = c.position;
        if(p[0] == "center"){
            s.left = this.getCenter(b.width(),size.w);
        }
        else if(p[0] == "left"){
            s.left = 0;
        }
        else if(p[0] == "right"){
            s.left = b.width()-size.w;
        }

        if(p[1] == "center"){
            s.top = this.getCenter(b.height(),size.h);
        }
        else if(p[1] == "top"){
            s.top = 0;
        }
        else if(p[1] == "bottom"){
            s.top = b.height()-size.h;
        }
        s.left = (s.left + c.offset.x) + "px";
        s.top = (s.top + c.offset.y) + "px";

        dom.css(s);
    },
    /**
     * Method: getCenter
     * 计算中心点。
     */
    getCenter:function(fLength,cLength){
        return fLength/2-cLength/2;
    },

    CLASS_NAME:"Bev.Tooltip"
} );
