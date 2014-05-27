(function (K) {
  var V = {
    get TOOLBAR(){return "toolbar";},
    get TOOLBAR_ITM(){return "toolbar-itm";},
    get UNDEF(){return "undefined";},
    get DIV(){return "div";},
    get CSS_NODE(){return "Node";},
    get CSS_SEL_ND(){return "selected";},
    get IDENT_STR(){return "Identifier";},
    get MOUSE_LEAVE(){return "mouseleave";},
    get TEXT(){return "Text";},
    get OPER(){return "Operator";},
    get VALUE(){return "Value";},
    get EXPRESSION(){return "Expression";},
    get TEXT_TYPE(){return "txt-cont";},
    get CHOICE(){return "Choice";},
    get TYPE_STR(){return 0x1;},
    get TYPE_BOOL(){return 0x2;},
    get TYPE_NBR(){return 0x4;},
    get REGX_IDENT(){return (/^Identifier\[.+\]$/);},
    get DATA_TBR(){return "data-toolbar";},
    get DATA_OPER(){return "data-operation";},
    get DT_CLASS(){return "data-obj-class";},
    get DT_TYPE(){return "data-type";},
    get DT_VAL(){return "data-value";},
    get DT_INPT(){return "data-input";}
  };
  
  function Node(args){
    var _this=checkInit(this);
    var pvt = {
      dom:document.createElement(V.DIV)
    };
    
    function setParent(itm) {
      if (itm !== window && typeof itm !== V.UNDEF) {
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
    
    function attachInput(input){
      if (input instanceof HTMLInputElement || input instanceof HTMLTextAreaElement){
        pvt.dom[V.DT_INPT]=input;
        putStackToInput(pvt.dom[V.DT_CLASS],input);
        pvt.dom.addEventListener("selected",expressionChangedEvent);
      }
    }
    
    function clear() {
      var dom = pvt.dom;
      for(var i=0,l=dom.classList.length; i<l;i++) {
        dom.classList.remove(dom.classList.item(0));
      }
      for(i=0,l=dom.children.length; i<l; i++) {
        dom.removeChild(dom.children[0]);
      }
      pvt.dom.classList.add(V.CSS_NODE);
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
      },
      attachInput:{
        get:function(){
          return attachInput;
        }
      }
    });
    if (typeof args !== V.UNDEF && args.parent !== V.UNDEF) {
      this.parent = args.parent;
      pvt.dom.classList.add(V.CSS_NODE);
    } else {
      args = args || {};
    }
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
  
  function removeVariables(varName, array){
    var index = array.indexOf(varName);
    if (index>=0) {
      array.splice(index,1);
    }
  }
  
  function addVariables(varName,array){
    array.push(varName);
  }
  
  function executeVariableAction(varName,type,action){
    type = type || VariableType.STRING;
    var array = getVarArrayByType(type);
    if (varName.constructor !== [].constructor){
      action(varName,array);
    }else{
      for(var i=0,l=varName.length;i<l;i++){
        action(varName[i],array);
      }
    }
  }
  
  function removeVariable(varName, type) {
    executeVariableAction(varName,type,removeVariables);
  }
  
  function addVariable(varName, type) {
    executeVariableAction(varName,type,addVariables);
  }
  
  function clearVariables(type){
    type = type || VariableType.STRING;
    switch(type) {
      case VariableType.STRING:
        variables.str=[];
        break;
      case VariableType.NUMBER:
        variables.numb=[];
        break;
      case VariableType.BOOLEAN:
        variables.bool=[];
        break;
    }
  }
  
  function createBooleanNode(current, cache) {
    var _type = K.BooleanNode.getBooleanNodeType(current);
    var result;
    switch(_type) {
      case K.BooleanNode.CONSTANT:
      case K.BooleanNode.IDENTIFIER:
        result = new K.BooleanNode({value:[current], type:_type});
        break;
      case K.BooleanNode.NOT:
        result = new K.BooleanNode({operation:current, value:[cache.pop()], type:_type});
        break;
      case K.BooleanNode.OPERATION:
        result = new K.BooleanNode({operation:current, value:[cache.pop(), cache.pop()], type:_type});
        break;
      default:
        console.error("BooleanNode type not supported");
        throw 0;
    }
    return result;
  }
  
  function createArithmeticNode(current, cache) {
    var types = K.ArithNode;
    var result;
    var _type = types.getArithNodeType(current);
    switch(_type) {
      case types.CONSTANT:
      case types.IDENTIFIER:
        result = new K.ArithNode({type:_type, value:[current]});
        break;
      case types.NEGATIVE:
        result = new K.ArithNode({operation:current, value:[cache.pop()], type:_type});
        break;
      case types.EXPRESSION:
        result = new K.ArithNode({condition:cache.pop(), value:[cache.pop(),cache.pop()], type:_type});
        break;
      case types.OPERATION:
        result = new K.ArithNode({operation:current, value:[cache.pop(),cache.pop()], type:_type});
        break;
      default:
        console.error("ArithNode type not supported");
        throw 0;
    }
    return result;
  }
  
  function createStringNode(current, cache) {
    var StringNode = K.StringNode;
    var _type = K.StringNode.getStringNodeType(current);
    var result;
    switch(_type) {
      case StringNode.CONSTANT:
      case StringNode.IDENTIFIER:
        result = new K.StringNode({type:_type, value:[current]});
        break;
      default:
        console.error("StringNode type not supported");
        throw 0;
    }
    return result;
  }
  
  function generateTree(stack, dom, input) {
    var cache = [];
    var current;
    var result;
    while(stack.length > 0) {
      current = stack.shift();
      if (current === "Choice") {
         result = getCorrectExpression({condition:cache.pop(),value:[cache.pop(),cache.pop()]});
      } else if (current === "Plus") {
        result = getStringOrNumberFromPlus({operation:current, value:[cache.pop(), cache.pop()]});
      } else if (K.BooleanNode.isBooleanNode(current)) {
        result = createBooleanNode(current, cache);
      } else if (K.ArithNode.isArithNode(current)) {
        result = createArithmeticNode(current, cache);
      } else if (K.StringNode.isStringNode(current)) {
        result = createStringNode(current, cache);
      } else if (V.REGX_IDENT.test(current)) {
        // é variável
        console.error("Identifier not expected", current);
        throw 0;
      } else {
        console.error("Parse exception, token not found", current);
        throw 0;
      }
      cache.push(result);
      var stck = result.getStack()[0];
      if (stck!=current) {
        console.error(current, result.getStack()[0]);
        throw 0;
      }
    }
    cache[0].parent=dom;
    if (cache.length !== 1) {
      console.error("Parse exception. More than one root was found");
      throw 0;
    }
    cache[0].attachInput(input);
    return cache.pop();
  }
  
  function expressionChangedEvent(evt){
    var input=this[V.DT_INPT];
    putStackToInput(this[V.DT_CLASS],input);
    input.dispatchEvent(new Event("change"));
  }
  
  function putStackToInput(node,input){
    input.value=["[",node.getStack().reverse().toString(),"]"].join("");
  }
  
  function calculateValueTypes(obj) {
    var type = 0x0;
    if (obj.value[0] instanceof Node && obj.value[1] instanceof Node) {
      type = obj.value[0] | obj.value[1];
    }
    return type;
  }
  
  function getStringOrNumberFromPlus(obj) {
    var result;
    switch(calculateValueTypes(obj)) {
      case V.TYPE_STR|V.TYPE_STR:
      case V.TYPE_STR|V.TYPE_BOOL:
      case V.TYPE_STR|V.TYPE_NBR:
      case V.TYPE_NBR|V.TYPE_BOOL:
        obj.type = K.StringNode.OPERATION;
        result = new K.StringNode(obj);
        break;
      case V.TYPE_NBR|V.TYPE_NBR:
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
      case V.TYPE_BOOL|V.TYPE_BOOL:
        obj.type = K.BooleanNode.EXPRESSION;
        result = new K.BooleanNode(obj);
        break;
      case V.TYPE_NBR|V.TYPE_NBR:
        obj.type = K.ArithNode.EXPRESSION;
        result = new K.ArithNode(obj);
        break;
      case V.TYPE_STR|V.TYPE_STR:
      case V.TYPE_STR|V.TYPE_BOOL:
      case V.TYPE_STR|V.TYPE_NBR:
      case V.TYPE_NBR|V.TYPE_BOOL:
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
    getClass:function getClass() {
      implReqrd("getClass");
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
    clearVariables:{
      get:function(){return clearVariables;}
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
  
  function clearToolbars() {
    var tbrlst=document.getElementsByClassName(V.TOOLBAR);
    for(var i=0,l=tbrlst.length;i<l;i++) {
      var itm = tbrlst[i].parentNode;
      if (typeof itm[K._.DATA_TBR] !== K._.UNDEF) {
        itm[K._.DATA_TBR].clear();
      }
    }
  }
  
  function mouseEnterDOM(evt) {
    var tbrlst = document.getElementsByClassName(V.CSS_SEL_ND);
    for(var i=0,l=tbrlst.length;i<l;i++) {
      var itm = tbrlst[i];
      itm.classList.remove(V.CSS_SEL_ND);
    }
    var item = evt.target;
    var parent=item.parentNode;
    parent.classList.add(V.CSS_SEL_ND);
  }
  
  function mouseClickDOM(evt) {
    clearToolbars();
    var item = evt.target;
    var parent=item.parentNode;
    var obj=parent[V.DT_CLASS];
    if (obj instanceof Node) {
      obj.initToolbar();
      var tbr = parent[K._.DATA_TBR];
      if (tbr!==undefined) {
        tbr.draw(evt.layerX+10,evt.layerY-20);
      }
    }
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
    var img;
    if (/^src=[\"|\'].*[\",\']$/.test(text)){
      img=document.createElement("img");
      img.src=text.slice(5,text.length-1);
      dom.appendChild(img);
    }else{
      dom.appendChild(document.createTextNode(text));
    }
    for(var i=0,l=classes.length;i<l;i++) {
      dom.classList.add(classes[i]);
    }
    
    for(var key in data) {
      dom[["data",key].join("-")] = data[key] || "";
    }
    
    if (params.hasToolbar !== V.UNDEF && params.hasToolbar) {
      dom.addEventListener("click", mouseClickDOM);
      dom.addEventListener("mouseenter", mouseEnterDOM);
    }
    
    if (typeof mouseenter === "function") {
      dom.addEventListener("mouseenter", mouseenter);
    }
    if (typeof mouseleave === "function") {
      dom.addEventListener(V.MOUSE_LEAVE, mouseleave);
    }
    if (typeof click === "function") {
      dom.addEventListener("click", click);
    }
    
    if (typeof parent !== V.UNDEF && parent instanceof HTMLElement) {
      parent.appendChild(dom);
    }
    
    return dom;
  }
  
  function getMessage(label) {
    return ((K.messages || {})[navigator.language] || {})[label] || label;
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
    },
    _:{
      get:function(){return V;}
    },
    getMessage:{
      get:function(){return getMessage;}
    }
  });
  
})(window._parser = window._parser || {});