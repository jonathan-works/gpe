(function(K) {
  var V = {
    get CONSTANT()0x1,
    get OPERATION()0x2,
    get IDENTIFIER()0x4,
    get NEGATIVE()0x8,
    get EXPRESSION()0x10,
    get FLOAT_STR()"FloatingPoint",
    get INT_STR()"Integer",
    get NAME()"ArithNode",
    get INT_PATT()"[+|-]?[1-9]*[0-9]",
    get FLOAT_SUFX()"[.][0-9]+",
    get FLOAT_PATT()this.INT_PATT+"("+this.FLOAT_SUFX+")?"
  };

  function ArithNode(args) {
    var _this = K.checkInit(this);
    var _super = new K.Node({parent:args.parent});
    var pvt = {
      type:args.type,
      childNodes:[]
    };
    
    function formatValue(val) {
      var result = [];
      if (isFloat(val)) {
        result.push(V.FLOAT_STR);
      } else if (isInteger(val)){
        result.push(V.INT_STR);
      } else {
        throw "NaN";
      }
      result.push("[");
      result.push(val);
      result.push("]");
      return result.join("");
    }
    
    function getStack() {
      var result = [];
      var appendArray = function (itm) {
        result.push(itm);
      };
      switch(pvt.type) {
        case V.OPERATION:
          result.push(pvt.operation);
          pvt.childNodes[0].getStack().forEach(appendArray);
          pvt.childNodes[1].getStack().forEach(appendArray);
          break;
        case V.IDENTIFIER:
          result = [[K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("")];
          break;
        case V.CONSTANT:
          result = [formatValue(pvt.childNodes[0])];
          break;
        case V.NEGATIVE:
          result.push(pvt.operation);
          pvt.childNodes[0].getStack().forEach(appendArray);
          break;
        case V.EXPRESSION:
          result.push(K._.CHOICE);
          pvt.childNodes[0].getStack().forEach(appendArray);
          pvt.childNodes[1].getStack().forEach(appendArray);
          pvt.childNodes[2].getStack().forEach(appendArray);
          break;
      }
      return result;
    }
    
    function toString() {
      var result = "";
      switch(pvt.type) {
        case V.OPERATION:
          result = [pvt.operation, pvt.childNodes[0].toString(), pvt.childNodes[1].toString()].join();
          break;
        case V.IDENTIFIER:
          result = [K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("");
          break;
        case V.CONSTANT:
          result = formatValue(pvt.childNodes[0]);
          break;
        case V.NEGATIVE:
          result = [pvt.operation,pvt.childNodes[0]].join();
          break;
        case V.EXPRESSION:
          result = [K._.CHOICE,pvt.childNodes[0].toString(), pvt.childNodes[1], pvt.childNodes[2]].join();
          break;
      }
      return result;
    }
    
    function getNodeType() {
      return V.NAME;
    }
    
    Object.defineProperties(_this, {
      getDOM:{
        get:function() {
          return _super.getDOM;
        }
      },
      parent:{
        get:function() {
          return _super.parent;
        },
        set:function(itm) {
          _super.parent = itm;
        }
      },
      toString : {
        get:function() {
          return toString;
        }
      },
      valueOf:{
        get:function() {
          return valueOf;
        }
      },
      getStack:{
        get:function() {
          return getStack;
        }
      },
      getNodeType:{
        get:function() {
          return getNodeType;
        }
      }
    });

    function operationToString() {
      var result = "";
      result = pvt.operation.label;
      return result;
    }
    
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }

    function renderOperationDOM() {
      var dom = _super.getDOM();
      dom.appendChild(K.createDOM({text:"(", classes:[K._.TEXT]}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({text:operationToString(), classes:[K._.TEXT,K._.OPER]}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({text:")", classes:[K._.TEXT]}));
      dom.classList.add(pvt.operation);
    }
    
    function renderNegativeDOM() {
      var dom = _super.getDOM();
      dom.appendChild(K.createDOM({text:operationToString(), classes:[K._.TEXT,K._.OPER]}));
      updateParent(pvt.childNodes[0]);
      dom.classList.add(pvt.operation);
    }
    
    function renderValueDOM(){
      var dom = _super.getDOM();
      if (pvt.type === V.IDENTIFIER) {
        dom.appendChild(K.createDOM({text:["[",pvt.childNodes[0],"]"].join("")}));
      } else {
        dom.appendChild(K.createDOM({text:pvt.childNodes[0].toLocaleString(navigator.language)}));
      }
      dom.classList.add(K._.VALUE);
    }

    function renderExpressionDOM() {
      dom.appendChild(K.createDOM({type:K._.DIV, text:K.getMessage([V.NAME,"if"].join(".")), classes:[K._.TEXT]}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({type:K._.DIV, text:K.getMessage([V.NAME,"then"].join(".")), classes:[K._.TEXT]}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({type:K._.DIV, text:K.getMessage([V.NAME,"else"].join(".")), classes:[K._.TEXT]}));
      updateParent(pvt.childNodes[2]);
      dom.classList.add(K._.EXPRESSION);
    }
    
    var renderDOM;
    
    (function () {
      var dom = _super.getDOM();
      dom.classList.add(V.NAME);
      switch(pvt.type) {
        case V.OPERATION:
          pvt.operation = K.ArithOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          renderDOM = renderOperationDOM;
          break;
        case V.NEGATIVE:
          pvt.operation = K.ArithOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value);
          renderDOM = renderNegativeDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(args.value);
          renderDOM = renderValueDOM;
          break;
        case V.CONSTANT:
          pvt.childNodes.push(Number.parseFloat(new RegExp(V.FLOAT_PATT).exec(args.value)[0]));
          renderDOM = renderValueDOM;
          break;
        case V.EXPRESSION:
          pvt.childNodes.push(args.condition);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          renderDOM = renderExpressionDOM;
          break;
        default:
          throw 0;
      }
      renderDOM();
    })();
  }

  function isFloat(val) {
    return new RegExp(V.INT_PATT+V.FLOAT_SUFX).test(val);
  }
  
  function isInteger(val) {
    return new RegExp(V.INT_PATT).test(val);
  }

  function isNumberConstant(current) {
    return new RegExp(V.INT_STR+"\\["+V.INT_PATT+"\\]|"+V.FLOAT_STR+"\\["+V.FLOAT_PATT+"\\]").test(current);
  }
  
  function getArithNodeType(str) {
    var type;
    var _ = K.Node;
    if (K._.REGX_IDENT.test(str) && _.getVariables(_.VariableType.NUMBER).indexOf(str.slice(11,str.length-1))>=0) {
      type = V.IDENTIFIER;
    } else if (isNumberConstant(str)) {
      type = V.CONSTANT;
    } else if (K.ArithOper.isArithOper(str)){
      type = K.BooleanOper.getValueOf(str);
      if (type === K.BooleanOper.NEGATIVE) {
        type = V.NEGATIVE;
      } else {
        type = V.OPERATION;
      }
    } else if (K._.CHOICE===str) {
      type = V.EXPRESSION;
    } else {
      type = 0x0;
    }
    return type;
  }
  
  function isArithNode(str) {
    var _ = K.Node;
    return K.ArithOper.isArithOper(str) || (K._.REGX_IDENT.test(str) && _.getVariables(_.VariableType.NUMBER).indexOf(str.slice(11,str.length-1))>=0) || isNumberConstant(str);
  }
  
  function valueOf(){
    return 0x4;
  }
  
  function getLabel(str) {
    (((K.messages || {})[navigator.language] || {ArithOper:{}}).ArithNode || {})[str] || [V.NAME,str].join(".");
  }
  
  ArithNode.prototype = new K.Node();
  Object.defineProperties(ArithNode, {
    isArithNode:{
      get:function(){return isArithNode;}
    },
    isNumberConstant:{
      get:function(){return isNumberConstant;}
    },
    getArithNodeType:{
      get:function(){return getArithNodeType;}
    },
    CONSTANT:{
      get:function(){return V.CONSTANT;}
    },
    OPERATION:{
      get:function(){return V.OPERATION;}
    },
    IDENTIFIER:{
      get:function(){return V.IDENTIFIER;}
    },
    NEGATIVE:{
      get:function(){return V.NEGATIVE;}
    },
    EXPRESSION:{
      get:function(){return V.EXPRESSION;}
    }
  });
  
  Object.defineProperties(K,{
    ArithNode:{
      get:function(){return ArithNode;}
    }
  });
  
})(window._parser);