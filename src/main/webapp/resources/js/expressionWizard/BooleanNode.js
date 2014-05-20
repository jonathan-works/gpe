(function(K) {
  var V = {
    get NAME()"BooleanNode",
    get TRUE()"True",
    get FALSE()"False",
    get CONSTANT()0x1,
    get OPERATION()0x2,
    get IDENTIFIER()0x4,
    get NOT()0x8,
    get EXPRESSION()0x10,
    get TYPE_EXCEP()"Type Exception"
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
    get EXPR      ()[V.NAME,"EXPR"].join("."),
    get OVERRIDE  ()[V.NAME,"OVERRIDE"].join(".")
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
      initToolbar:{
        get:function(){return initToolbar;}
      },
      clearToolbar:{
        get:function(){return clearToolbar;}
      },
      replaceParent:{
        get:function(){return replaceParent;}
      },
      replaceChild:{
        get:function(){return replaceChild;}
      }
    });
    
    function replaceParent() {
      var _parent = getParent();
      var _gParent;
      if(_parent instanceof BooleanNode){
        _gParent=_parent.parent;
        setParent(_gParent);
        if(_gParent instanceof K.Node){
          _gParent.replaceChild(_parent,_this);
        }else if(_gParent instanceof Element){
          _gParent.replaceChild(_this.getDOM(),_parent.getDOM());
        }
      }
    }
    
    function replaceChild(_old,_new) {
      var pos=pvt.childNodes.indexOf(_old);
      if (pos<0) {
        console.error("");
        throw 0;
      }
      if (!_new instanceof BooleanNode) {
        console.error("");
        throw 0;
      }
      pvt.childNodes[pos]=_new;
      _this.getDOM().replaceChild(_new.getDOM(),_old.getDOM());
    }
    
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }
    
    function initBooleanOperToolbar() {
      var boolOp = K.BooleanOper;
      pvt.toolbar = new K.Toolbar({classes:[K._.TOOLBAR, pvt.operation.name], parent:_this.getDOM(), items:[
        {text:K.getMessage(_lbl.negate), click:genericClickEvent, data:{type:V.NOT}},
        {text:K.getMessage(_lbl.and), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.AND}},
        {text:K.getMessage(_lbl.or), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.OR}}
      ]});
    }
    
    function initArithComparOperToolbar() {
      var boolOp = K.BooleanOper;
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR, pvt.operation.name],items:[
        {text:K.getMessage(_lbl.negate), click:genericClickEvent, data:{type:V.NOT}},
        {text:K.getMessage(_lbl.and), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.AND}},
        {text:K.getMessage(_lbl.or), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.OR}},
        {text:K.getMessage(_lbl.eq), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.EQ}},
        {text:K.getMessage(_lbl.neq), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.NEQ}},
        {text:K.getMessage(_lbl.gte), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.GTE}},
        {text:K.getMessage(_lbl.gt), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.GT}},
        {text:K.getMessage(_lbl.lte), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.LTE}},
        {text:K.getMessage(_lbl.lt), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.LT}}
      ]});
    }
    
    function initNegToolbar() {
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR, pvt.operation.name],items:[
        {text:K.getMessage(_lbl.negate), click:genericClickEvent, data:{type:V.NOT}}
      ]});
    }
    
    function initValueToolbar() {
      var boolOp = K.BooleanOper;
      var tbarItems = [
        {text:K.getMessage(_lbl.negate), click:genericClickEvent, data:{type:V.NOT}},
        {text:K.getMessage(_lbl.and), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.AND}},
        {text:K.getMessage(_lbl.or), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.OR}},
        {text:"-", classes:[]}
      ];
      tbarItems.push(getVariableSubMenu());
      tbarItems.push({text:K.getMessage(_lbl.TRUE), click:genericClickEvent, data:{type:V.CONSTANT,value:V.TRUE}});
      tbarItems.push({text:K.getMessage(_lbl.FALSE), click:genericClickEvent, data:{type:V.CONSTANT,value:V.FALSE}});
      tbarItems.push({text:K.getMessage(_lbl.ARIT), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.EQ}});
      
      if (getParent() instanceof BooleanNode) {
        tbarItems.push({parent:toolbar, text:K.getMessage(_lbl.OVERRIDE), click:genericClickEvent, data:{type:"override",value:_this}});
      }
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR,K._.VALUE],items:tbarItems});
    }
    
    function getVariableSubMenu() {
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.BOOLEAN);
      var items = [];
      for(var i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:genericClickEvent, data:{type:V.IDENTIFIER,value:variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE], items:items, text:K.getMessage(_lbl.var)});
    }
    
    function genericClickEvent(evt) {
      var result = "";
      var dtType = evt.target["data-type"];
      var _ = K.BooleanOper;
      switch(dtType) {
        case V.OPERATION:
          setOperation(evt.target[K._.DATA_OPER]);
          break;
        case V.IDENTIFIER:
          setIdentifier([K._.IDENT_STR,"[",evt.target["data-value"],"]"].join(""));
          break;
        case V.CONSTANT:
          setConstant(evt.target["data-value"]);
          break;
        case V.NOT:
          negate();
          break;
        case "override":
          evt.target["data-value"].replaceParent();
          break;
        default:
          break;
      }
    }
    
    function setOperation(oper) {
      var _ = K.BooleanOper;
      switch(oper) {
        case _.AND:
        case _.OR:
          setBooleanOperation(oper);
          break;
        case _.GT:
        case _.GTE:
        case _.LT:
        case _.LTE:
        case _.EQ:
        case _.NEQ:
          setArithOperation(oper);
          break;
      }
    }
    
    function setConstant(_value) {
      clear();
      init(args={type:V.CONSTANT,parent:getParent(),value:_value});
    }
    
    function setIdentifier(_value) {
      clear();
      init(args={type:V.IDENTIFIER,parent:getParent(),value:_value});
    }
    
    function setArithOperation(oper) {
      var children=[];
      var childArgs;
      var _B = K.BooleanOper;
      
      function createChildNodes() {
        // CONSTRUCT TWO CHILD CONSTANT NODES AND CHANGE THIS
          var childArgs = {type:K.ArithNode.CONSTANT, value:"Integer[0]", parent:_this};
          clear();
          init(args = {operation:oper.name, type:V.OPERATION, value:[new K.ArithNode(childArgs),new K.ArithNode(childArgs)], parent:getParent()});
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
    
    function setBooleanOperation(oper) {
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
      if(pvt.type===V.CONSTANT) {
        var value = pvt.childNodes[0];
        clear();
        if(value === V.TRUE) {
          value = V.FALSE;
        }else if(value===V.FALSE){
          value = V.TRUE;
        }
        setConstant(value);
      }else if (getParent().type===V.NOT) {
        replaceParent();
      }else{
        clear();
        init(args={operation:K.BooleanOper.NOT.name,type:V.NOT,value:new BooleanNode(args)});
      }
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
      _this.getDOM()[K._.DATA_TBR] = pvt.toolbar;
    }
    
    function renderOperationDOM() {
      var dom = _this.getDOM();
      dom.classList.add(pvt.operation.name);
      
      K.createDOM({text:"(", classes:[K._.TEXT], parent:dom});
      updateParent(pvt.childNodes[0]);
      K.createDOM({text:pvt.operation.label, classes:[K._.TEXT, K._.OPER], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[1]);
      K.createDOM({text:")", classes:[K._.TEXT] , parent:dom});
    }
    
    function renderNegationDOM() {
      var dom = _this.getDOM();
      dom.classList.add(pvt.operation.name);
      K.createDOM({text:pvt.operation.label, classes:[K._.TEXT, K._.OPER], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[0]);
    }
    
    function renderValueDOM() {
      var dom = _this.getDOM();
      dom.classList.add(K._.VALUE);
      
      var _text;
      if (pvt.type===V.IDENTIFIER) {
        _text=["[",pvt.childNodes[0],"]"].join("");
      } else {
        if (args.value === V.TRUE) {
            _text=K.getMessage(_lbl.TRUE);
          } else if (args.value === V.FALSE) {
            _text=K.getMessage(_lbl.FALSE);
          }
      }
      K.createDOM({text:_text, classes:[V.NAME,K._.TEXT, K._.VALUE], parent:dom, hasToolbar:true});
    }
    
    function renderDOM() {
      var child;
      _super.renderDOM();
      pvt.renderDOM();
      initToolbar();
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
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.renderDOM=renderOperationDOM;
          break;
        case V.NOT:
          pvt.operation = K.BooleanOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value);
          pvt.childNodes[0].parent = _this;
          pvt.renderDOM=renderNegationDOM;
          break;
        case V.CONSTANT:
          pvt.childNodes.push(args.value = args.value || V.TRUE);
          pvt.renderDOM=renderValueDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(args.value.slice(11,args.value.length-1));
          pvt.renderDOM=renderValueDOM;
          break;
        default:
          throw V.TYPE_EXCEP;
      }
      pvt.renderDOM();
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