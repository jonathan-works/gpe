(function(K) {
  if (K.BooleanOper !== undefined){
    console.log("BooleanOper already loaded");
    return;
  }
  /* private static variables */
  var V = {
    get AND(){return "And";},
    get OR(){return "Or";},
    get NOT(){return "Not";},
    get EQ(){return "Equal";},
    get NEQ(){return "NotEqual";},
    get GT(){return "GreaterThan";},
    get GTE(){return "GreaterThanEqual";},
    get LT(){return "LessThan";},
    get LTE(){return "LessThanEqual";},
    get NAME(){return "BooleanOper";}
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
      return K.getMessage([V.NAME,args.label].join("."));
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
      return pvt.ordinal;
    }
    
    function toSource() {
      return _this.label;
    }
    
    /* privileged public methods and properties */
    Object.defineProperties(_this,{
      name:{
        get:getName
      },
      ordinal:{
        get:getOrdinal
      },
      label:{
        get:getLabel
      },
      valueOf:{
        get:function(){return valueOf;}
      },
      toString:{
        get:function(){return toString;}
      },
      toSource:{
        get:function(){return toSource;}
      }
    });
    values.push(_this);
    values[args.name]=_this;
  }
  
  BooleanOper.prototype = new K.Enum();
  
  /* private static methods */
  function getValues() {
    return values.slice(0,values.length);
  }
  
  function getValueOf(str) {
    var _ = this;
    var result = values[str];
    if (typeof result === K._.UNDEF) {
      throw ["BooleanOper.getValueOf", str, K._.UNDEF].join(" ");
    }
    return result;
  }
  
  function isBooleanOper(str) {
    if (str instanceof BooleanOper) {
      return true;
    }
    return typeof values[str] !== K._.UNDEF;
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