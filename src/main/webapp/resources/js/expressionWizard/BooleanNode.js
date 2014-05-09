(function(K) {
  var V = {
    SELECTED:"selected",
    NAME:"BooleanNode",
    IDENTIFIER:"Identifier",
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
    FALSE:"False"
  };
  
  function mouseEnterDOM(evt) {
    var sel = document.getElementsByClassName(V.SELECTED);
    for(var i=0,l=sel.length;i<l;i++) {
      sel[i].classList.remove(V.SELECTED);
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
          result = [V.IDENTIFIER,"[",pvt.childNodes[0],"]"].join("");
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
      nodeType:{
        get:nodeType
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
      MenuItem({parent:toolbar, text:"!"});
      MenuItem({parent:toolbar, text:"&&"});
      MenuItem({parent:toolbar, text:"||"});
    }
    
    function initArithComparOperToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, mouseLeave:mouseLeaveDOM, classes:[V.TOOLBAR, pvt.operation.name], parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:"!"});
      MenuItem({parent:toolbar, text:"&&"});
      MenuItem({parent:toolbar, text:"=="});
      MenuItem({parent:toolbar, text:"!="});
      MenuItem({parent:toolbar, text:">="});
      MenuItem({parent:toolbar, text:">"});
      MenuItem({parent:toolbar, text:"<="});
      MenuItem({parent:toolbar, text:"<"});
    }
    
    function initNegToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, classes:[V.TOOLBAR, pvt.operation.name], mouseLeave:mouseLeaveDOM, parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:"||"});
      MenuItem({parent:toolbar, text:"&&"});
      MenuItem({parent:toolbar, text:"!"});
    }
    
    function initValueToolbar() {
      var toolbar = pvt.toolbar = K.createDOM({type:V.UL, classes:[V.TOOLBAR,V.VALUE], mouseLeave:mouseLeaveDOM, parent:_super.getDOM()});
      MenuItem({parent:toolbar, text:"&&"});
      MenuItem({parent:toolbar, text:"||"});
      MenuItem({parent:toolbar, text:"!"});
      
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
    
    (function Constructor() {
      var dom = _super.getDOM();
      dom.classList.add(V.NAME);
      switch(pvt.type) {
        case BooleanNode.OPERATION:
          pvt.operation = K.BoolOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          
          renderOperationDOM();
          break;
        case BooleanNode.NOT:
          pvt.operation = K.BoolOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value);
          
          renderNegationDOM();
          break;
        case BooleanNode.CONSTANT:
          pvt.childNodes.push(args.value);
          renderValueDOM(pvt.childNodes[0]===V.TRUE?V.VERD:V.FALSO);
          break;
        case BooleanNode.IDENTIFIER:
          pvt.childNodes.push(args.value);
          
          renderValueDOM(["[",pvt.childNodes[0],"]"].join(""));
          break;
        default:
          throw V.TYPE_EXCEP;
      }
    })();
  }
  
  function isBooleanNode(str) {
    var _ = K.Node;
    return K.BoolOper.isBoolOper(str) || (str.indexOf("Identifier[")===0 && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) || (str === V.TRUE || str === V.FALSE);
  }
  
  function getBooleanNodeType(str) {
    var type;
    var _ = K.Node;
    if (str.indexOf("Identifier[")===0 && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) {
      console.log("Identifier");
      type = BooleanNode.IDENTIFIER;
    } else if (str === V.TRUE || str === V.FALSE) {
      console.log("Constant");
      type = BooleanNode.CONSTANT;
    } else if (K.BoolOper.isBoolOper(str)){
      type = K.BoolOper.getValueOf(str);
      console.log("BoolOper",type.name);
      if (type.ordinal === K.BoolOper.NOT.ordinal) {
        type = BooleanNode.NOT;
      } else {
        type = BooleanNode.OPERATION;
      }
    } else {
      console.log("fail");
      type = 0x0;
    }
    return type;
  }
  
  BooleanNode.prototype = new K.Node();
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