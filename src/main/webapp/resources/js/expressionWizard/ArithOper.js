(function(K) {
  
  /* private static variables */
  var V = {
    get PLUS()"Plus",
    get MINUS()"Minus",
    get MULT()"Mult",
    get DIV()"Div",
    get NEGATIVE()"Negative",
    get NAME()"ArithOper"
  };
  
  var values = [];
  var it = 1;
  var _ArithOper = {
    PLUS:new ArithOper({name:V.PLUS, label:"plus"}),
    MINUS:new ArithOper({name:V.MINUS, label:"minus"}),
    MULT:new ArithOper({name:V.MULT, label:"mult"}),
    DIV:new ArithOper({name:V.DIV, label:"div"}),
    NEGATIVE:new ArithOper({name:V.NEGATIVE, label:"negative"})
  };
  
  /* Object */
  function ArithOper(args) {
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
        get:function(){return valueOf;}
      },
      label:{
        get:getLabel
      },
      toString:{
        get:function(){return toString;}
      },
      toSource:{
        get:function(){return toSource;}
      }
    });
    values.push(_this);
    values[args.name] = _this;
  }
  
  ArithOper.prototype = new K.Enum();
  
  /* private static methods */
  function getValues() {
    return values.slice(0,values.length);
  }
  
  function getValueOf(str) {
    if (typeof values[str] === K._.UNDEF) {
      throw ["ArithOper.getValueOf(",str,")",K._.UNDEF].join(" ");
    }
    return values[str];
  }
  
  function isArithOper(str) {
    if (str instanceof ArithOper) {
      return true;
    }
    return typeof values[str] !== K._.UNDEF;
  }
  
  /* public static methods */
  
  Object.defineProperties(_ArithOper,{
    values:{
      get:getValues
    },
    getValueOf:{
      get:function() {
        return getValueOf;
      }
    },
    isArithOper:{
      get:function() {
        return isArithOper;
      }
    }
  });
  
  Object.defineProperties(K,{
    ArithOper:{
      get:function() {
        return _ArithOper;
      }
    }
  });
})(window._parser);