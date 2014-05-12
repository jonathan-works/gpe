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
    VAR:"Variáveis",
    TEXT_TYPE:"txt-cont",
    VERD:"VERDADEIRO",
    FALSO:"FALSO",
    ARIT:"0=0",
    STR_COMP:"''=''",
    EXPR:"true?true:false",
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
      getNodeType:{
        get:function() {
          return getNodeType;
        }
      },
      operation:{
        get:getOperation
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
      return new K.createDOM({type:params.type||V.LI, classes:params.classes||[V.ACTION], click:params.click||evtTst, text:params.text||"", parent:params.parent});
    }
    
    function initBoolOperToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, mouseLeave:mouseLeaveDOM, classes:[V.TOOLBAR, pvt.operation.name], parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:"NEGATE",click:clickNotEvent});
      MenuItem({parent:toolbar, text:"&&"});
      MenuItem({parent:toolbar, text:"||"});
    }
    
    function initArithComparOperToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, mouseLeave:mouseLeaveDOM, classes:[V.TOOLBAR, pvt.operation.name], parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:"NEGATE",click:clickNotEvent});
      MenuItem({parent:toolbar, text:"&&"});
      MenuItem({parent:toolbar, text:"||"});
      MenuItem({parent:toolbar, text:"=="});
      MenuItem({parent:toolbar, text:"!="});
      MenuItem({parent:toolbar, text:">="});
      MenuItem({parent:toolbar, text:">"});
      MenuItem({parent:toolbar, text:"<="});
      MenuItem({parent:toolbar, text:"<"});
    }
    
    function initNegToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, classes:[V.TOOLBAR, pvt.operation.name], mouseLeave:mouseLeaveDOM, parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:"NEGATE", click:clickNotEvent});
    }
    
    function initValueToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, classes:[V.TOOLBAR,V.VALUE], mouseLeave:mouseLeaveDOM, parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:"NEGATE", click:clickNotEvent});
      MenuItem({parent:toolbar, text:"&&"});
      MenuItem({parent:toolbar, text:"||"});
      
      var variable = K.createDOM({type:V.LI, parent:toolbar});
      
      K.createDOM({text:V.VAR,classes:[V.TEXT_TYPE], parent:variable});
      var variableMenu = K.createDOM({type:V.UL, parent:variable});
      
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.BOOLEAN);
      for(var i=0, l=variables.length;i<l;i++) {
        variableMenu.appendChild(MenuItem({text:variables[i]}));
      }
      MenuItem({parent:toolbar, text:V.VERD});
      MenuItem({parent:toolbar, text:V.FALSO});
      MenuItem({parent:toolbar, text:V.ARIT});
      MenuItem({parent:toolbar, text:V.STR_COMP});
      MenuItem({parent:toolbar, text:V.EXPR});
    }
    
    function clickNotEvent(evt) {
      switch(pvt.type) {
        case V.NOT:
          replaceWithChild(0);
          break;
        case V.CONSTANT:
          _this.clear();
          args.value = oppositeConstant(args.value);
          init(args);
          break;
        case V.IDENTIFIER:
        case V.EXPRESSION:
        case V.OPERATION:
          negate();
          break;
      }
    }
    
    function oppositeConstant(value) {
      var result;
      if (value === V.TRUE) {
        result = V.FALSE;
      } else if(value === V.FALSE){
        result = V.TRUE;
      }
      _this.clear();
      return result;
    }
    
    function setConstant(value) {
      pvt.childNodes.push(value);
      args.value=value;
      console.log(value, pvt.childNodes);
      if (value === V.TRUE) {
        renderValueDOM(V.VERD);
      } else if (value === V.FALSE) {
        renderValueDOM(V.FALSO);
      }
    }
    
    function negate() {
      var child = new BooleanNode(args);
      _this.clear();
      args.operation = "Not";
      args.type = V.NOT;
      args.value = child;
      init(args);
      child.parent = _this;
    }
    
    function replaceWithChild(numChild) {
      var child = pvt.childNodes[numChild];
      pvt.childNodes.splice(numChild,1);
      var _type = child.type;
      _this.clear();
      switch(_type) {
        case V.CONSTANT:
          init({type:child.type,value:child});
          break;
        case V.NOT:
          init({type:child.type,value:child});
          break;
        case V.IDENTIFIER:
          init({type:child.type,value:child.toString()});
          break;
        case V.EXPRESSION:
          //init({type:child.type,value:child.values[0]});
          break;
        case V.OPERATION:
          args.type = child.type;
          args.value = child.values;
          args.operation = child.operation.name;
          init(args);
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
        case K.BoolOper.AND.valueOf():
        case K.BoolOper.OR.valueOf():
          initBoolOperToolbar();
          break;
        case K.BoolOper.EQ.valueOf():
        case K.BoolOper.NEQ.valueOf():
        case K.BoolOper.GTE.valueOf():
        case K.BoolOper.GT.valueOf():
        case K.BoolOper.LTE.valueOf():
        case K.BoolOper.LT.valueOf():
          initArithComparOperToolbar();
          break;
      }
    }
    
    function setOperation(operation) {
      
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
      console.log(args);
      var dom = _super.getDOM();
      dom.classList.add(V.NAME);
      pvt.type = args.type;
      switch(pvt.type) {
        case V.OPERATION:
          pvt.operation = K.BoolOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          renderOperationDOM();
          console.log(_this.toString());
          break;
        case V.NOT:
          pvt.operation = K.BoolOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value);
          
          renderNegationDOM();
          break;
        case V.CONSTANT:
          setConstant(args.value);
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
    return K.BoolOper.isBoolOper(str) || (str.indexOf(V.IDENT_STR)===0 && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) || (str === V.TRUE || str === V.FALSE);
  }
  
  function getBooleanNodeType(str) {
    var type;
    var _ = K.Node;
    if (str.indexOf(V.IDENT_STR)===0 && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) {
      type = V.IDENTIFIER;
    } else if (str === V.TRUE || str === V.FALSE) {
      type = V.CONSTANT;
    } else if (K.BoolOper.isBoolOper(str)){
      type = K.BoolOper.getValueOf(str);
      if (type.ordinal === K.BoolOper.NOT.ordinal) {
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