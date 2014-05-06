(function(container) {
  StringNode.prototype = new container.Node();
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
    var _this = container.checkInit(this);
    var _super = new container.Node({parent:args.parent});
    var pvt = {
      type:args.type,
      childNodes:[]
    };
    
    function toString() {
      var result = "";
      switch(pvt.type) {
        case StringNode.CONSTANT:
          result = ["String[",pvt.childNodes[0],"]"].join("");
          break;
        case StringNode.OPERATION:
          result = [pvt.operation, pvt.childNodes[0].toString(), pvt.childNodes[1].toString()].join();
          break;
        case StringNode.IDENTIFIER:
          result = ["Identifier[",pvt.childNodes[0],"]"].join("");
          break;
        case StringNode.EXPRESSION:
          result = ["Choice",pvt.condition.toString(), pvt.childNodes[0].toString(), pvt.childNodes[1].toString()].join();
          break;
      }
      return result;
    }
    
    function valueOf() {
      return args.type;
    }
    
    function getNodeType() {
      return "StringNode";
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
      node.getDOM().classList.add("breakLine");
    }
    
    function updateParent(node) {
      if (node instanceof container.Node) {
        node.parent = _this;
      }
    }
    
    function generateDOM() {
      var dom = _super.getDOM();
      dom.classList.add("StringNode");
      switch(pvt.type) {
        case StringNode.OPERATION:
          dom.classList.add(pvt.operation);
          //updateParent(pvt.childNodes[0]);
          dom.appendChild(document.createTextNode("+"));
          //updateParent(pvt.childNodes[1]);
          break;
        case StringNode.IDENTIFIER:
        case StringNode.CONSTANT:
          dom.appendChild(document.createTextNode(pvt.childNodes[0]));
          dom.classList.add("Value");
          break;
        case StringNode.EXPRESSION:
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
    
    (function() {
      switch(pvt.type) {
        case StringNode.OPERATION:
          pvt.operation = args.operation;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          updateParent(pvt.childNodes[0]);
          updateParent(pvt.childNodes[1]);
          break;
        case StringNode.IDENTIFIER:
        case StringNode.CONSTANT:
          pvt.childNodes.push(args.value);
          break;
        case StringNode.EXPRESSION:
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
    StringNode:{
      get:function() {
        return StringNode;
      }
    }
  });
  
})(window._parser);