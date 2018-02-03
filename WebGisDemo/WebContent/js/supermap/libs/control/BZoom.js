/* COPYRIGHT 2012 SUPERMAP
 * 本程序只能在有效的授权许可下使用。
 * 未经许可，不得以任何手段擅自使用或传播。*/

/**
 * @requires SuperMap/Control.js
 */

/**
 * Class: SuperMap.Control.BZoom
 * 缩放类。
 * 用于缩放地图。默认情况下垂直显示在地图左上角。
 *
 * Inherits from:
 *  - <SuperMap.Control>
 */
SuperMap.Control.BZoom = SuperMap.Class(SuperMap.Control, {

    /**
     * Property: zoomInId
     * {String}
     * 加号按钮的dom id
     */
    zoomInId: "smZoomInLink",

    /**
     * Property: zoomOutId
     * {String}
     * 减号按钮的dom id
     */
    zoomOutId: "smZoomOutLink",
    
    /**
     * Property: body
     * {DOMElement}
     * 
     */
    body:null,

    /**
     * Method: draw，创建缩放控件
     *
     * Returns:
     * {DOMElement} A reference to the DOMElement containing the zoom links.
     */
    draw: function() {
        var div = SuperMap.Control.prototype.draw.apply(this,arguments),
            links = this.getOrCreateLinks(div),
            zoomIn = links.zoomIn,
            zoomOut = links.zoomOut,
            eventsInstance = this.map.events;
        
//        if (zoomOut.parentNode !== div) {
//            eventsInstance = this.events;
//            eventsInstance.attachToElement(zoomOut.parentNode);
//        }
//        eventsInstance.register("buttonclick", this, this.onZoomClick);
        var handler = function(me){
            return function(evt){
                me.buttonClick(evt);
            }
        }(this);
        SuperMap.Event.observe(zoomOut.parentNode, "mousedown", SuperMap.Function.bindAsEventListener(handler, zoomOut.parentNode));
        SuperMap.Event.observe(zoomOut.parentNode, "touchstart", SuperMap.Function.bindAsEventListener(handler, zoomOut.parentNode));
        
        this.zoomInLink = zoomIn;
        this.zoomOutLink = zoomOut;
        return div;
    },

    
    /**
     * Method: getOrCreateLinks 创建加减号按钮
     * 
     * Parameters:
     * el - {DOMElement} 父容器
     *
     * Return: 
     * {Object} Object with zoomIn and zoomOut properties referencing links.
     */
    getOrCreateLinks: function(el) {
    	  var zoomIn = document.getElementById(this.zoomInId),
            zoomOut = document.getElementById(this.zoomOutId),b,s;
        b = this.body;
        if(!b){
            b = document.createElement("div");
            el.appendChild(b);
            s = b.style;
            s.left = "10px";
            s.top = "50px";
            s.position = "absolute";
            this.body = b;
        }
        if (!zoomIn) {
            zoomIn = this.createBtn(b,"zoom-plus-mini.png","smControlZoomIn");
        }
        SuperMap.Element.addClass(zoomIn, "smButton");
        if (!zoomOut) {
            zoomOut = this.createBtn(b,"zoom-minus-mini.png","smControlZoomOut");
        }
        SuperMap.Element.addClass(zoomOut, "smButton");
        return {
            zoomIn: zoomIn, zoomOut: zoomOut
        };
    },
    
    /**
     * Method: createBtn 创建加减号按钮
     * 
     * Parameters:
     * p - {DOMElement} 父容器
     * m - {String} 图片名称
     * c - {String} 样式名称
     *
     * Return: 
     * {DOMElement} 创建好的按钮对象.
     */
    createBtn: function(p,m,c){//container imgName className
        var a,d = document,s;
        
        a = d.createElement("div");
        a.className = c;
        s = a.style;
        s.width = "34px";
        s.height = "30px";
        s.cursor = "pointer";
        p.appendChild(a);
        
        b = d.createElement("img");
        s = b.style;
        s.width = "34px";
        s.height = "30px";
        //控件的样式
        if(SuperMap.Control.SKIN==="BLUE"){
            b.src = SuperMap.Util.getImagesLocation() +"controlSkinBlue/"+ m;
        }
        else{
            b.src = SuperMap.Util.getImagesLocation() +"controlSkinWhite/"+ m;
        }

        a.appendChild(b);
        
        return a;
    },
    
    /**
     * Method: onZoomClick
     * 当点击按钮时调用.
     */
    onZoomClick: function(evt) {
        var button = evt.buttonElement;
        if (button === this.zoomInLink) {
            this.map.zoomIn();
        } else if (button === this.zoomOutLink) {
            this.map.zoomOut();
        }
    },
    
    /**
     * Method: buttonClick
     * 处理鼠标事件.
     */
    buttonClick: function(evt) {
        var element = SuperMap.Event.element(evt);
        if (element && (SuperMap.Event.isLeftClick(evt) || !~evt.type.indexOf("mouse"))) {
            var button = this.getPressedButton(element);
            if (button) {
                var args = {buttonElement: button};
                this.onZoomClick(args);
            }
        }
    },
    
     /**
     * Method: getPressedButton
     * Get the pressed button, if any. Returns undefined if no button
     * was pressed.
     *
     * Arguments:
     * element - {DOMElement} The event target.
     *
     * Returns:
     * {DOMElement} The button element, or undefined.
     */
    getPressedButton: function(element) {
        var depth = 3, // limit the search depth
            button;
        do {
            if(SuperMap.Element.hasClass(element, "smButton")) {
                // hit!
                button = element;
                break;
            }
            element = element.parentNode;
        } while(--depth > 0 && element);
        return button;
    },



    /** 
     * APIMethod: destroy
     * 销毁Zoom控件，释放相关资源。
     */
    destroy: function() {
        SuperMap.Event.stopObservingElement(this.zoomInLink);
        SuperMap.Event.stopObservingElement(this.zoomOutLink);
        delete this.zoomInLink;
        delete this.zoomOutLink;
        delete this.body;
        SuperMap.Control.prototype.destroy.apply(this);
    },


    CLASS_NAME: "SuperMap.Control.BZoom"
});
