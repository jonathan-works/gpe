(function(container) {

  function BooleanNode(args) {
    var _this = container.checkInit(this);
    var _super = new container.Node({parent:args.parent});
    
    args = args || {};
    var pvt = {
      type : args.type,
      childNodes : []
    };

    function operationToString() {
      var result = "";
      if (pvt.operation === "Or") {
        result = "Ou";
      } else if (pvt.operation === "Not") {
        result = "Negação";
      } else if (pvt.operation === "And") {
        result = "E";
      } else if (pvt.operation === "GreaterThanEqual") {
        result = ">=";
      } else if (pvt.operation === "GreaterThan") {
        result = ">";
      } else if (pvt.operation === "LessThanEqual") {
        result = "<=";
      } else if (pvt.operation === "LessThan") {
        result = "<";
      } else if (pvt.operation === "Equal") {
        result = "==";
      } else if (pvt.operation === "NotEqual") {
        result = "!=";
      } else {
        throw "Operation not supported";
      }
      return result;
    }

    function toString() {
      var result="";
      switch(pvt.type) {
        case BooleanNode.CONSTANT:
          //result = pvt.childNodes[0];
          result = pvt.childNodes[0].slice(0,1).toUpperCase()+pvt.childNodes[0].slice(1,pvt.childNodes[0].length);
          break;
        case BooleanNode.OPERATION:
          result = [pvt.operation, pvt.childNodes[0], pvt.childNodes[1]].join();
          break;
        case BooleanNode.IDENTIFIER:
          result = ["Identifier[",pvt.childNodes[0],"]"].join("");
          break;
        case BooleanNode.NOT:
          result = [pvt.operation, pvt.childNodes[0].toString()].join();
          break;
      }
      return result;
    }
    
    function valueOf() {
      return pvt.type;
    }
    
    function clear() {
    }
    
    function getDOM() {
    }
    
    function getType() {
      return pvt.type;
    }
    
    function getValues() {
      return pvt.childNodes.slice(0, pvt.childNodes.length);
    }
    
    function nodeType() {
      return "BooleanNode";
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
      clear: {
        get:function() {
          return clear;
        }
      },
      type : {
        get:getType
      },
      values : {
        get:getValues
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
      nodeType:{
        get:nodeType
      }
    });
    
    function updateParent(node) {
      if (node instanceof container.Node) {
        node.parent = _this;
      }
    }
    
    (function Constructor() {
      var dom = _super.getDOM();
      dom.classList.add("BooleanNode");
      switch(pvt.type) {
        case BooleanNode.OPERATION:
          pvt.operation = args.operation;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          dom.classList.add(pvt.operation);
          dom.appendChild(container.createDOM({text:"(", type:"span", classes:["Text"]}));
          updateParent(pvt.childNodes[0]);
          dom.appendChild(container.createDOM({text:operationToString(), type:"span", classes:["Text", "Operator"]}));
          updateParent(pvt.childNodes[1]);
          dom.appendChild(container.createDOM({text:")", type:"span", classes:["Text"]}));
          break;
        case BooleanNode.NOT:
          pvt.operation = "Not";
          pvt.childNodes.push(args.value);
          dom.appendChild(container.createDOM({text:operationToString(), type:"span", classes:["Text", "Operator"]}));
          updateParent(pvt.childNodes[0]);
          break;
        case BooleanNode.CONSTANT:
          dom.classList.add("Value");
          pvt.childNodes.push(args.value);
          dom.appendChild(container.createDOM({type:"span", text:pvt.childNodes[0]==="true"?"VERDADEIRO":"FALSO"}));
          break;
        case BooleanNode.IDENTIFIER:
          dom.classList.add("Value");
          pvt.childNodes.push(args.value);
          dom.appendChild(container.createDOM({type:"span", text:["[",pvt.childNodes[0],"]"].join("")}));
          break;
        default:
          throw "Missing type value";
      }
    })();
  }
  
  BooleanNode.prototype = new container.Node();
  /*    CONSTANTES DA CLASSE    */
  Object.defineProperties(BooleanNode, {
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
    NOT:{
      get:function() {
        return 0x8;
      }
    },
    EXPRESSION:{
      get:function() {
        return 0x10;
      }
    }
  });
  
  Object.defineProperties(container,{
    BooleanNode:{
      get:function() {
        return BooleanNode;
      }
    }
  });
})(window._parser);