(function(container) {

    
  function mouseEnterDOM(evt) {
    var sel = document.getElementsByClassName("selected");
    for(var i=0,l=sel.length;i<l;i++) {
      sel[i].classList.remove("selected");
    }
    var parent = evt.target.parentNode;
    parent.classList.add("selected");
  }
  
  function mouseLeaveDOM(evt) {
    evt.target.parentNode.classList.remove("selected");
  }
  
  function BooleanNode(args) {
    var _this = container.checkInit(this);
    var _super = new container.Node({parent:args.parent});
    
    args = args || {};
    var pvt = {
      type : args.type,
      childNodes : []
    };

    function toString() {
      var result="";
      switch(pvt.type) {
        case BooleanNode.CONSTANT:
          result = pvt.childNodes[0].slice(0,1).toUpperCase()+pvt.childNodes[0].slice(1,pvt.childNodes[0].length);
          break;
        case BooleanNode.OPERATION:
          result = [pvt.operation.name, pvt.childNodes[0], pvt.childNodes[1]].join();
          break;
        case BooleanNode.IDENTIFIER:
          result = ["Identifier[",pvt.childNodes[0],"]"].join("");
          break;
        case BooleanNode.NOT:
          result = [pvt.operation.name, pvt.childNodes[0].toString()].join();
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
    
    function Action(params) {
      var action = document.createElement("li");
      action.classList.add("Action");
      action.textContent = params.text || "";
      action.addEventListener("click", function(evt) {
        alert(evt.target.textContent);
      });
      return action;
    }
    
    function initBoolOperToolbar() {
      var toolbar = pvt.toolbar = document.createElement("ul");
      toolbar.classList.add("toolbar");
      toolbar.classList.add(pvt.operation.name);
      toolbar.appendChild(new Action({text:"!"}));
      toolbar.appendChild(new Action({text:"&&"}));
      toolbar.appendChild(new Action({text:"||"}));
      toolbar.addEventListener("mouseleave", mouseLeaveDOM);
      _super.getDOM().appendChild(toolbar);
    }
    
    function initArithComparOperToolbar() {
      var toolbar = pvt.toolbar = document.createElement("ul");
      toolbar.classList.add("toolbar");
      toolbar.classList.add(pvt.operation.name);
      toolbar.appendChild(new Action({text:"!"}));
      toolbar.appendChild(new Action({text:"&&"}));
      toolbar.appendChild(new Action({text:"=="}));
      toolbar.appendChild(new Action({text:"!="}));
      toolbar.appendChild(new Action({text:">="}));
      toolbar.appendChild(new Action({text:">"}));
      toolbar.appendChild(new Action({text:"<="}));
      toolbar.appendChild(new Action({text:"<"}));
      toolbar.addEventListener("mouseleave", mouseLeaveDOM);
      _super.getDOM().appendChild(toolbar);
    }
    
    function initNegToolbar() {
      var toolbar = pvt.toolbar = document.createElement("ul");
      toolbar.classList.add("toolbar");
      toolbar.classList.add(pvt.operation.name);
      toolbar.appendChild(new Action({text:"||"}));
      toolbar.appendChild(new Action({text:"&&"}));
      toolbar.appendChild(new Action({text:"!"}));
      toolbar.addEventListener("mouseleave", mouseLeaveDOM);
      _super.getDOM().appendChild(toolbar);
    }
    
    function TextContainer(text, type) {
      var dom = document.createElement(type || "div");
      dom.appendChild(document.createTextNode(text));
      dom.classList.add("txt-cont");
      return dom;
    }
    
    function initValueToolbar() {
      var toolbar = pvt.toolbar = document.createElement("ul");
      toolbar.classList.add("toolbar");
      toolbar.classList.add("Value");
      toolbar.appendChild(new Action({text:"&&"}));
      toolbar.appendChild(new Action({text:"||"}));
      toolbar.appendChild(new Action({text:"!"}));
      
      var variable = document.createElement("li");
      toolbar.appendChild(variable);
      variable.appendChild(new TextContainer("Variáveis"));
      var variableMenu = document.createElement("ul");
      variable.appendChild(variableMenu);
      
      var _ = container.Node;
      var variables = _.getVariables(_.VariableType.NUMBER);
      for(var i=0, l=variables.length;i<l;i++) {
        variableMenu.appendChild(new Action({text:variables[i]}));
      }
      toolbar.appendChild(new Action({text:"VERDADEIRO"}));
      toolbar.appendChild(new Action({text:"FALSO"}));
      toolbar.appendChild(new Action({text:"0==0"}));
      toolbar.appendChild(new Action({text:"''==''"}));
      toolbar.appendChild(new Action({text:"true?true:false"}));
      toolbar.addEventListener("mouseleave", mouseLeaveDOM);
      _super.getDOM().appendChild(toolbar);
    }
    
    function renderOperationDOM() {
      var dom = _super.getDOM();
      dom.classList.add(pvt.operation.name);
      
      dom.appendChild(container.createDOM({text:"(", type:"span", classes:["Text"], mouseenter:mouseEnterDOM}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(container.createDOM({text:pvt.operation.label, type:"span", classes:["Text", "Operator"], mouseenter:mouseEnterDOM}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(container.createDOM({text:")", type:"span", classes:["Text"], mouseenter:mouseEnterDOM}));
      
      switch(pvt.operation.valueOf()) {
        case container.BoolOper.AND.valueOf():
        case container.BoolOper.OR.valueOf():
          initBoolOperToolbar();
          break;
        case container.BoolOper.EQ.valueOf():
        case container.BoolOper.NEQ.valueOf():
        case container.BoolOper.GTE.valueOf():
        case container.BoolOper.GT.valueOf():
        case container.BoolOper.LTE.valueOf():
        case container.BoolOper.LT.valueOf():
          initArithComparOperToolbar();
          break;
      }
    }
    
    function setOperation(operation) {
      
    }
    
    function renderNegationDOM() {
      var dom = _super.getDOM();
      dom.classList.add(pvt.operation.name);
      dom.appendChild(container.createDOM({text:pvt.operation.label, type:"span", classes:["Text", "Operator"], mouseenter:mouseEnterDOM}));
      updateParent(pvt.childNodes[0]);
      initNegToolbar();
    }
    
    function renderValueDOM(text) {
      var dom = _super.getDOM();
      dom.classList.add("Value");
      dom.appendChild(container.createDOM({type:"span", text:text, classes:["BooleanNode","Text", "Value"], mouseenter:mouseEnterDOM}));
      initValueToolbar();
    }
    
    (function Constructor() {
      var dom = _super.getDOM();
      dom.classList.add("BooleanNode");
      switch(pvt.type) {
        case BooleanNode.OPERATION:
          pvt.operation = container.BoolOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          renderOperationDOM();
          break;
        case BooleanNode.NOT:
          pvt.operation = container.BoolOper.getValueOf("Not");
          pvt.childNodes.push(args.value);
          
          renderNegationDOM();
          break;
        case BooleanNode.CONSTANT:
          pvt.childNodes.push(args.value);
          
          renderValueDOM(pvt.childNodes[0]==="true"?"VERDADEIRO":"FALSO");
          break;
        case BooleanNode.IDENTIFIER:
          pvt.childNodes.push(args.value);
          
          renderValueDOM(["[",pvt.childNodes[0],"]"].join(""));
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
        return 0x3;
      }
    },
    NOT:{
      get:function() {
        return 0x4;
      }
    },
    EXPRESSION:{
      get:function() {
        return 0x5;
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