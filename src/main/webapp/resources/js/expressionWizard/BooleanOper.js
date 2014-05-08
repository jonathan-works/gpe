(function(container) {
  
  function Enum(label, ordinal, name) {
    function getLabel() {
      return label;
    }
    
    function getOrdinal() {
      return ordinal;
    }
    
    function getName() {
      return name;
    }
    
    function valueOf() {
      return ordinal;
    }
    
    if (this !== window) {
      Object.defineProperties(this, {
        label:{
          get:getLabel
        },
        ordinal:{
          get:getOrdinal
        },
        name:{
          get:getName
        },
        valueOf:{
          get:function() {
            return valueOf;
          }
        }
      });
    }
  }
  
  var K = {
    AND:"And",
    OR:"Or",
    NOT:"Not",
    EQ:"Equal",
    NEQ:"NotEqual",
    GT:"GreaterThan",
    GTE:"GreaterThanEqual",
    LT:"LessThan",
    LTE:"LessThanEqual"
  };
  
  var BooleanOper = Object.defineProperties({},{
    AND:{
      get:function() {
        return new Enum("E", 0x1, K.AND);
      }
    },
    OR:{
      get:function() {
        return new Enum("OU", 0x2, K.OR);
      }
    },
    NOT:{
      get:function() {
        return new Enum("NEGAÇÃO", 0x3, K.NOT);
      }
    },
    EQ:{
      get:function() {
        return new Enum("==", 0x4, K.EQ);
      }
    },
    NEQ:{
      get:function() {
        return new Enum("!=", 0x5, K.NEQ);
      }
    },
    GT:{
      get:function() {
        return new Enum(">", 0x6, K.GT);
      }
    },
    GTE:{
      get:function() {
        return new Enum(">=", 0x7, K.GTE);
      }
    },
    LT:{
      get:function() {
        return new Enum("<", 0x8, K.LT);
      }
    },
    LTE:{
      get:function() {
        return new Enum("<=", 0x9, K.LTE);
      }
    },
    getValueOf:{
      get:function() {
        return function valueOf(str) {
          var _ = BooleanOper;
          if (str === K.AND) {
            return _.AND;
          } else if (str === K.OR) {
            return _.OR;
          } else if (str === K.NOT) {
            return _.NOT;
          } else if (str === K.EQ) {
            return _.EQ;
          } else if (str === K.NEQ) {
            return _.NEQ;
          } else if (str === K.GT) {
            return _.GT;
          } else if (str === K.GTE) {
            return _.GTE;
          } else if (str === K.LT) {
            return _.LT;
          } else if (str === K.LTE) {
            return _.LTE;
          } else {
            throw "Exception";
          }
        };
      }
    },
    
  });
  
  Object.defineProperties(container,{
    BoolOper:{
      get:function() {
        return BooleanOper;
      }
    }
  });
})(window._parser);