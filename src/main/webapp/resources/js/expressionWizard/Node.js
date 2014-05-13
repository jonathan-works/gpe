(function (K) {
  function Node(args){
    checkInit(this);
    
    var pvt = {
      dom:document.createElement("div")
    };
    
    function setParent(itm) {
      if (itm !== window) {
        if (itm instanceof K.Node) {
          itm.getDOM().appendChild(pvt.dom);
        } else {
          itm.appendChild(pvt.dom);
        }
        pvt.parent=itm;
      }
    }
    
    function getParent() {
      return pvt.parent;
    }
    
    function getDOM() {
      return pvt.dom;
    }
    
    function clear() {
      var dom = pvt.dom;
      for(var i=0,l=dom.classList.length; i<l;i++) {
        dom.classList.remove(dom.classList.item(0));
      }
      for(i=0,l=dom.children.length; i<l; i++) {
        dom.removeChild(dom.children[0]);
      }
    }
    
    Object.defineProperties(this,{
      parent:{
        get:getParent,
        set:setParent
      },
      getDOM:{
        get:function() {
          return getDOM;
        }
      },
      clear:{
        get:function() {
          return clear;
        }
      }
    });
    
    if (typeof args !== "undefined") {
      this.parent = args.parent;
    } else {
      args = args || {};
    }
    pvt.dom.classList.add("Node");
  }
  
  function checkInit(obj) {
    if (obj === window) {
      throw "window";
    }
    return obj;
  }
  
  var variables = {
    bool:[],
    str:[],
    numb:[]
  };
  
  function getVariables(type) {
    type = type || VariableType.STRING;
    var result = getVarArrayByType(type);
    return cloneArray(result);
  }
  
  function setVariables(array, type) {
    type = type || VariableType.STRING;
    switch(type) {
      case VariableType.STRING:
        variables.str = array;
        break;
      case VariableType.NUMBER:
        variables.numb = array;
        break;
      case VariableType.BOOLEAN:
        variables.bool = array;
        break;
    }
  }
  
  function getVarArrayByType(type) {
    var array;
    switch(type) {
      case VariableType.STRING:
        array = variables.str;
        break;
      case VariableType.NUMBER:
        array = variables.numb;
        break;
      case VariableType.BOOLEAN:
        array = variables.bool;
        break;
      default:
        array = [];
        break;
    }
    return array;
  }
  
  function cloneArray(arr) {
    return arr.slice(0, arr.length);
  }
  
  function removeVariable(varName, type) {
    type = type || VariableType.STRING;
    var array = getVarArrayByType(type);
    var index = array.indexOf(varName);
    if (index>=0) {
      array.splice(index,1);
    }
  }
  
  function addVariable(varName, type) {
    type = type || VariableType.STRING;
    var array = getVarArrayByType(type);
    array.push(varName);
  }
  
  function createBooleanNode(current, cache, dom) {
    var _value = [];
    var _type = K.BooleanNode.getBooleanNodeType(current);
    var result;
    switch(_type) {
      case K.BooleanNode.CONSTANT:
      case K.BooleanNode.IDENTIFIER:
        result = new K.BooleanNode({value:current, type:_type, parent:dom});
        break;
      case K.BooleanNode.NOT:
        result = new K.BooleanNode({operation:current, value:cache.pop(), type:_type, parent:dom});
        break;
      case K.BooleanNode.OPERATION:
        result = new K.BooleanNode({operation:current, value:[cache.pop(), cache.pop()], type:_type, parent:dom});
        break;
    }
    return result;
  }
  
  function generateTree(stack, dom) {
    var cache = [];
    var current;
    var result;
    while(stack.length > 0) {
      current = stack.shift();
      if (K.BooleanNode.isBooleanNode(current)) {
        result = createBooleanNode(current, cache, dom);
      } else if (current.indexOf("Integer")===0 && current.match(/Integer\[[+|-]?[0-9]+\]/g) !== null) {
        result = new K.ArithNode({type:K.ArithNode.CONSTANT, value:current.slice("8", current.length-1), parent:dom});
      } else if (current.indexOf("FloatingPoint")===0 && current.match(/FloatingPoint\[[+|-]?[0-9]+([.][0-9]+)?\]/g) !== null) {
         //FloatingPoint
        result = new K.ArithNode({type:K.ArithNode.CONSTANT, value:current.slice("14", current.length-1), parent:dom});
      } else if (current.indexOf("String")===0 && current.match(/String\['.*']/g) !== null) {
        result = new K.StringNode({type:K.StringNode.CONSTANT, value:current.slice("7", current.length-1), parent:dom});
      } else if (current.indexOf("Identifier")===0 && current.match(/Identifier\[[a-zA-Z][a-zA-Z0-9]*\]/g) !== null) {
        // é variável
        current = current.slice("11",current.length-1);
        if (variables.numb.indexOf(current) >= 0) {
          result = new K.ArithNode({value:current, type:K.ArithNode.IDENTIFIER, parent:dom});
        } else if (variables.str.indexOf(current) >= 0) {
          result = new K.StringNode({value:current, type:K.StringNode.IDENTIFIER, parent:dom});
        } else {
          throw "Identifier ["+current+"] not expected";
        }
      } else if (current === "Mult" || current === "Minus" || current === "Div") {
        result = new K.ArithNode({type:K.ArithNode.OPERATION, operation:current, value:[cache.pop(), cache.pop()], parent:dom});
      } else if (current === "Plus") {
        result = getStringOrNumberFromPlus({operation:current, value:[cache.pop(), cache.pop()], parent:dom});
      } else if (current === "Negative") {
        result = new K.ArithNode({type:K.ArithNode.NEGATIVE, value:cache.pop(), parent:dom});
       } else if (current === "Choice") {
         result = getCorrectExpression({condition:cache.pop(),value:[cache.pop(),cache.pop()], parent:dom});
      } else {
        throw "Parse exception, token "+current+" not found";
      }
      cache.push(result);
    }
    if (cache.length !== 1) {
      throw "Parse exception. More than one root was found";
    }
    return cache.pop();
  }
  
  function calculateValueTypes(obj) {
    var type = 0x0;
    if (obj.value[0] instanceof K.StringNode) {
      type = 0x1;
    } else if (obj.value[0] instanceof K.BooleanNode) {
      type = 0x2;
    } else if (obj.value[0] instanceof K.ArithNode) {
      type = 0x4;
    }
    if (obj.value[1] instanceof K.StringNode) {
      type |= 0x1;
    } else if (obj.value[1] instanceof K.BooleanNode) {
      type |= 0x2;
    } else if (obj.value[1] instanceof K.ArithNode) {
      type |= 0x4;
    }
    return type;
  }
  
  function getStringOrNumberFromPlus(obj) {
    var result;
    
    switch(calculateValueTypes(obj)) {
      case 0x1:
      case 0x3:
      case 0x5:
      case 0x6:
        obj.type = K.StringNode.OPERATION;
        result = new K.StringNode(obj);
        break;
      case 0x4:
        obj.type = K.ArithNode.OPERATION;
        result = new K.ArithNode(obj);
        break;
      default:
        throw "Arithmetic combination of values not expected "+obj.value[0].toString()+" "+obj.value[1].toString();
    }
    
    return result;
  }
  
  function getCorrectExpression(obj) {
    var result;
    switch(calculateValueTypes(obj)) {
      case 0x2:
        obj.type = K.BooleanNode.EXPRESSION;
        result = new K.BooleanNode(obj);
        break;
      case 0x4:
        obj.type = K.ArithNode.OPERATION;
        result = new K.ArithNode(obj);
        break;
      case 0x1:
      case 0x3:
      case 0x5:
      case 0x6:
        obj.type = K.StringNode.EXPRESSION;
        result = new K.StringNode(obj);
        break;
      default:
        throw "Conditional combination of values not expected";
    }
    
    return result;
  }
  
  function implReqrd(str) {
    throw ["Implementation of function ",str," required"].join("");
  }
  
  Node.prototype = {
    destroy:function destroy() {
      implReqrd();
    },
    getDOM:function getDOM() {
    },
    accept:function accept(visitor) {
      var visitStack = [this];
      var current;
      while(visitStack.length > 0) {
        current = visitStack.pop();
        var children = current.getChildNodes();
        for(var i=0,l=children.length;i<l;i++) {
          visitStack.push(children[i]);
        }
        visitor.visit(current);
      }
    },
    get parent() {
    },
    set parent(itm) {
    },
    get type() {
      implReqrd("type");
    },
    get values() {
      implReqrd("values");
    },
    get condition() {
      implReqrd("condition");
    },
    getNodeType:function getNodeType() {
      implReqrd("getNodeType");
    }
  };
  
  var VariableType = {};
  Object.defineProperties(VariableType,{
    STRING:{
      get:function() {
        return 0x1;
      }
    },BOOLEAN:{
      get:function() {
        return 0x2;
      }
    },NUMBER:{
      get:function() {
        return 0x3;
      }
    }
  });
  Object.defineProperties(Node,{
    VariableType:{
      get:function() {
        return VariableType;
      }
    },
    addVariable:{
      get:function() {
        return addVariable;
      }
    },
    removeVariable:{
      get:function() {
        return removeVariable;
      }
    },
    getVariables:{
      get:function() {
        return getVariables;
      }
    },
    generateTree:{
      get:function() {
        return generateTree;
      }
    }
  });
  
  function mouseEnterDOM(evt) {
    evt.target.parentNode.classList.add("selected");
    /*
    var toolbars = evt.target.getElementsByClassName("toolbar");
    for(var i=0,l=toolbars.length;i<l;i++) {
      toolbars[i].classList.add("visible");
    }
    //*/
  }
  
  function mouseLeaveDOM(evt) {
    evt.target.parentNode.classList.remove("selected");
  }
  
  function mouseClickDOM(evt) {
    
  }

  function createDOM(params) {
    if (this === window) {
      throw "Constructor Exception";
    }
    params = params || {};
    var type = params.type || "span";
    var text = params.text || "";
    var click = params.click;
    var mouseenter = params.mouseEnter;
    var mouseleave = params.mouseLeave;
    var parent = params.parent;
    var data = params.data || {};

    var classes = params.classes || [];
    var dom = document.createElement(type);
    var parentNode = params.parentNode;
    dom.appendChild(document.createTextNode(text));
    for(var i=0,l=classes.length;i<l;i++) {
      dom.classList.add(classes[i]);
    }
    
    for(var key in data) {
      dom[["data",key].join("-")] = data[key] || "";
    }
    
    if (typeof mouseenter === "function") {
      dom.addEventListener("mouseenter", mouseenter);
    }
    if (typeof mouseleave === "function") {
      dom.addEventListener("mouseleave", mouseleave);
    }
    if (typeof click === "function") {
      dom.addEventListener("click", click);
    }
    
    if (typeof parent !== "undefined" && parent instanceof HTMLElement) {
      parent.appendChild(dom);
    }
    
    return dom;
  }
  
  Object.defineProperties(K, {
    Node:{
      get:function() {
        return Node;
      }
    },
    checkInit:{
      get:function() {
        return checkInit;
      }
    },
    createDOM:{
      get:function() {
        return createDOM;
      }
    }
  });
  
})(window._parser = window._parser || {});