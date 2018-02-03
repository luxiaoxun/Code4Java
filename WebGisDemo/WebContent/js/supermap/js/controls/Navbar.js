/**
 * Class: Bev.Navbar
 * 导航条控件。
 */

Bev.Navbar=Bev.Class({

    /**
     * APIProperty: body
     * {HTMLElement} 父容器
     */
    body:null,

    /**
     * APIProperty: navBody
     * {HTMLElement}导航条dom对象
     */
    navBody:null,

    /**
     * APIProperty: itemBody
     * {Array}子菜单的容器
     */
    itemBody:[],

    /**
     * APIProperty：navItems
     * 导航菜单的每一项
     *
     * (code)
     * var navItems=[
     *    {"icon_class":"tool-icon-20","title":"基本操作","events":{"mouseclick":function(){},"mouseover":function(){},"mouseout":function(){}}},
     *    {"icon_class":"tool-icon-20","title":"其他操作","events":{"mouseclick":function(){},"mouseover":function(){},"mouseout":function(){}}}
     * ]
     * (end)
     */
    navItems:[],

    /**
     * Property: isFilesLoaded
     * {boolean} 依赖的文件是否加载完成
     *
     * */
    isFilesLoaded:false,

    /**
     * Constructor: Bev.Navbar
     * 实例化Navbar类
     *
     *  Parameters:
     *  options - {Object}参数
     *
     *  Examples:
     * (code)
     *  myNavbar = new Bev.Navbar({
     *      "body":$("#toolbar")
     *  });
     * (end)
     */
    initialize:function(options){
        for(var key in options)
        {
            this[key]=options[key];
            if(key=="body")
            {
                this[key]=$(options[key]);
            }
        }

        var nav = this.createNav();
        nav&&nav.appendTo($(this.body));
        /*for(var i=0;i<this.itemBody.length;i++)
        {
            $(this.itemBody[i]).button();
        }*/
        //$(window).resize();
    },

    /**
     * Method:createNav
     * 创建一个导航条
     *
     */
    createNav:function(){

        var nav=this.navBody=$("<ul class='nav_ul'></ul>");
        for(var i=0;i<this.navItems.length;i++)
        {
            this.addNavItem(this.navItems[i]);
        }

        return nav;
    },

    /**
     * APIMethod: addNavItem
     * 往导航条添加项
     *
     * Parameters:
     * item - {object} 对象
     *
     * Examples:
     * (code)
     * myNavbar.addNavItem({
     *      "icon_class":"tool-icon-20",
     *      "title":"基本操作",
     *      "events":{
     *          "mouseclick":function(){},
     *          "mouseover":function(){},
     *          "mouseout":function(){}
     *       }
     *  });
     * (end)
     */
    addNavItem:function(item){
        var navItem_li=$("<li class='navItem_li'></li>");
        this.itemBody.push(navItem_li);
        var navItem_a=$("<a class='navItem_a'></a>");
        this.setDefaultStyle(navItem_li,navItem_a);

        var icon_class=item&&item.icon_class;
        var title=item&&item.title;

        var index=this.getIndexByItem(item);
        if(-1==index)
        {
            this.navItems.push(item);
            index=this.getIndexByItem(item);
        }
        navItem_li.attr("class",icon_class);
        navItem_a.html(title);
        navItem_a.appendTo($(this.itemBody[index]));
        navItem_li.appendTo($(this.navBody));

        this.loadFiles(function(navItem_li){
            return function(){
                navItem_li.button();
            }
        }(navItem_li));

        if(this.navItems[index].events)
        {
            this.bindEvent(index,this.navItems[index].events);
        }
        for(var i=0;i<this.navItems.length;i++)
        {
            this.setNavItemStyle(i,{"border-radius":"0px","margin-left":"3px"})
            var css={};
            if(i==(this.navItems.length-1))
            {
                css={
                    "border-bottom-right-radius":"10px",
                    "border-top-right-radius":"10px"
                };
                this.setNavItemStyle(i,css);
            }
            if(i==0)
            {
                css={
                    "border-bottom-left-radius":"10px",
                    "border-top-left-radius":"10px"
                };
                this.setNavItemStyle(i,css);
            }

        }
    },

    /**
     * Method:setDefaultStyle
     * 设置每个导航条项的style属性
     * */
    setDefaultStyle:function(li,a){
        li.css({"height":"35px","float":"left"});

        a.css({"position":"relative","top":"5px"}) ;
    },

    /**
     * Method:getIndexByItem
     * 通过项来获取其索引值，找不到则返回-1
     * */
    getIndexByItem:function(item){
        for(var i=0;i<this.navItems.length;i++)
        {
            if(this.navItems[i]==item)
            {
                return i;
            }
        }
        return -1;
    } ,

    /**
     * Method:getItemByIndex
     * 通过索引查找项
     * */
    getItemByIndex:function(index){
        return index<this.navItems.length&&this.navItems[index];
    },

    /**
     * APIMethod: bindEvent
     * 给导航条绑定事件
     *
     * Parameters:
     * index - {Number} 要绑定事件的项的索引
     * events - {Object} 事件名及对应的方法
     *
     * Examples:
     * (code)
     * myNav.bindEvent(0,{
     *      "mouseover":function(){
     *          var position=$(this).position();
     *          myMenu.menuBody.css({"display":"block","position":"absolute","top":position.top+30}) ;
     *          myMenu.menuBody.mouseover(function(){
     *              myMenu.menuBody.css({"display":"block","position":"absolute","top":position.top+30}) ;
     *          });
     *      },
     *      "mouseout":function(){
     *          myMenu.menuBody.css({"display":"none"});
     *          myMenu.menuBody.mouseout(function(){
     *              myMenu.menuBody.css({"display":"none"});
     *          });
     *      }
     *  });
     * (end)
     */
    bindEvent:function(index,events)
    {
        if(!this.itemBody[index])return;
        for(var evt in events)
        {
            switch(evt)
            {
                case "mouseclick":
                    $(this.itemBody[index]).click(events[evt]);
                    break;
                case "mouseover":
                    $(this.itemBody[index]).mouseover(events[evt]);
                    break;
                case "mouseout":
                    $(this.itemBody[index]).mouseout(events[evt]);
                    break;
                default:
                    break;
            }
        }
    } ,

    /**
     * APIMethod: setNavItemStyle
     * 设置导航条某一项的样式
     *
     * Parameters:
     * index - {Number} 索引值
     * style - {Object} css样式
     *
     * Examples:
     * (code)
     * var style={
     *     "width":"200px",
     *     "height":"50px"
     * };
     * myNavbar.setNavItemStyle(0,style);
    * (end)
    */
    setNavItemStyle:function(index,style)
    {
        for(var attr in style)
        {
            if(this.itemBody[index])
            {
                $(this.itemBody[index]).css(attr,style[attr]);
            }
        }
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
                "js/ui/jquery.ui.button.js"
            ],function(){
                t.isFilesLoaded = true;
                if(cb)cb();
            });
        }
        else{
            if(cb)cb();
        }
    },

    CLASS_NAME: "Bev.Navbar"
});