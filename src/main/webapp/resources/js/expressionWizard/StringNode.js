(function(K) {
  StringNode.prototype = new K.Node();
  
  var V = {
    STRING:"String",
    IDENTIFIER:"Identifier",
    CHOICE:"Choice",
    NAME:"StringNode",
    LN_BRK_CLS:"breakLine",
    SPAN:"span",
    DIV:"div",
    EXPRESSION:"Expression",
    TEXT:"Text",
    OPER:"Operator",
    VALUE:"Value",
    IF:"SE",
    THEN:"ENTÃO RETORNE",
    ELSE:"SENÃO RETORNE"
  };
  
  Object.defineProperties(StringNode, {
    CONSTANT:{
      get:function() {
        return 0x1;
      }
    },
    OPERATION:{
      get:function() {
        return 0x2;
      }
    },
    IDENTIFIER:{
      get:function() {
        return 0x4;
      }
    },
    EXPRESSION:{
      get:function() {
        return 0x8;
      }
    }
  });

  function StringNode(args) {
    var _this = K.checkInit(this);
    var _super = new K.Node({parent:args.parent});
    var pvt = {
      type:args.type,
      childNodes:[]
    };
    
    function toString() {
      var result = "";
      switch(pvt.type) {
        case StringNode.CONSTANT:
          result = [V.STRING,"[",pvt.childNodes[0],"]"].join("");
          break;
        case StringNode.OPERATION:
          result = [pvt.operation, pvt.childNodes[0].toString(), pvt.childNodes[1].toString()].join();
          break;
        case StringNode.IDENTIFIER:
          result = [V.IDENTIFIER,"[",pvt.childNodes[0],"]"].join("");
          break;
        case StringNode.EXPRESSION:
          result = [V.CHOICE,pvt.condition.toString(), pvt.childNodes[0].toString(), pvt.childNodes[1].toString()].join();
          break;
      }
      return result;
    }
    
    function valueOf() {
      return args.type;
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
      getNodeType:{
        get:function() {
          return getNodeType;
        }
      }
    });
    
    function addLineBreak(node) {
      node.getDOM().classList.add(V.LN_BRK_CLS);
    }
    
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }
    
    (function() {
      var dom = _super.getDOM();
      dom.classList.add(V.NAME);
      switch(pvt.type) {
        case StringNode.OPERATION:
          pvt.operation = args.operation;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          updateParent(pvt.childNodes[0]);
          dom.appendChild(K.createDOM({text:"+", classes:[V.TEXT,V.OPER]}));
          updateParent(pvt.childNodes[1]);
          dom.classList.add(pvt.operation);
          break;
        case StringNode.IDENTIFIER:
          pvt.childNodes.push(args.value);
          
          dom.appendChild(K.createDOM({text:["[",pvt.childNodes[0],"]"].join("")}));
          dom.classList.add(V.VALUE);
          break;
        case StringNode.CONSTANT:
          pvt.childNodes.push(args.value);
          dom.appendChild(K.createDOM({text:pvt.childNodes[0]}));
          dom.classList.add(V.VALUE);
          break;
        case StringNode.EXPRESSION:
          pvt.condition = args.condition;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          dom.appendChild(K.createDOM({type:V.DIV, text:V.IF, classes:[V.TEXT]}));
          updateParent(pvt.condition);
          dom.appendChild(K.createDOM({type:V.DIV, text:V.THEN, classes:[V.TEXT]}));
          updateParent(pvt.childNodes[0]);
          dom.appendChild(K.createDOM({type:V.DIV, text:V.ELSE, classes:[V.TEXT]}));
          updateParent(pvt.childNodes[1]);
          
          dom.classList.add(V.EXPRESSION);
          break;
      }
    })();
  }
  
  function isStringNode(str) {
    var _ = K.Node;
    
    return (str.indexOf(V.STRING)===0 && str.match(/String\['.*'\]/g)!==null)
      || ((str.indexOf(V.IDENTIFIER)===0 && str.match(/Identifier\[[a-zA-Z][a-zA-Z0-9]*\]/g) !== null)
            && _.getVariables().indexOf(str.slice(11,str.length-1))>=0)
      || (str === V.TRUE || str === V.FALSE);
  }
  
  function getStringNodeType(str) {
    var type;
    var _ = K.Node;
    if (str.indexOf(V.IDENTIFIER)===0 && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) {
      type = BooleanNode.IDENTIFIER;
    } else if (str === V.TRUE || str === V.FALSE) {
      type = BooleanNode.CONSTANT;
    } else if (K.BoolOper.isBoolOper(str)){
      type = K.BoolOper.getValueOf(str);
      if (type.ordinal === K.BoolOper.NOT.ordinal) {
        type = BooleanNode.NOT;
      } else {
        type = BooleanNode.OPERATION;
      }
    } else {
      type = 0x0;
    }
    return type;
  }
  
  Object.defineProperties(K,{
    StringNode:{
      get:function() {
        return StringNode;
      }
    }
  });
  
})(window._parser);