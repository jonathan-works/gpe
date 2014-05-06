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

    (function () {
      var dom = _super.getDOM();
      dom.classList.add("ArithmeticNode");
      switch(pvt.type) {
        case ArithNode.OPERATION:
          pvt.operation = args.operation;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          dom.appendChild(container.createDOM({type:"span", text:"(", classes:["Text"]}));
          updateParent(pvt.childNodes[0]);
          dom.appendChild(container.createDOM({type:"span", text:operationToString(), classes:["Text","Operator"]}));
          updateParent(pvt.childNodes[1]);
          dom.appendChild(container.createDOM({type:"span", text:")", classes:["Text"]}));
          dom.classList.add(pvt.operation);
          break;
        case ArithNode.NEGATIVE:
          pvt.operation = "Negative";
          pvt.childNodes.push(args.value);
          
          dom.appendChild(container.createDOM({type:"span", text:operationToString(), classes:["Text","Operator"]}));
          updateParent(pvt.childNodes[0]);
          dom.classList.add(pvt.operation);
          break;
        case ArithNode.IDENTIFIER:
          pvt.childNodes.push(args.value);
          dom.appendChild(container.createDOM({type:"span", text:["[",pvt.childNodes[0],"]"].join("")}));
          dom.classList.add("Value");
          break;
        case ArithNode.CONSTANT:
          pvt.childNodes.push(args.value);
          dom.appendChild(container.createDOM({type:"span", text:pvt.childNodes[0]}));
          dom.classList.add("Value");
          break;
        case ArithNode.EXPRESSION:
          pvt.condition = args.condition;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          
          dom.appendChild(container.createDOM({type:"div", text:"SE", classes:["Text"]}));
          updateParent(pvt.condition);
          dom.appendChild(container.createDOM({type:"div", text:"ENTÃO RETORNE", classes:["Text"]}));
          updateParent(pvt.childNodes[0]);
          dom.appendChild(container.createDOM({type:"div", text:"SENÃO RETORNE", classes:["Text"]}));
          updateParent(pvt.childNodes[1]);
          
          dom.classList.add("Expression");
          break;
      }
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