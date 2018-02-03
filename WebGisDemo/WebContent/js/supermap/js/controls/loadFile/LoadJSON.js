/*
 * @requires js/Class.js
 * @requires js/controls/loadFile/LoadFile.js
 * */

/**
 * Class: Bev.LoadJSON
 * 加载JSON文件的控件。
 */

Bev.LoadJSON = Bev.Class(Bev.LoadFile,{

    /*
     * APIProperty: labelText
     * {string}  选择器的提示标签
     * */
    labelText:"",

    /*
     * Property: rooPath
     * ｛string} 文档所在的目录
     * */
    rootPath:"",

    /*
     * APIProperty: submitCallback
     * {Function} 加载数据完成回调函数
     * */
    submitCallback:function(){console.log("成功返回数据！")},

    /*
     * APIProperty: addToLayer
     * {Function} 添加数据到地图上
     * */
    addToLayer:null,

    /*
     * APIProperty:：clearLayer
     * {Function} 移除地图上的数据
     * */
    clearLayer:null,

    /*
     * Constructor: Bev.LoadJSON
     * 用于加载服务器的json格式的数据文档到前端显示
     *
     * Parameters:options
     *
     * Example:
     * (code)
     *    var loadJSON=new Bev.LoadJSON();
     * (end)
     * */
    initialize:function(options){
        Bev.LoadFile.prototype.initialize.apply(this, [options]);

        if(options){
            for(var key in options){
                this[key]=options[key];
            }
        }

        this.createLoadJSON();
    },

    createLoadJSON:function(){
        var t=this;

        var location=document.location.href;
        var index=location.lastIndexOf("/");
        var rootPath= t.rootPaht=location.slice(0,index);
        $.ajax({ url: rootPath+"/getdata?data=json", success: function(data,isSucess,result){
            var dataList= eval(result.responseText);
            if(dataList)
            {
                t.setLabel(t.labelText);
                t.setSubmit({
                    iconClass:"glyphicon-json",
                    text:"加载json文件",
                    onSubmit: function(){
                        if(!t.addToLayer)return;
                        $.ajax({
                            url:rootPath+"/getdata?data="+ $(t.select).val(),
                            success: function(data,isSucess,result){
                                t.submitCallback();
                                var responseText=null;
                                if(result.hasOwnProperty("responseText")){
                                    responseText=result.responseText;
                                    //var jsonData=eval(responseText);
                                    var json=new SuperMap.Format.JSON();
                                    jsonData=json.read(responseText);
                                    t.loadJsonData(jsonData);
                                }
                            }
                        });
                    }
                });
                t.setSelect(dataList);
                t.setClear({
                    iconClass:"glyphicon-trash",
                    text:"清除",
                    onClear:function(){
                        t.clearJsonData();
                    }
                });
                t.enableSubmit();
            }
        }
        });

    },

    loadJsonData:function(jsonObject){
        this.addToLayer&&(typeof(this.addToLayer)==="function")&&this.addToLayer(jsonObject);
    },

    clearJsonData:function(){
        this.clearLayer&&(typeof(this.clearLayer)==="function")&&this.clearLayer();
    },

    bindSubmitCallback:function(submitCallback){
        if(submitCallback&&typeof(submitCallback)==="function"){
            this.submitCallback=submitCallback;
        }
    },

    /*
     * APIMethod: destroy
     * Bev.LoadJSON对象清空方法
     * */
    destroy:function(){
        Bev.LoadFile.prototype.destroy.apply(this);
        this.labelText=null;
        this.rootPath=null;
        this.submitCallback=null;
        this.clearJsonData();
    },

    CLASS_NAME:"Bev.LoadJSON"
});