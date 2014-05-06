(function(container) {
  ArithNode.prototype = new container.Node();
  Object.defineProperties(ArithNode, {
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
    NEGATIVE:{
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

  function ArithNode(args) {
    var _this = container.checkInit(this);
    var _super = new container.Node({parent:args.parent});
    var pvt = {
      type:args.type,
      childNodes:[]
    };
    
    function isNumber(val) {
      val = val.trim();
      return (!isNaN(val) && isFinite(val) && val!=="");
    }
    
    function isFloat(val) {
      return isNumber(val) && !Number.isInteger(val);
    }
    
    function isInteger(val) {
      return isNumber(val) && Number.isInteger(val);
    }
    
    function formatValue(val) {
      var result = [];
      if (isNumber(val)) {
        val = Number.parseFloat(val);
        if (Number.isInteger(val)){
          result.push("Integer[");
        } else {
          result.push("FloatingPoint[");
        }
      } else {
        throw "NaN";
      }
      result.push(val);
      result.push("]");
      return result.join("");
    }
    
    function toString() {
      var result = "";
      switch(pvt.type) {
        case ArithNode.OPERATION:
          result = [pvt.operation, pvt.childNodes[0].toString(), pvt.childNodes[1].toString()].join();
          break;
        case ArithNode.IDENTIFIER:
          result = ["Identifier[",pvt.childNodes[0],"]"].join("");
          break;
        case ArithNode.CONSTANT:
          result = formatValue(pvt.childNodes[0]);
          break;
        case ArithNode.NEGATIVE:
          result = [pvt.operation,pvt.childNodes[0]].join();
          break;
        case ArithNode.EXPRESSION:
          result = ["Choice",pvt.condition.toString(), pvt.childNodes[0], pvt.childNodes[1]].join();
          break;
      }
      return result;
    }
    
    function valueOf() {
      return args.type;
    }
    
    function getNodeType() {
      return "ArithNode";
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

    function operationToString() {
      var result = "";
      if (pvt.operation === "Plus") {
        result = "+";
      } else if (pvt.operation === "Minus" || pvt.operation === "Negative") {
        result = "-";
      } else if (pvt.operation === "Mult") {
        result = "*";
      } else if (pvt.operation === "Div") {
        result = "/";
      } else {
        throw "Operation not supported";
      }
      return result;
    }
    
    function updateParent(node) {
      if (node instanceof container.Node) {
        node.parent = _this;
      }
    }
    
    function generateDOM() {
      var dom = _super.getDOM();
      dom.classList.add("ArithmeticNode");
      switch(pvt.type) {
        case ArithNode.OPERATION:
          dom.classList.add(pvt.operation);
          dom.appendChild(document.createTextNode("("));
          //updateParent(pvt.childNodes[0]);
          dom.appendChild(document.createTextNode(operationToString()));
          //updateParent(pvt.childNodes[1]);
          dom.appendChild(document.createTextNode(")"));
          break;
        case ArithNode.NEGATIVE:
          dom.classList.add(pvt.operation);
          dom.appendChild(document.createTextNode(operationToString()));
          //updateParent(pvt.childNodes[0]);
          break;
        case ArithNode.IDENTIFIER:
        case ArithNode.CONSTANT:
          dom.classList.add("Value");
          dom.appendChild(document.createTextNode(pvt.childNodes[0]));
          break;
        case ArithNode.EXPRESSION:
          dom.classList.add("Expression");
          dom.appendChild(document.createTextNode("SE"));
          //updateParent(pvt.condition);
          dom.appendChild(document.createTextNode("ENTÃO RETORNE"));
          //updateParent(pvt.childNodes[0]);
          dom.appendChild(document.createTextNode("SENÃO RETORNE"));
          //updateParent(pvt.childNodes[1]);
          break;
      }
    }

    (function () {
      switch(pvt.type) {
        case ArithNode.OPERATION:
          pvt.operation = args.operation;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          updateParent(pvt.childNodes[0]);
          updateParent(pvt.childNodes[1]);
          break;
        case ArithNode.NEGATIVE:
          pvt.operation = "Negative";
          pvt.childNodes.push(args.value);
          updateParent(pvt.childNodes[0]);
          break;
        case ArithNode.IDENTIFIER:
        case ArithNode.CONSTANT:
          pvt.childNodes.push(args.value);
          break;
        case ArithNode.EXPRESSION:
          pvt.condition = args.condition;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          updateParent(pvt.condition);
          updateParent(pvt.childNodes[0]);
          updateParent(pvt.childNodes[1]);
          break;
      }
      generateDOM();
    })();
  }
  
  Object.defineProperties(container,{
    ArithNode:{
      get:function() {
        return ArithNode;
      }
    }
  });
  
})(window._parser);