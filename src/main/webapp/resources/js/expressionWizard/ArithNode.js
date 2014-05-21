(function(K) {
  var V = {
    get CONSTANT()0x1,
    get OPERATION()0x2,
    get IDENTIFIER()0x4,
    get NEGATIVE()0x8,
    get EXPRESSION()0x10,
    get FLOAT_STR()"FloatingPoint",
    get INT_STR()"Integer",
    get NAME()"ArithNode",
    get INT_PATT()"[0-9]+",
    get FLOAT_SUFX()"[.][0-9]+",
    get FLOAT_PATT()this.INT_PATT+"(?:"+this.FLOAT_SUFX+")?",
  };
  var msg=K.getMessage;
  var lbl={
    get NEGATIVE()msg([V.NAME,"negative"].join(".")),
    get PLUS()msg([V.NAME,"plus"].join(".")),
    get MINUS()msg([V.NAME,"minus"].join(".")),
    get MULT()msg([V.NAME,"mult"].join(".")),
    get DIV()msg([V.NAME,"div"].join(".")),
    get IF()msg([V.NAME,"if"].join(".")),
    get THEN()msg([V.NAME,"then"].join(".")),
    get ELSE()msg([V.NAME,"else"].join(".")),
    get CONSTANT()msg([V.NAME,"constant"].join(".")),
    get VAR()msg([V.NAME,"variable"].join(".")),
    get NMBR_PROMPT()msg([V.NAME,"number","valid","prompt"].join(".")),
    get OVERRIDE()msg([V.NAME,"override"].join(".")),
    get EXPRESSION()msg([V.NAME,"expression"].join("."))
  };
///(?:(?:[1-9][0-9]{0,2}(?:[,][0-9]{3,3})*)|[0-9])(?:[.][0-9]*[1-9])?/ LOCALE NUMERIC REGEXP
  function ArithNode(args) {
    var _this = K.checkInit(this);
    var _super = new K.Node({parent:(args=args||{}).parent});
    var pvt = {
      type:args.type,
      childNodes:[]
    };
    
    function formatValue(val) {
      var result = [];
      if (isFloat(val)) {
        result.push(V.FLOAT_STR);
      } else if (isInteger(val)){
        result.push(V.INT_STR);
      } else {
        console.error("NaN");
        throw 0;
      }
      result.push("[");
      result.push(val);
      result.push("]");
      return result.join("");
    }
    
    function getStack() {
      var result=[];
      var appendArray = function (itm) {
        result.push(itm);
      };
      var children=pvt.childNodes;
      switch(pvt.type) {
        case V.OPERATION:
          result.push(pvt.operation.name);
          children[0].getStack().forEach(appendArray);
          children[1].getStack().forEach(appendArray);
          break;
        case V.IDENTIFIER:
          result = [[K._.IDENT_STR,"[",children[0],"]"].join("")];
          break;
        case V.CONSTANT:
          result = [formatValue(children[0])];
          break;
        case V.NEGATIVE:
          result.push(pvt.operation.name);
          children[0].getStack().forEach(appendArray);
          break;
        case V.EXPRESSION:
          result.push(K._.CHOICE);
          children[0].getStack().forEach(appendArray);
          children[1].getStack().forEach(appendArray);
          children[2].getStack().forEach(appendArray);
          break;
      }
      return result;
    }
    
    function clear() {
      pvt.childNodes = [];
      _super.clear();
    }
    
    function toString() {
      var result = "";
      switch(pvt.type) {
        case V.OPERATION:
          result = [pvt.operation, pvt.childNodes[0].toString(), pvt.childNodes[1].toString()].join();
          break;
        case V.IDENTIFIER:
          result = [K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("");
          break;
        case V.CONSTANT:
          result = formatValue(pvt.childNodes[0]);
          break;
        case V.NEGATIVE:
          result = [pvt.operation,pvt.childNodes[0]].join();
          break;
        case V.EXPRESSION:
          result = [K._.CHOICE,pvt.childNodes[0].toString(), pvt.childNodes[1], pvt.childNodes[2]].join();
          break;
      }
      return result;
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
    
    function getValues() {
      return pvt.childNodes;
    }
    
    function getType() {
      return pvt.type;
    }
    
    function getOperation() {
      return pvt.operation;
    }
    
    Object.defineProperties(_this, {
      parent:{
        get:getParent,
        set:setParent
      },
      values:{
        get:getValues
      },
      type:{
        get:getType
      },
      operation:{
        get:getOperation
      },
      replaceWithChild:{
        get:function(){return replaceWithChild;}
      },
      clear:{
        get:function(){return clear;}
      },
      getDOM:{
        get:function(){return _super.getDOM;}
      },
      toString : {
        get:function(){return toString;}
      },
      valueOf:{
        get:function(){return valueOf;}
      },
      getStack:{
        get:function(){return getStack;}
      },
      getNodeType:{
        get:function(){return getNodeType;}
      }
    });

    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }

    function renderOperationDOM() {
      var dom = _this.getDOM();
      dom.appendChild(K.createDOM({text:"(", classes:[K._.TEXT,"start-oper"]}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({text:pvt.operation.label, classes:[K._.TEXT,K._.OPER], hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({text:")", classes:[K._.TEXT]}));
      dom.classList.add(pvt.operation);
    }
    
    function renderNegativeDOM() {
      var dom = _this.getDOM();
      dom.appendChild(K.createDOM({text:pvt.operation.label, classes:[K._.TEXT,K._.OPER], hasToolbar:true}));
      updateParent(pvt.childNodes[0]);
      dom.classList.add(pvt.operation);
    }
    
    function renderValueDOM(){
      var dom = _this.getDOM();
      var _text = "";
      if (pvt.type === V.IDENTIFIER) {
        _text = ["[",pvt.childNodes[0],"]"].join("");
      } else {
        _text = pvt.childNodes[0].toLocaleString(navigator.language);
      }
      dom.appendChild(K.createDOM({text:_text, hasToolbar:true}));
      dom.classList.add(K._.VALUE);
    }

    function renderExpressionDOM() {
      var dom = _this.getDOM();
      dom.appendChild(K.createDOM({type:K._.DIV, text:lbl.IF, classes:[K._.TEXT], hasToolbar:true}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({type:K._.DIV, text:lbl.THEN, classes:[K._.TEXT], hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({type:K._.DIV, text:lbl.ELSE, classes:[K._.TEXT], hasToolbar:true}));
      updateParent(pvt.childNodes[2]);
      dom.classList.add(K._.EXPRESSION);
    }
    
    function clearToolbar() {
      if (pvt.toolbar) {
        pvt.toolbar.clear();
        delete pvt.toolbar;
      }
    }

    function getVariableSubMenu() {
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.NUMBER);
      var items = [];
      for(var i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:genericClickEvent, data:{type:V.IDENTIFIER,"var-name":variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE], items:items, text:lbl.VAR});
    }
    
    function promptForConstant() {
      var result = "";
      while(!isFloat(result) && !isInteger(result)) {
        result = prompt(lbl.NMBR_PROMPT);
      }
      return Number.parseFloat(result);
    }
    
    function genericClickEvent(evt) {
      var result = "";
      var dtType = evt.target["data-type"];
      switch(dtType) {
        case V.OPERATION:
          switchToOperation(evt.target[K._.DATA_OPER]);
          break;
        case V.IDENTIFIER:
          setIdentifier(evt.target["data-var-name"]);
          break;
        case V.CONSTANT:
          setConstant(promptForConstant());
          break;
        case V.EXPRESSION:
          setExpression();
          break;
        case V.NEGATIVE:
          negativate();
          break;
        default:
          break;
      }
    }
    
    function negativate() {
      if (pvt.type===V.NEGATIVE) {
        _this.replaceWithChild(0);
      }else if (getParent().type===V.NEGATIVE){
        getParent().replaceWithChild(0);
      }else{
        clear();
        init(args={type:V.NEGATIVE, operation:K.ArithOper.NEGATIVE.name, value:new ArithNode(args), parent:_this});
      }
    }
    
    function setExpression() {
      var _parent = getParent();
      var condition = new K.BooleanNode({type:K.BooleanNode.CONSTANT, parent:_parent});
      var value = new K.ArithNode(args);
      var value2 = new K.ArithNode({type:V.CONSTANT, value:[V.INT_STR,"[0]"].join("")});
      clear();
      init(args={condition:condition, value:[value,value2], type:V.EXPRESSION, parent:_parent});
    }
    
    function replaceWithChild(numChild) {
      var child = pvt.childNodes[numChild];
      pvt.childNodes.splice(numChild,1);
      var _type = child.type;
      clear();
      child.parent = getParent();
      
      switch(_type) {
        case V.CONSTANT:
          init(args = {type:child.type, value:child.toString(), parent:_this});
          break;
        case V.NEGATIVE:
          init(args = {type:child.type,value:child, parent:_this});
          break;
        case V.IDENTIFIER:
          init(args = {type:child.type,value:child.toString(), parent:_this});
          break;
        case V.EXPRESSION:
          init({type:child.type, value:[child.values[1],child.values[2]], condition:child.values[0], parent:_this});
          break;
        case V.OPERATION:
          init(args = {type:child.type, value:child.values, operation:child.operation.name, parent:_this});
          break;
      }
      child.clear();
      child.getDOM().remove();
    }
    
    function setConstant(constantValue) {
      clear();
      init(args = {type:V.CONSTANT, value:formatValue(constantValue), parent:getParent()});
    }
    
    function setIdentifier(varName) {
      clear();
      var _value = [K._.IDENT_STR,"[",varName,"]"].join("");
      init(args = {type:V.IDENTIFIER, value:_value, parent:getParent()});
    }
    
    function switchToOperation(oper) {
      var children=[];
      
      function getConstantNeutral() {
        var result = [V.INT_STR,"[",];
        switch(oper) {
          case K.ArithOper.PLUS:
          case K.ArithOper.MINUS:
            result.push(0);
            break;
          case K.ArithOper.MULT:
          case K.ArithOper.DIV:
            result.push(1);
            break;
          default:
            console.error("Operator not Supported");
            throw 0;
        }
        result.push("]");
        return result.join("");
      }

      switch(pvt.type) {
        case V.CONSTANT:
        case V.IDENTIFIER:
        case V.NEGATIVE:
        case V.EXPRESSION:
          // CONSTRUCT TWO CHILD CONSTANT NODES AND CHANGE THIS
          children = [new ArithNode(args),new ArithNode({type:V.CONSTANT, value:getConstantNeutral()})];
          clear();
          args = {operation:oper.name, type:V.OPERATION, value:children, parent:getParent()};
          init(args);
          break;
        case V.OPERATION:
          clear();
          init(args = {operation:oper.name, type:args.type, value:args.value, parent:getParent()});
          break;
      }
    }
    
    function initNegToolbar() {
      var arithOp = K.ArithOper;
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR, pvt.operation.name],items:[
        {text:lbl.NEGATIVE, click:genericClickEvent, data:{type:V.NEGATIVE,operation:arithOp.NEGATIVE}},
        {text:lbl.PLUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.PLUS}},
        {text:lbl.MINUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MINUS}},
        {text:lbl.MULT, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MULT}},
        {text:lbl.DIV, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.DIV}},
        {text:"-", classes:[]},
        {text:lbl.EXPRESSION, click:genericClickEvent, data:{type:V.EXPRESSION}}
      ]});
    }
    
    function clickOverrideParentEvent(evt) {
      var parent = getParent();
      if (parent instanceof K.Node) {
        if (parent instanceof ArithNode) {
          parent.replaceWithChild(parent.values.indexOf(_this));
        }
      } else {
        alert("parent is not a Node type");
      }
    }
    
    function initValueToolbar() {
      var arithOp = K.ArithOper;
      var itms = [
        {text:lbl.NEGATIVE, click:genericClickEvent, data:{type:V.NEGATIVE,operation:arithOp.NEGATIVE}},
        {text:lbl.PLUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.PLUS}},
        {text:lbl.MINUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MINUS}},
        {text:lbl.MULT, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MULT}},
        {text:lbl.DIV, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.DIV}},
        {text:"-", classes:[]}
      ];
      itms.push(getVariableSubMenu());
      itms.push({text:lbl.CONSTANT, click:genericClickEvent, data:{type:V.CONSTANT}});
      
      itms.push({text:"-", classes:[]});
      itms.push({text:lbl.EXPRESSION, click:genericClickEvent, data:{type:V.EXPRESSION}});
      itms.push({text:"-", classes:[]});
      if (getParent() instanceof ArithNode) {
        itms.push({parent:toolbar, text:lbl.OVERRIDE, click:clickOverrideParentEvent});
      }
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR,K._.VALUE],items:itms});
    }
    
    function initOperationToolbar() {
      var arithOp = K.ArithOper;
      var itms = [
        {text:lbl.NEGATIVE, click:genericClickEvent, data:{type:V.NEGATIVE,operation:arithOp.NEGATIVE}},
        {text:lbl.PLUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.PLUS}},
        {text:lbl.MINUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MINUS}},
        {text:lbl.MULT, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MULT}},
        {text:lbl.DIV, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.DIV}},
        {text:"-", classes:[]},
        {text:lbl.EXPRESSION, click:genericClickEvent, data:{type:V.EXPRESSION}}
      ];
      if (getParent() instanceof ArithNode) {
        itms.push({parent:toolbar, text:lbl.OVERRIDE, click:clickOverrideParentEvent});
      }
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR, pvt.operation.name],items:itms});
    }
    
    function initExpressionToolbar() {
      var arithOp = K.ArithOper;
      var itms = [
        {text:lbl.NEGATIVE, click:genericClickEvent, data:{type:V.NEGATIVE,operation:arithOp.NEGATIVE}},
        {text:lbl.PLUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.PLUS}},
        {text:lbl.MINUS, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MINUS}},
        {text:lbl.MULT, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.MULT}},
        {text:lbl.DIV, click:genericClickEvent, data:{type:V.OPERATION,operation:arithOp.DIV}},
        {text:"-", classes:[]},
        {text:lbl.EXPRESSION, click:genericClickEvent, data:{type:V.EXPRESSION}}
      ];
      if (getParent() instanceof ArithNode) {
        itms.push({parent:toolbar, text:lbl.OVERRIDE, click:clickOverrideParentEvent});
      }
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR, K._.EXPRESSION],items:itms});
    }
    
    function initToolbar() {
      switch(pvt.type) {
        case V.NEGATIVE:
          initNegToolbar();
          break;
        case V.CONSTANT:
        case V.IDENTIFIER:
          initValueToolbar();
          break;
        case V.OPERATION:
          initOperationToolbar();
          break;
        case V.EXPRESSION:
          initExpressionToolbar();
          break;
      }
      _this.getDOM()[K._.DATA_TBR] = pvt.toolbar;
    }
    
    function init(args) {
      var dom = _this.getDOM();
      dom.classList.add(V.NAME);
      pvt.type=args.type||V.CONSTANT;
      switch(pvt.type) {
        case V.OPERATION:
          pvt.operation = K.ArithOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.renderDOM = renderOperationDOM;
          break;
        case V.NEGATIVE:
          pvt.operation = K.ArithOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value);
          pvt.childNodes[0].parent = _this;
          pvt.renderDOM = renderNegativeDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(args.value.slice(11,args.value.length-1));
          pvt.renderDOM = renderValueDOM;
          break;
        case V.CONSTANT:
          pvt.childNodes.push(Number.parseFloat(new RegExp(V.FLOAT_PATT).exec(args.value||0)[0]));
          pvt.renderDOM = renderValueDOM;
          break;
        case V.EXPRESSION:
          pvt.childNodes.push(args.condition);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.childNodes[2].parent = _this;
          pvt.renderDOM = renderExpressionDOM;
          break;
        default:
          console.error("Unsupported type",args);
          throw 0;
      }
      pvt.renderDOM();
      _this.getDOM()[K._.DT_CLASS]=_this;
      initToolbar();
    }
    init(args);
  }

  function isFloat(val) {
    return new RegExp(["^",V.INT_PATT,V.FLOAT_SUFX,"$"].join("")).test(val);
  }
  
  function isInteger(val) {
    return new RegExp(["^",V.INT_PATT,"$"].join("")).test(val);
  }

  function isNumberConstant(current) {
    return new RegExp(["^",V.INT_STR,"\\[",V.INT_PATT,"\\]|",V.FLOAT_STR,"\\[",V.FLOAT_PATT,"\\]$"].join("")).test(current);
  }
  
  function getArithNodeType(str) {
    var type;
    var _ = K.Node;
    if (K._.REGX_IDENT.test(str) && _.getVariables(_.VariableType.NUMBER).indexOf(str.slice(11,str.length-1))>=0) {
      type = V.IDENTIFIER;
    } else if (isNumberConstant(str)) {
      type = V.CONSTANT;
    } else if (K.ArithOper.isArithOper(str)){
      type = K.ArithOper.getValueOf(str);
      if (type === K.ArithOper.NEGATIVE) {
        type = V.NEGATIVE;
      } else {
        type = V.OPERATION;
      }
    } else if (K._.CHOICE===str) {
      type = V.EXPRESSION;
    } else {
      type = 0x0;
    }
    return type;
  }
  
  function isArithNode(str) {
    var _ = K.Node;
    return K.ArithOper.isArithOper(str) || (K._.REGX_IDENT.test(str) && _.getVariables(_.VariableType.NUMBER).indexOf(str.slice(11,str.length-1))>=0) || isNumberConstant(str);
  }
  
  function valueOf(){
    return 0x4;
  }
  
  ArithNode.prototype = new K.Node();
  Object.defineProperties(ArithNode, {
    isArithNode:{
      get:function(){return isArithNode;}
    },
    isNumberConstant:{
      get:function(){return isNumberConstant;}
    },
    getArithNodeType:{
      get:function(){return getArithNodeType;}
    },
    CONSTANT:{
      get:function(){return V.CONSTANT;}
    },
    OPERATION:{
      get:function(){return V.OPERATION;}
    },
    IDENTIFIER:{
      get:function(){return V.IDENTIFIER;}
    },
    NEGATIVE:{
      get:function(){return V.NEGATIVE;}
    },
    EXPRESSION:{
      get:function(){return V.EXPRESSION;}
    }
  });
  
  Object.defineProperties(K,{
    ArithNode:{
      get:function(){return ArithNode;}
    }
  });
  
})(window._parser);