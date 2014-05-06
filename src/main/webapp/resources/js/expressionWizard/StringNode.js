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
    
    (function() {
      var dom = _super.getDOM();
      dom.classList.add("StringNode");
      switch(pvt.type) {
        case StringNode.OPERATION:
          pvt.operation = args.operation;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          updateParent(pvt.childNodes[0]);
          dom.appendChild(container.createDOM({type:"span", text:"+", classes:["Text","Operator"]}));
          updateParent(pvt.childNodes[1]);
          dom.classList.add(pvt.operation);
          break;
        case StringNode.IDENTIFIER:
          pvt.childNodes.push(args.value);
          
          dom.appendChild(container.createDOM({type:"span", text:["[",pvt.childNodes[0],"]"].join("")}));
          dom.classList.add("Value");
          break;
        case StringNode.CONSTANT:
          pvt.childNodes.push(args.value);
          dom.appendChild(container.createDOM({type:"span", text:pvt.childNodes[0]}));
          dom.classList.add("Value");
          break;
        case StringNode.EXPRESSION:
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
    StringNode:{
      get:function() {
        return StringNode;
      }
    }
  });
  
})(window._parser);