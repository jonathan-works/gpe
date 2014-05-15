(function(K) {
  var V = {
    get NAME()"BooleanNode",
    get TRUE()"True",
    get FALSE()"False",
    get CONSTANT()0x1,
    get OPERATION()0x2,
    get IDENTIFIER()0x4,
    get NOT()0x8,
    get EXPRESSION()0x10
  };
  
  var _lbl={
    get negate    ()[V.NAME,"negate"].join("."),
    get and       ()[V.NAME,"and"].join("."),
    get or        ()[V.NAME,"or"].join("."),
    get eq        ()[V.NAME,"eq"].join("."),
    get neq       ()[V.NAME,"neq"].join("."),
    get gte       ()[V.NAME,"gte"].join("."),
    get gt        ()[V.NAME,"gt"].join("."),
    get lte       ()[V.NAME,"lte"].join("."),
    get lt        ()[V.NAME,"lt"].join("."),
    get var       ()[V.NAME,"var"].join("."),
    get TRUE      ()[V.NAME,"TRUE"].join("."),
    get FALSE     ()[V.NAME,"FALSE"].join("."),
    get ARIT      ()[V.NAME,"ARIT"].join("."),
    get STR_COMP  ()[V.NAME,"STR_COMP"].join("."),
    get EXPR      ()[V.NAME,"EXPR"].join(".")
  };
  
    
  function valueOf() {
    return K._.TYPE_BOOL;
  }
  
  function BooleanNode(args) {
    var _this = K.checkInit(this);
    var _super = new K.Node({parent:args.parent});
    
    args = args || {};
    var pvt = {
      childNodes : []
    };
    
    function getStack() {
      var result = [];
      var appendArray = function (itm) {
        result.push(itm);
      };
      switch(pvt.type) {
        case V.CONSTANT:
          result = [pvt.childNodes[0].slice(0,1).toUpperCase()+pvt.childNodes[0].slice(1,pvt.childNodes[0].length)];
          break;
        case V.OPERATION:
          result.push(pvt.operation.name);
          pvt.childNodes[0].getStack().forEach(appendArray);
          pvt.childNodes[1].getStack().forEach(appendArray);
          break;
        case V.IDENTIFIER:
          result = [[K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("")];
          break;
        case V.NOT:
          result.push(pvt.operation.name);
          pvt.childNodes[0].getStack().forEach(appendArray);
          break;
      }
      return result;
    }
    
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
          result = [K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("");
          break;
        case V.NOT:
          result = [pvt.operation.name, pvt.childNodes[0].toString()].join();
          break;
      }
      return result;
    }
    
    function clear() {
      pvt.childNodes = [];
      _super.clear();
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
    
    function getParent() {
      return _super.parent;
    }
    
    function setParent(itm) {
      args.parent = _super.parent = itm;
      clearToolbar();
      initToolbar();
    }
  
    Object.defineProperties(_this, {
      parent:{
        get:getParent, set:setParent
      },
      type:{
        get:getType
      },
      values:{
        get:getValues
      },
      operation:{
        get:getOperation
      },
      getStack:{
        get:function(){return getStack;}
      },
      clear:{
        get:function(){return clear;}
      },
      getDOM:{
        get:function(){return _super.getDOM}
      },
      toString:{
        get:function(){return toString;}
      },
      valueOf:{
        get:function(){return valueOf;}
      },
      getNodeType:{
        get:function(){return getNodeType;}
      },
      replaceWithChild:{
        get:function(){return replaceWithChild;}
      },
      initToolbar:{
        get:function(){return initToolbar;}
      },
      clearToolbar:{
        get:function(){return clearToolbar;}
      }
    });
    
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }
    
    function initBooleanOperToolbar() {
      var boolOp = K.BooleanOper;
      pvt.toolbar = new K.Toolbar({classes:[K._.TOOLBAR, pvt.operation.name], parent:_this.getDOM(), items:[
        {text:K.getMessage(_lbl.negate), click:clickNotEvent},
        {text:K.getMessage(_lbl.and), click:clickOperationEvent, data:{operation:boolOp.AND.name}},
        {text:K.getMessage(_lbl.or), click:clickOperationEvent, data:{operation:boolOp.OR.name}}
      ]});
    }
    
    function initArithComparOperToolbar() {
      var boolOp = K.BooleanOper;
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR, pvt.operation.name],items:[
        {text:K.getMessage(_lbl.negate), click:clickNotEvent},
        {text:K.getMessage(_lbl.and), click:clickOperationEvent, data:{operation:boolOp.AND.name}},
        {text:K.getMessage(_lbl.or), click:clickOperationEvent, data:{operation:boolOp.OR.name}},
        {text:K.getMessage(_lbl.eq), click:clickOperationEvent, data:{operation:boolOp.EQ.name}},
        {text:K.getMessage(_lbl.neq), click:clickOperationEvent, data:{operation:boolOp.NEQ.name}},
        {text:K.getMessage(_lbl.gte), click:clickOperationEvent, data:{operation:boolOp.GTE.name}},
        {text:K.getMessage(_lbl.gt), click:clickOperationEvent, data:{operation:boolOp.GT.name}},
        {text:K.getMessage(_lbl.lte), click:clickOperationEvent, data:{operation:boolOp.LTE.name}},
        {text:K.getMessage(_lbl.lt), click:clickOperationEvent, data:{operation:boolOp.LT.name}}
      ]});
    }
    
    function initNegToolbar() {
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR, pvt.operation.name],items:[
        {text:K.getMessage(_lbl.negate), click:clickNotEvent}
      ]});
    }
    
    function initValueToolbar() {
      var boolOp = K.BooleanOper;
      var tbarItems = [
        {text:K.getMessage(_lbl.negate), click:clickNotEvent},
        {text:K.getMessage(_lbl.and), click:clickOperationEvent, data:{operation:boolOp.AND.name}},
        {text:K.getMessage(_lbl.or), click:clickOperationEvent, data:{operation:boolOp.OR.name}},
        {text:"-", classes:[]}
      ];
      tbarItems.push(getVariableSubMenu());
      tbarItems.push({text:K.getMessage(_lbl.TRUE), click:clickConstantEvent, data:{type:V.TRUE}});
      tbarItems.push({text:K.getMessage(_lbl.FALSE), click:clickConstantEvent, data:{type:V.FALSE}});
      tbarItems.push({text:K.getMessage(_lbl.ARIT), click:clickOperationEvent, data:{operation:boolOp.EQ.name}});
      
      if (getParent() instanceof BooleanNode) {
        tbarItems.push({parent:toolbar, text:K.getMessage(_lbl.OVERRIDE), click:clickOverrideParentEvent});
      }
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR,K._.VALUE],items:tbarItems});
    }
    
    function getVariableSubMenu() {
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.BOOLEAN);
      var items = [];
      for(var i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:clickVariableEvent, data:{varname:variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE], items:items, text:K.getMessage(_lbl.var)});
    }
    
    function clickOverrideParentEvent(evt) {
      if (getParent() instanceof K.Node) {
        if (getParent() instanceof BooleanNode) {
          getParent().replaceWithChild(getParent().values.indexOf(_this));
        }
      } else {
        alert("parent is not a Node type");
      }
    }
    
    function clickVariableEvent(evt) {
      var varId = [K._.IDENT_STR,"[",evt.target["data-varname"],"]"].join("");
      changeToValue(varId, V.IDENTIFIER);
    }
    
    function clickConstantEvent(evt) {
      changeToValue(evt.target["data-type"]);
    }
    
    function clickOperationEvent(evt) {
      var _ = K.BooleanOper;
      var oper = _.getValueOf(evt.target["data-operation"]);
      switch(oper) {
        case _.AND:
        case _.OR:
          changeToBooleanOperation(oper);
          break;
        case _.GT:
        case _.GTE:
        case _.LT:
        case _.LTE:
        case _.EQ:
        case _.NEQ:
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
      clear();
      init(args = {type:_type || V.CONSTANT, parent:getParent(), value:_value});
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
    
    function changeToArithOperation(oper) {
      var children=[];
      var childArgs;
      var _B = K.BooleanOper;
      
      function createChildNodes() {
        // CONSTRUCT TWO CHILD CONSTANT NODES AND CHANGE THIS
          childArgs = {type:K.ArithNode.CONSTANT, value:"0", parent:_this};
          children.push(new K.ArithNode(childArgs));
          children.push(new K.ArithNode(childArgs));
          clear();
          init(args = {operation:oper.name, type:V.OPERATION, value:children, parent:getParent()});
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
          switch(pvt.operation) {
            case _B.AND:
            case _B.OR:
              createChildNodes();
              break;
            case _B.GT:
            case _B.GTE:
            case _B.LT:
            case _B.LTE:
            case _B.EQ:
            case _B.NEQ:
              clear();
              init(args = {operation:oper.name, type:args.type, value:args.value, parent:getParent()});
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
          clear();
          init(args = {operation:oper.name, type:V.OPERATION, value:children, parent:getParent()});
          break;
        case V.NOT:
          // CONSTRUCT ANOTHER CHILD CONSTANT NODE AND CHANGE THIS
          break;
        case V.OPERATION:
          switch(pvt.operation) {
            case _B.AND:
            case _B.OR:
              clear();
              init(args = {operation:oper.name, type:args.type, value:args.value, parent:getParent()});
              break;
            case _B.GT:
            case _B.GTE:
            case _B.LT:
            case _B.LTE:
            case _B.EQ:
            case _B.NEQ:
              children = createChildren({ type:V.CONSTANT, value:V.TRUE, parent:_this });
              clear();
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
      var parent = getParent();
      if (parent.type === V.NOT) {
        parent.replaceWithChild(0);
      } else {
        var childArgs = args;
        childArgs.parent = _this;
        var child = new BooleanNode(childArgs);
        
        clear();
        args = {operation:K.BooleanOper.NOT.name, type:V.NOT, value:child};
        init(args);
      }
    }
    
    function replaceWithChild(numChild) {
      var child = pvt.childNodes[numChild];
      pvt.childNodes.splice(numChild,1);
      var _type = child.type;
      clear();
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
    
    function clearToolbar() {
      if (pvt.toolbar) {
        pvt.toolbar.clear();
        delete pvt.toolbar;
      }
    }
    
    function initToolbar() {
      var _ = K.BooleanOper;
      switch(pvt.type) {
        case V.NOT:
          initNegToolbar();
          break;
        case V.CONSTANT:
        case V.IDENTIFIER:
          initValueToolbar();
          break;
        case V.OPERATION:
          switch(pvt.operation) {
            case _.AND:
            case _.OR:
              initBooleanOperToolbar();
              break;
            case _.EQ:
            case _.NEQ:
            case _.GTE:
            case _.GT:
            case _.LTE:
            case _.LT:
              initArithComparOperToolbar();
              break;
          }
          break;
      }
      _this.getDOM()["data-toolbar"] = pvt.toolbar;
    }
    
    function renderOperationDOM() {
      var dom = _this.getDOM();
      dom.classList.add(pvt.operation.name);
      
      K.createDOM({text:"(", classes:[K._.TEXT], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[0]);
      K.createDOM({text:pvt.operation.label, classes:[K._.TEXT, K._.OPER], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[1]);
      K.createDOM({text:")", classes:[K._.TEXT] , parent:dom, hasToolbar:true});
    }
    
    function renderNegationDOM() {
      var dom = _this.getDOM();
      dom.classList.add(pvt.operation.name);
      K.createDOM({text:pvt.operation.label, classes:[K._.TEXT, K._.OPER], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[0]);
    }
    
    function renderValueDOM(text) {
      var dom = _this.getDOM();
      dom.classList.add(K._.VALUE);
      
      K.createDOM({text:text, classes:[V.NAME,K._.TEXT, K._.VALUE], parent:dom, hasToolbar:true});
    }
    
    function init(args) {
      var dom = _this.getDOM();
      dom.classList.add(V.NAME);
      var obj = dom[K._.DT_CLASS] = _this;
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
            renderValueDOM(K.getMessage(_lbl.TRUE));
          } else if (args.value === V.FALSE) {
            renderValueDOM(K.getMessage(_lbl.FALSE));
          }
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(args.value.slice(11,args.value.length-1));
          
          renderValueDOM(["[",pvt.childNodes[0],"]"].join(""));
          break;
        default:
          throw V.TYPE_EXCEP;
      }
      initToolbar();
    }
    
    init(args);
  }
  
  function isBooleanNode(str) {
    var _ = K.Node;
    return K.BooleanOper.isBooleanOper(str) || (K._.REGX_IDENT.test(str) && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) || (str === V.TRUE || str === V.FALSE);
  }
  
  function getBooleanNodeType(str) {
    var type;
    var _ = K.Node;
    if (K._.REGX_IDENT.test(str) && _.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0) {
      type = V.IDENTIFIER;
    } else if (str === V.TRUE || str === V.FALSE) {
      type = V.CONSTANT;
    } else if (K.BooleanOper.isBooleanOper(str)){
      type = K.BooleanOper.getValueOf(str);
      if (type === K.BooleanOper.NOT) {
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
      get:function(){return V.CONSTANT;}
    },
    OPERATION:{
      get:function(){return V.OPERATION;}
    },
    IDENTIFIER:{
      get:function(){return V.IDENTIFIER;}
    },
    NOT:{
      get:function(){return V.NOT;}
    },
    EXPRESSION:{
      get:function(){return V.EXPRESSION;}
    },
    isBooleanNode:{
      get:function(){return isBooleanNode;}
    },
    getBooleanNodeType:{
      get:function(){return getBooleanNodeType;}
    }
  });
  
  Object.defineProperties(K,{
    BooleanNode:{
      get:function(){return BooleanNode;}
    }
  });
  
})(window._parser);