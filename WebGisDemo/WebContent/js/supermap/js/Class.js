/**
 * Constructor: Bev.Class
 * 基类。 
 * 
 * 创建一个新的Bev类，如下所示:
 * (code)
 *     var MyClass = new Bev.Class(prototype);
 * (end)
 *
 * 创建一个新的有多个继承类的SuperMap类，如下所示:
 * (code)
 *     var MyClass = new Bev.Class(Class1, Class2, prototype);
 * (end)
 */
Bev.Class = function() {
    var len = arguments.length;
    var P = arguments[0];
    var F = arguments[len-1];

    var C = typeof F.initialize == "function" ?  F.initialize : function(){ P.prototype.initialize.apply(this, arguments); };

    if (len > 1) {
        var newArgs = [C, P].concat( Array.prototype.slice.call(arguments).slice(1, len-1), F);
        Bev.inherit.apply(null, newArgs);
    } else {
        C.prototype = F;
    }
    return C;
};

/**
 * Function: Bev.inherit
 *
 * Parameters:
 * C - {Object} the class that inherits
 * P - {Object} the superclass to inherit from
 *
 * In addition to the mandatory C and P parameters, an arbitrary number of
 * objects can be passed, which will extend C.
 */
Bev.inherit = function(C, P) {
   var F = function() {};
   F.prototype = P.prototype;
   C.prototype = new F;
   var i, l, o;
   for(i=2, l=arguments.length; i<l; i++) {
       o = arguments[i];
       if(typeof o === "function") {
           o = o.prototype;
       }
       Bev.Util.extend(C.prototype, o);
   }
};

Bev.Util = Bev.Util || {};

/**
 * APIFunction: extend
 * 复制源对象的所有属性到目标对象上，源对象上的没有定义的属性在目标对象上也不会被设置。
 *
 * 要复制Bev.Size对象的所有属性到自定义对象上，使用方法如下:
 *  (code)
 *     var size = new Bev.Size(100, 100);
 *     var obj = {}；
 *     Bev.Util.extend(obj, size);
 * (end)
 *
 * Parameters:
 * destination - {Object} 目标对象。
 * source - {Object} 源对象，其属性将被设置到目标对象上。
 * Returns:
 * {Object} 目标对象。
 */
Bev.Util.extend = function(destination, source) {
    destination = destination || {};
    if (source) {
        for (var property in source) {
            var value = source[property];
            if (value !== undefined) {
                destination[property] = value;
            }
        }

        /**
         * IE doesn't include the toString property when iterating over an object's
         * properties with the for(property in object) syntax.  Explicitly check if
         * the source has its own toString property.
         */

        /*
         * FF/Windows < 2.0.0.13 reports "Illegal operation on WrappedNative
         * prototype object" when calling hawOwnProperty if the source object
         * is an instance of window.Event.
         */

        var sourceIsEvt = typeof window.Event == "function"
                          && source instanceof window.Event;

        if (!sourceIsEvt
           && source.hasOwnProperty && source.hasOwnProperty("toString")) {
            destination.toString = source.toString;
        }
    }
    return destination;
};
Bev.Util.copy = function(des, soc) {
    des = des || {};
    var v;
    if(soc) {
        for(var p in des) {
            v = soc[p];
            if(typeof v !== 'undefined') {
                des[p] = v;
            }
        }
    }
};
Bev.Util.reset = function(obj) {
    obj = obj || {};
    for(var p in obj) {
        if(obj.hasOwnProperty(p)) {
            if(typeof obj[p] === "object" && obj[p] instanceof Array) {
                for(var i in obj[p]) {
                    if(obj[p][i].destroy) {
                        obj[p][i].destroy();
                    }
                }
                obj[p].length = 0;
            } else if(typeof obj[p] === "object" && obj[p] instanceof Object) {
                if(obj[p].destroy) {
                    obj[p].destroy();
                }
            }
            obj[p] = null;
        }
    }
};
