/**
 * Class: Bev.Loading
 * 加载动画。
 */
Bev.Loading = Bev.Class({
    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * Constructor: Bev.Loading
     * 实例化 Loading 类。
     *
     * Parameters:
     * body - {HTMLElement} 父容器
     */
    initialize: function(option) {
        for(var key in option){
            this[key] = option[key];
            if(key=="body")
            {
                this[key]=$(option[key]);
            }
        }
        this.create();
    },

    /**
     * Method: create
     * 创建该控件的dom对象。
     */
    create:function(){
        var b = $(this.body),d;

        if(b){
            d = $("<div>")
                .appendTo(b)
                .append(
                    $("<img>")
                        .attr({
                            "src":"../img/loading1.gif"
                        })
                        .css({
                            "width":"64px",
                            "height":"64px"
                        })
                );

            d = $("<div>")
                .html("加载中...")
                .css({
                    "font-size":"14px"
                })
                .appendTo(b);
        }
    },
    /**
     * APIMethod: hide
     * 隐藏loading。
     */
    hide:function(){
        if(this.body)this.body.css({
            "display":"none"
        });
    },
    /**
     * APIMethod: show
     * 显示loading。
     */
    show:function(){
        if(this.body)this.body.css({
            "display":"block"
        });
    },

    CLASS_NAME: "Bev.Loading"
});
