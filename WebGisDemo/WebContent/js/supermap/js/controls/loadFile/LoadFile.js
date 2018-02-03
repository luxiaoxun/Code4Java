/*
* @requeries "js/ui/jquery.ui.bevbutton.js"
* */

/**
 * Class: Bev.LoadFile
 * 加载文件的控件基类。
 */
Bev.LoadFile = Bev.Class({
    label:null,

    select:null,

    submit:null,

    clear:null,

    onSubmit:null,

    onClear:null,


   /*
   * Property:controlBody
   * {jQuery Object} 控件的容器对象
   * */
    controlBody:null,

    /**
     * Constructor: Bev.LoadFile
     * LoadFile的构造函数
     *
     * Example :
     * (code)
     *   var loadFile=new Bev.LoadFile();
     * (end)
     * */
    initialize:function(options){
         if(options){
             for(var key in options){
                 this[key]=options[key];
             }
         }


        this.createLoadFile();
    },

    createLoadFile:function(){
        var controlBody=this.controlBody=$("<div class='controlBody container'></div>");

        var selectDiv=this.selectDiv=$("<div class='selectDiv'></div>");
        var label=this.label=$("<span></span>").appendTo(selectDiv);
        //var select=this.select=$("<select></select>").appendTo(selectDiv);

        var buttonDiv=$("<div class='buttonDiv'></div>");
        var button=this.submit=$("<button disabled='disabled'></button>").appendTo(buttonDiv);
        var clearbt=this.clear=$("<button>清除</button>").appendTo(buttonDiv);

        selectDiv.appendTo(controlBody);
        buttonDiv.appendTo(controlBody);
    },

    /*
     * APIMethod: getControlBody
     * 获取控件的主体
     *
     * Return :
     * {HTMLElement} controlBody
     * */
    getControlBody:function(){
        return this.controlBody&&$(this.controlBody);
    },


    setLabel:function(text){
        if(!text)return;
        $(this.label).html(text);
    },

    setSelect:function(options){
        if(!this.select&&this.selectDiv){
            var select=this.select=$("<select></select>").appendTo(this.selectDiv);
        }

        for(var i=0;i<options.length;i++){
            var option=options[i];
            $(this.select).append($("<option value="+option+">"+option+"</option>"));
        }
    },

    /*
     * APIMethod: setSubmit
     * 设置提交按钮参数
     * Example ：
     * （code)
     * loadFile.setSubmit({
     *     iconClass:"g"
     *     text:"加载json文件",
     *     onSubmit: function(){}
     * });
     * (end)
     * */
    setSubmit:function(option){
        if(!option)return;
        var t=this;
        var iconClass=option.iconClass||"";
        var submitText=option.text||"";
        var onSubmit=this.onSubmit=option.onSubmit||null;

        Bev.loader.js(["js/ui/jquery.ui.bevbutton.js"],function(){
            $(t.submit).html(submitText).bevbutton({
                "icons":{
                    "primary":iconClass
                }
            });
        });
        this.bindSubmitEvent(onSubmit);
    },

    /*
     * APIMethod: setClear
     * 设置清除按钮参数
     * Example ：
     * （code)
     * loadFile.setClear({
     *     iconClass:"g"
     *     text:"清除",
     *     onClear: function(){}
     * });
     * (end)
     * */
    setClear:function(option){
        if(!option)return;
        var t=this;
        var iconClass=option.iconClass||null;
        var clearText=option.text||null;
        var onClear= t.onClear=option.onClear||null;

        Bev.loader.js(["js/ui/jquery.ui.bevbutton.js"],function(){
            $(t.clear).html(clearText).bevbutton({
                "icons":{
                    "primary":iconClass
                }
            });
        });
        this.bindClearEvent(onClear);
    },

    disableSubmit:function(){
        this.submit&&$(this.submit).attr("disabled",true);
    },

    enableSubmit:function(){
        this.submit&&$(this.submit).attr("disabled",false);
    },

    bindSubmitEvent:function(onSubmit){
        if(onSubmit&&typeof(onSubmit)==="function"){
            this.onSubmit=onSubmit;
        }
        if(this.onSubmit){
            $(this.submit).click(onSubmit);
        }
    },

    bindClearEvent:function(onClear){
        if(onClear&&typeof(onClear)==="function"){
            this.onClear=onClear;
        }
        if(this.onClear){
            $(this.clear).click(onClear);
        }
    },

    /*
     * APIMethod: destroy
     * 对象清空方法
     * */
    destroy:function(){
        this.label=null;
        this.select=null;
        this.submit=null;
        this.onSubmit=null;
        this.controlBody=null;
        this.onClear=null;
    },

    CLASS_NAME:"Bev.LoadFile"
});