(function(K) {
  
  /* private static variables */
  var V = {
    AND:"And",
    OR:"Or",
    NOT:"Not",
    EQ:"Equal",
    NEQ:"NotEqual",
    GT:"GreaterThan",
    GTE:"GreaterThanEqual",
    LT:"LessThan",
    LTE:"LessThanEqual",
    NAME:"BooleanOper"
  };
  
  var values = [];
  var it = 1;
  
  var _BooleanOper = {
    AND:new BooleanOper({name:V.AND, label:"and"}),
    OR:new BooleanOper({name:V.OR, label:"or"}),
    NOT:new BooleanOper({name:V.NOT, label:"not"}),
    EQ:new BooleanOper({name:V.EQ, label:"eq"}),
    NEQ:new BooleanOper({name:V.NEQ, label:"neq"}),
    GT:new BooleanOper({name:V.GT, label:"gt"}),
    GTE:new BooleanOper({name:V.GTE, label:"gte"}),
    LT:new BooleanOper({name:V.LT, label:"lt"}),
    LTE:new BooleanOper({name:V.LTE, label:"lte"})
  };
  
  /* Object */
  function BooleanOper(args) {
    var _super = new K.Enum(args.name);
    var _this = this;
    var pvt = {
      ordinal : it++
    };
    
    /* private methods */
    function getLabel() {
      var msg = K.messages || {};
      return (msg = (msg = msg[navigator.language] || {BoolOper:{}}).BoolOper || {})[args.label] || [V.NAME,args.label].join(".");
    }
    
    function getOrdinal() {
      return pvt.ordinal;
    }
    
    function getName() {
      return _super.name;
    }
    
    function valueOf() {
      return pvt.ordinal;
    }
    
    function toString() {
      return _super.name;
    }
    
    function toSource() {
      return _this.label;
    }
    
    /* privileged public methods and properties */
    Object.defineProperties(_this, {
      name:{
        get:getName
      },
      ordinal:{
        get:getOrdinal
      },
      valueOf:{
        get:function () {
          return valueOf;
        }
      },
      label:{
        get:getLabel
      },
      toString:{
        get:function () {
          return toString;
        }
      },
      toSource:{
        get:function() {
          return toSource;
        }
      }
    });
    values.push(_this);
  }
  
  BooleanOper.prototype = new K.Enum();
  
  /* private static methods */
  function getValues() {
    return values.slice(0,values.length);
  }
  
  function getValueOf(str) {
    var _ = this;
    var result;
    if (str === V.AND) {
      result = _.AND;
    } else if (str === V.OR) {
      result = _.OR;
    } else if (str === V.NOT) {
      result = _.NOT;
    } else if (str === V.EQ) {
      result = _.EQ;
    } else if (str === V.NEQ) {
      result = _.NEQ;
    } else if (str === V.GT) {
      result = _.GT;
    } else if (str === V.GTE) {
      result = _.GTE;
    } else if (str === V.LT) {
      result = _.LT;
    } else if (str === V.LTE) {
      result = _.LTE;
    } else {
      throw 0;
    }
    return result;
  }
  
  function isBooleanOper(str) {
    if (str instanceof BooleanOper) {
      return true;
    }
    var result = true;
    try {
      K.BooleanOper.getValueOf(str);
    } catch(e) {
      result = false;
    }
    return result;
  }
  
  /* public static methods */
  
  Object.defineProperties(_BooleanOper,{
    values:{
      get:getValues
    },
    getValueOf:{
      get:function() {
        return getValueOf;
      }
    },
    isBooleanOper:{
      get:function() {
        return isBooleanOper;
      }
    }
  });
  
  Object.defineProperties(K,{
    BooleanOper:{
      get:function() {
        return _BooleanOper;
      }
    }
  });
})(window._parser);