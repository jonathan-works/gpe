(function(K) {
  var V = {
    SELECTED:"selected",
    NAME:"BooleanNode",
    IDENT_STR:"Identifier",
    LI:"li",
    UL:"ul",
    ACTION:"Action",
    TOOLBAR:"toolbar",
    MOUSELEAVE:"mouseleave",
    VALUE:"Value",
    TEXT_TYPE:"txt-cont",
    TEXT:"Text",
    OPER:"Operator",
    TRUE:"True",
    FALSE:"False",
    CONSTANT:0x1,
    OPERATION:0x2,
    IDENTIFIER:0x3,
    NOT:0x4,
    EXPRESSION:0x5
  };
  
  var _M={};
  
  _M.inst = (_M.inst = (_M.inst = K.messages || {})[navigator.language] || {}).BooleanNode || {
    negate    :[V.NAME,"negate"].join("."),
    and       :[V.NAME,"and"].join("."),
    or        :[V.NAME,"or"].join("."),
    eq        :[V.NAME,"eq"].join("."),
    neq       :[V.NAME,"neq"].join("."),
    gte       :[V.NAME,"gte"].join("."),
    gt        :[V.NAME,"gt"].join("."),
    lte       :[V.NAME,"lte"].join("."),
    lt        :[V.NAME,"lt"].join("."),
    var       :[V.NAME,"var"].join("."),
    TRUE      :[V.NAME,"TRUE"].join("."),
    FALSE     :[V.NAME,"FALSE"].join("."),
    ARIT      :[V.NAME,"ARIT"].join("."),
    STR_COMP  :[V.NAME,"STR_COMP"].join("."),
    EXPR      :[V.NAME,"EXPR"].join(".")
  };
  
  function mouseEnterDOM(evt) {
    var sel = document.getElementsByClassName(V.SELECTED);
    for(var i=0,l=sel.length;i<l;i++) {
      sel[i].classList.remove(V.SELECTED);
    }
    var tbr = document.getElementsByClassName(V.TOOLBAR);
    for(i=0, l=tbr.length;i<l;i++) {
      tbr[i].style.left = [evt.layerX+5,"px"].join("");
      tbr[i].style.top = [evt.layerY+5,"px"].join("");
      
    }
    var parent = evt.target.parentNode;
    parent.classList.add(V.SELECTED);
  }
  
  function mouseLeaveDOM(evt) {
    evt.target.parentNode.classList.remove(V.SELECTED);
  }
  
  function BooleanNode(args) {
    var _this = K.checkInit(this);
    var _super = new K.Node({parent:args.parent});
    
    args = args || {};
    var pvt = {
      childNodes : []
    };

    function toString() {
      var result="";
      switch(pvt.type) {
        case V.CONSTANT:
          result = pvt.childNodes[0].slice(0,1).toUpperCase()+pvt.childNodes[0].slice(1,pvt.childNodes[0].length);
          break;
        case V.OPERATION:
          result = [pvt.operation.name, pvt.childNodes[0], pvt.childNodes[1]].join();
          break;
        case V.IDENTIFIER:
          result = [V.IDENT_STR,"[",pvt.childNodes[0],"]"].join("");
          break;
        case V.NOT:
          result = [pvt.operation.name, pvt.childNodes[0].toString()].join();
          break;
      }
      return result;
    }
    
    function valueOf() {
      return pvt.type;
    }
    
    function clear() {
      pvt.childNodes = [];
      _super.clear();
    }
    
    function getDOM() {
    }
    
    function getType() {
      return pvt.type;
    }
    
    function getOperation() {
      return pvt.operation;
    }
    
    function getValues() {
      return pvt.childNodes.slice(0, pvt.childNodes.length);
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
          args.parent = _super.parent = itm;
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
      getNodeType:{
        get:function() {
          return getNodeType;
        }
      },
      operation:{
        get:getOperation
      },
      detachChildren:{
        get:function () {
          return detachChildren;
        }
      },
      replaceWithChild:{
        get:function() {
          return replaceWithChild;
        }
      }
    });
    
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }
    
    function Action(params) {
      return K.createDOM({
        type:V.LI,
        text:params.text,
        classes:[V.ACTION],
        click:function(evt) {
          alert(evt.target.textContent);
        }
      });
    }
    
    function evtTst(evt) {
      alert(evt.target.textContent);
    }
    
    function getToolBar(params) {
      
    }
    
    function MenuItem(params) {
      return new K.createDOM({type:params.type||V.LI, classes:params.classes||[V.ACTION], click:params.click||evtTst, text:params.text||"", parent:params.parent, data:params.data||{}});
    }
    
    function initBooleanOperToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, mouseLeave:mouseLeaveDOM, classes:[V.TOOLBAR, pvt.operation.name], parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:_M.inst.negate, click:clickNotEvent});
      MenuItem({parent:toolbar, text:_M.inst.and, click:clickOperationEvent, data:{operation:K.BooleanOper.AND.name}});
      MenuItem({parent:toolbar, text:_M.inst.or, click:clickOperationEvent, data:{operation:K.BooleanOper.OR.name}});
    }
    
    function initArithComparOperToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, mouseLeave:mouseLeaveDOM, classes:[V.TOOLBAR, pvt.operation.name], parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:_M.inst.negate,click:clickNotEvent});
      MenuItem({parent:toolbar, text:_M.inst.and, click:clickOperationEvent, data:{operation:K.BooleanOper.AND.name}});
      MenuItem({parent:toolbar, text:_M.inst.or, click:clickOperationEvent, data:{operation:K.BooleanOper.OR.name}});
      
      MenuItem({parent:toolbar, text:_M.inst.eq, click:clickOperationEvent, data:{operation:K.BooleanOper.EQ.name}});
      MenuItem({parent:toolbar, text:_M.inst.neq, click:clickOperationEvent, data:{operation:K.BooleanOper.NEQ.name}});
      MenuItem({parent:toolbar, text:_M.inst.gte, click:clickOperationEvent, data:{operation:K.BooleanOper.GTE.name}});
      MenuItem({parent:toolbar, text:_M.inst.gt, click:clickOperationEvent, data:{operation:K.BooleanOper.GT.name}});
      MenuItem({parent:toolbar, text:_M.inst.lte, click:clickOperationEvent, data:{operation:K.BooleanOper.LTE.name}});
      MenuItem({parent:toolbar, text:_M.inst.lt, click:clickOperationEvent, data:{operation:K.BooleanOper.LT.name}});
    }
    
    function initNegToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, classes:[V.TOOLBAR, pvt.operation.name], mouseLeave:mouseLeaveDOM, parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:_M.inst.negate, click:clickNotEvent});
    }
    
    function initValueToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, classes:[V.TOOLBAR,V.VALUE], mouseLeave:mouseLeaveDOM, parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:_M.inst.negate, click:clickNotEvent});
      MenuItem({parent:toolbar, text:_M.inst.and, click:clickOperationEvent, data:{operation:K.BooleanOper.AND.name}});
      MenuItem({parent:toolbar, text:_M.inst.or, click:clickOperationEvent, data:{operation:K.BooleanOper.OR.name}});
      
      var variable = K.createDOM({type:V.LI, parent:toolbar});
      
      K.createDOM({text:_M.inst.var,classes:[V.TEXT_TYPE], parent:variable});
      var variableMenu = K.createDOM({type:V.UL, parent:variable});
      
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.BOOLEAN);
      for(var i=0, l=variables.length;i<l;i++) {
        variableMenu.appendChild(MenuItem({text:variables[i], click:clickVariableEvent, data:{varname:variables[i]}}));
      }
      MenuItem({parent:toolbar, text:_M.inst.TRUE, click:clickConstantEvent, data:{type:V.TRUE}});
      MenuItem({parent:toolbar, text:_M.inst.FALSE, click:clickConstantEvent, data:{type:V.FALSE}});
      MenuItem({parent:toolbar, text:_M.inst.ARIT, click:clickOperationEvent, data:{operation:K.BooleanOper.EQ.name}});
      
      MenuItem({parent:toolbar, text:_M.inst.OVERRIDE, click:clickOverrideParentEvent});
    }
    
    function clickOverrideParentEvent(evt) {
      if (_this.parent instanceof K.Node) {
        if (_this.parent instanceof BooleanNode) {
          _this.parent.replaceWithChild(_this.parent.values.indexOf(_this));
        }
      } else {
        alert("parent is not a Node type");
      }
    }
    
    function clickVariableEvent(evt) {
      var varId = [V.IDENT_STR,"[",evt.target["data-varname"],"]"].join("");
      changeToValue(varId, V.IDENTIFIER);
    }
    
    function clickConstantEvent(evt) {
      changeToValue(evt.target["data-type"]);
    }
    
    function clickOperationEvent(evt) {
      var _ = K.BooleanOper;
      var oper = _.getValueOf(evt.target["data-operation"]);
      switch(oper.ordinal) {
        case _.AND.ordinal:
        case _.OR.ordinal:
          changeToBooleanOperation(oper);
          break;
        case _.GT.ordinal:
        case _.GTE.ordinal:
        case _.LT.ordinal:
        case _.LTE.ordinal:
        case _.EQ.ordinal:
        case _.NEQ.ordinal:
          changeToArithOperation(oper);
          break;
      }
    }
    
    function clickNotEvent(evt) {
      switch(pvt.type) {
        case V.NOT:
          replaceWithChild(0);
          break;
        case V.CONSTANT:
          invertConstantValue();
          break;
        case V.IDENTIFIER:
        case V.EXPRESSION:
        case V.OPERATION:
          negate();
          break;
      }
    }
    
    function changeToValue(_value, _type) {
      _this.clear();
      init(args = {type:_type || V.CONSTANT, parent:_this.parent, value:_value});
    }
    
    function invertConstantValue() {
      var value = args.value;
      if (value === V.TRUE) {
        value = V.FALSE;
      } else if(value === V.FALSE){
        value = V.TRUE;
      }
      changeToValue(value);
    }
    
    function detachChildren() {
      var children = pvt.childNodes;
      var dom;
      for(var i=0, l=children.length,item; i<l; i++,item=children[i]) {
        if (item instanceof K.Node) {
          if (item instanceof BooleanNode) {
            item.detachChildren();
          }
          item.clear();
          dom = item.getDOM();
          dom.parentNode.removeChild(dom);
        }
      }
    }
    
    function changeToArithOperation(oper) {
      var children=[];
      var childArgs;
      var _B = K.BooleanOper;
      
      function createChildNodes() {
        // CONSTRUCT TWO CHILD CONSTANT NODES AND CHANGE THIS
          childArgs = {type:K.ArithNode.CONSTANT, value:"0", parent:_this};
          children.push(new K.ArithNode(childArgs));
          children.push(new K.ArithNode(childArgs));
          _this.clear();
          init(args = {operation:oper.name, type:V.OPERATION, value:children, parent:_this.parent});
      }
      
      switch(pvt.type) {
        case V.CONSTANT:
        case V.IDENTIFIER:
          createChildNodes();
          break;
        case V.NOT:
          // CONSTRUCT ANOTHER CHILD CONSTANT NODE AND CHANGE THIS
          break;
        case V.OPERATION:
          switch(pvt.operation.ordinal) {
            case _B.AND.ordinal:
            case _B.OR.ordinal:
              createChildNodes();
              break;
            case _B.GT.ordinal:
            case _B.GTE.ordinal:
            case _B.LT.ordinal:
            case _B.LTE.ordinal:
            case _B.EQ.ordinal:
            case _B.NEQ.ordinal:
              _this.clear();
              init(args = {operation:oper.name, type:args.type, value:args.value, parent:_this.parent});
              break;
          }
          // SWAP OPERATOR AND CHILD NODES IF ARITHMETIC OPERATOR
          // SWAP OPERATOR AND CHILD NODES IF STRING COMPARATION
          break;
        case V.EXPRESSION:
          break;
      }
    }
    
    function changeToBooleanOperation(oper) {
      var children=[];
      var childArgs;
      var _B = K.BooleanOper;
      
      function createChildren(childArgs) {
        children.push(new BooleanNode(childArgs));
        children.push(new BooleanNode(childArgs));
        return children;
      }
      
      switch(pvt.type) {
        case V.CONSTANT:
        case V.IDENTIFIER:
          // CONSTRUCT TWO CHILD CONSTANT NODES AND CHANGE THIS
          children = createChildren(args);
          _this.clear();
          init(args = {operation:oper.name, type:V.OPERATION, value:children, parent:_this.parent});
          break;
        case V.NOT:
          // CONSTRUCT ANOTHER CHILD CONSTANT NODE AND CHANGE THIS
          break;
        case V.OPERATION:
          switch(pvt.operation.ordinal) {
            case _B.AND.ordinal:
            case _B.OR.ordinal:
              _this.clear();
              init(args = {operation:oper.name, type:args.type, value:args.value, parent:_this.parent});
              break;
            case _B.GT.ordinal:
            case _B.GTE.ordinal:
            case _B.LT.ordinal:
            case _B.LTE.ordinal:
            case _B.EQ.ordinal:
            case _B.NEQ.ordinal:
              children = createChildren({ type:V.CONSTANT, value:V.TRUE, parent:_this });
              _this.clear();
              init(args = {operation:oper.name, type:V.OPERATION, value:children});
              break;
          }
          // SWAP OPERATOR AND CHILD NODES IF ARITHMETIC OPERATOR
          // SWAP OPERATOR AND CHILD NODES IF STRING COMPARATION
          break;
        case V.EXPRESSION:
          break;
      }
    }
    
    function negate() {
      if (_this.parent.type === V.NOT) {
        _this.parent.replaceWithChild(0);
      } else {
        var childArgs = args;
        childArgs.parent = _this;
        var child = new BooleanNode(childArgs);
        
        _this.clear();
        args = {operation:K.BooleanOper.NOT.name, type:V.NOT, value:child};
        init(args);
      }
    }
    
    function replaceWithChild(numChild) {
      var child = pvt.childNodes[numChild];
      pvt.childNodes.splice(numChild,1);
      var _type = child.type;
      _this.clear();
      switch(_type) {
        case V.CONSTANT:
          init(args = {type:child.type, value:child.toString(), parent:_this});
          break;
        case V.NOT:
          init(args = {type:child.type,value:child, parent:_this});
          break;
        case V.IDENTIFIER:
          init(args = {type:child.type,value:child.toString(), parent:_this});
          break;
        case V.EXPRESSION:
          //init({type:child.type,value:child.values[0]});
          break;
        case V.OPERATION:
          init(args = {type:child.type, value:child.values, operation:child.operation.name, parent:_this});
          break;
      }
      child.clear();
    }
    
    function renderOperationDOM() {
      var dom = _super.getDOM();
      dom.classList.add(pvt.operation.name);
      
      K.createDOM({text:"(", classes:[V.TEXT], mouseEnter:mouseEnterDOM, parent:dom});
      updateParent(pvt.childNodes[0]);
      K.createDOM({text:pvt.operation.label, classes:[V.TEXT, V.OPER], mouseEnter:mouseEnterDOM, parent:dom});
      updateParent(pvt.childNodes[1]);
      K.createDOM({text:")", classes:[V.TEXT], mouseEnter:mouseEnterDOM, parent:dom});
      
      switch(pvt.operation.valueOf()) {
        case K.BooleanOper.AND.valueOf():
        case K.BooleanOper.OR.valueOf():
          initBooleanOperToolbar();
          break;
        case K.BooleanOper.EQ.valueOf():
        case K.BooleanOper.NEQ.valueOf():
        case K.BooleanOper.GTE.valueOf():
        case K.BooleanOper.GT.valueOf():
        case K.BooleanOper.LTE.valueOf():
        case K.BooleanOper.LT.valueOf():
          initArithComparOperToolbar();
          break;
      }
    }
    
    function renderNegationDOM() {
      var dom = _super.getDOM();
      dom.classList.add(pvt.operation.name);
      K.createDOM({text:pvt.operation.label, classes:[V.TEXT, V.OPER], mouseEnter:mouseEnterDOM, parent:dom});
      updateParent(pvt.childNodes[0]);
      initNegToolbar();
    }
    
    function renderValueDOM(text) {
      var dom = _super.getDOM();
      dom.classList.add(V.VALUE);
      K.createDOM({text:text, classes:[V.NAME,V.TEXT, V.VALUE], mouseEnter:mouseEnterDOM, parent:dom});
      initValueToolbar();
    }
    
    function init(args) {
      var dom = _super.getDOM();
      dom.classList.add(V.NAME);
      pvt.type = args.type;
      switch(pvt.type) {
        case V.OPERATION:
          pvt.operation = K.BooleanOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          renderOperationDOM();
          break;
        case V.NOT:
          pvt.operation = K.BooleanOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value);
          
          renderNegationDOM();
          break;
        case V.CONSTANT:
          pvt.childNodes.push(args.value);
          
          if (args.value === V.TRUE) {
            renderValueDOM(_M.inst.TRUE);
          } else if (args.value === V.FALSE) {
            renderValueDOM(_M.inst.FALSE);
          }
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(args.value.slice(11,args.value.length-1));
          
          renderValueDOM(["[",pvt.childNodes[0],"]"].join(""));
          break;
        default:
          throw V.TYPE_EXCEP;
      }
    }
    
    init(args);
  }
  
  function isBooleanNode(str) {
    var _ = K.Node;
    return K.BooleanOper.isBooleanOper(str) || (str.indexOf(V.IDENT_STR)===0 && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) || (str === V.TRUE || str === V.FALSE);
  }
  
  function getBooleanNodeType(str) {
    var type;
    var _ = K.Node;
    if (str.indexOf(V.IDENT_STR)===0 && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) {
      type = V.IDENTIFIER;
    } else if (str === V.TRUE || str === V.FALSE) {
      type = V.CONSTANT;
    } else if (K.BooleanOper.isBooleanOper(str)){
      type = K.BooleanOper.getValueOf(str);
      if (type.ordinal === K.BooleanOper.NOT.ordinal) {
        type = V.NOT;
      } else {
        type = V.OPERATION;
      }
    } else {
      type = 0x0;
    }
    return type;
  }
  
  BooleanNode.prototype = new K.Node();
  /*    CONSTANTES DA CLASSE    */
  Object.defineProperties(BooleanNode, {
    CONSTANT:{
      get:function() {
        return V.CONSTANT;
      }
    },
    OPERATION:{
      get:function() {
        return V.OPERATION;
      }
    },
    IDENTIFIER:{
      get:function() {
        return V.IDENTIFIER;
      }
    },
    NOT:{
      get:function() {
        return V.NOT;
      }
    },
    EXPRESSION:{
      get:function() {
        return V.EXPRESSION;
      }
    },
    isBooleanNode:{
      get:function() {
        return isBooleanNode;
      }
    },
    getBooleanNodeType:{
      get:function() {
        return getBooleanNodeType;
      }
    }
  });
  
  Object.defineProperties(K,{
    BooleanNode:{
      get:function() {
        return BooleanNode;
      }
    }
  });
})(window._parser);