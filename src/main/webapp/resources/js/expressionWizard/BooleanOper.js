(function(K) {
  
  var V = {
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
  /*current === "Or" || current === "And" || current === "NotEqual" || current === "Equal" || current === "GreaterThan" || current === "GreaterThanEqual"
       || current === "LessThan" || current === "LessThanEqual"*/
  var BoolOper = Object.defineProperties({},{
    AND:{
      get:function() {
        return new K.Enum("E", 0x1, V.AND);
      }
    },
    OR:{
      get:function() {
        return new K.Enum("OU", 0x2, V.OR);
      }
    },
    NOT:{
      get:function() {
        return new K.Enum("NEGAÇÃO", 0x3, V.NOT);
      }
    },
    EQ:{
      get:function() {
        return new K.Enum("==", 0x4, V.EQ);
      }
    },
    NEQ:{
      get:function() {
        return new K.Enum("!=", 0x5, V.NEQ);
      }
    },
    GT:{
      get:function() {
        return new K.Enum(">", 0x6, V.GT);
      }
    },
    GTE:{
      get:function() {
        return new K.Enum(">=", 0x7, V.GTE);
      }
    },
    LT:{
      get:function() {
        return new K.Enum("<", 0x8, V.LT);
      }
    },
    LTE:{
      get:function() {
        return new K.Enum("<=", 0x9, V.LTE);
      }
    },
    getValueOf:{
      get:function() {
        return function getValueOf(str) {
          var _ = BoolOper;
          if (str === V.AND) {
            return _.AND;
          } else if (str === V.OR) {
            return _.OR;
          } else if (str === V.NOT) {
            return _.NOT;
          } else if (str === V.EQ) {
            return _.EQ;
          } else if (str === V.NEQ) {
            return _.NEQ;
          } else if (str === V.GT) {
            return _.GT;
          } else if (str === V.GTE) {
            return _.GTE;
          } else if (str === V.LT) {
            return _.LT;
          } else if (str === V.LTE) {
            return _.LTE;
          } else {
            throw 0;
          }
        };
      }
    },
    isBoolOper:{
      get:function() {
        return function isBoolOper(str) {
          var result = true;
          try {
            K.BoolOper.getValueOf(str);
          } catch(e) {
            result = false;
          }
          return result;
        };
      }
    },
    toString:{
      get:function () {
        return function toString() {
          return "BooleanOper";
        };
      }
    }
  });
  
  Object.defineProperties(K,{
    BoolOper:{
      get:function() {
        return BoolOper;
      }
    }
  });
})(window._parser);