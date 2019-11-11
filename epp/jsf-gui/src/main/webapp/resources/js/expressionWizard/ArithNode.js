(function(K) {
  if (K.ArithNode !== undefined){
    console.log("ArithNode already loaded");
    return;
  }
  var V = {
    get CONSTANT(){return 0x1;},
    get OPERATION(){return 0x2;},
    get IDENTIFIER(){return 0x4;},
    get NEGATIVE(){return 0x8;},
    get EXPRESSION(){return 0x10;},
    get FLOAT_STR(){return "FloatingPoint";},
    get INT_STR(){return "Integer";},
    get NAME(){return "ArithNode";},
    get INT_PATT(){return "[0-9]+";},
    get FLOAT_SUFX(){return "[.][0-9]+";},
    get FLOAT_PATT(){return this.INT_PATT+"(?:"+this.FLOAT_SUFX+")?";},
  };
  var msg=K.getMessage;
  var lbl={
    get NEGATIVE(){return msg([V.NAME,"negative"].join("."));},
    get PLUS(){return msg([V.NAME,"plus"].join("."));},
    get MINUS(){return msg([V.NAME,"minus"].join("."));},
    get MULT(){return msg([V.NAME,"mult"].join("."));},
    get DIV(){return msg([V.NAME,"div"].join("."));},
    get IF(){return msg([V.NAME,"if"].join("."));},
    get THEN(){return msg([V.NAME,"then"].join("."));},
    get ELSE(){return msg([V.NAME,"else"].join("."));},
    get CONSTANT(){return msg([V.NAME,"constant"].join("."));},
    get VAR(){return msg([V.NAME,"variable"].join("."));},
    get NMBR_PROMPT(){return msg([V.NAME,"number","valid","prompt"].join("."));},
    get OVERRIDE(){return msg([V.NAME,"override"].join("."));},
    get EXPRESSION(){return msg([V.NAME,"expression"].join("."));},
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
    
    function clear(){
      var itm;
      while(pvt.childNodes.length>0){
        itm=pvt.childNodes.pop();
        if(itm instanceof Node){
          itm.clear();
        }
      }
      _super.clear();
    }
    
    function toString() {
      return "";
    }
    
    function getParent() {
      return _super.parent;
    }
    
    function setParent(itm) {
      args.parent = _super.parent = itm;
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
    
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }

    function replaceParent(){
      var _parent=getParent();
      var _gParent;
      if(_parent instanceof _this.getClass()){
        _gParent=_parent.parent;
        if(_gParent instanceof K.Node){
          _gParent.replaceChild(_parent,_this);
        }else if(_gParent instanceof Element){
          _gParent.replaceChild(_this.getDOM(),_parent.getDOM());
          _this.parent=_gParent;
          _this.attachInput(_parent.getDOM()[K._.DT_INPT]);
        }
      }
    }
    
    function replaceChild(_old,_new){
      var pos=pvt.childNodes.indexOf(_old);
      if(pos<0){
        console.error("replace Child ArithNode");
        throw 0;
      }
      if(!_new instanceof _this.getClass()){
        console.error("replace Child ArithNode");
        throw 0;
      }
      _new.parent=_this;
      pvt.childNodes[pos]=_new;
      args.value=pvt.childNodes.slice(length-2,length);
      _this.getDOM().replaceChild(_new.getDOM(),_old.getDOM());
    }

    function renderOperationDOM() {
      var dom = _this.getDOM();
      dom.appendChild(K.createDOM({text:"(", classes:[K._.TEXT,"start-oper"], hasToolbar:true}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({text:pvt.operation.label, classes:[K._.TEXT,K._.OPER], hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({text:")", classes:[K._.TEXT], hasToolbar:true}));
      dom.classList.add(pvt.operation);
    }
    
    function renderNegativeDOM() {
      var dom = _this.getDOM();
      dom.appendChild(K.createDOM({text:"(", classes:[K._.TEXT,"start-oper"], hasToolbar:true}));
      dom.appendChild(K.createDOM({text:pvt.operation.label, classes:[K._.TEXT,K._.OPER], hasToolbar:true}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({text:")", classes:[K._.TEXT], hasToolbar:true}));
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
    
    function promptForConstant() {
      var result = "";
      while(!isFloat(result) && !isInteger(result)) {
        result = prompt(lbl.NMBR_PROMPT);
      }
      return Number.parseFloat(result);
    }
    
    function toolbarClickItem(evt) {
      var result = "";
      var dtType = evt.target[K._.DT_TYPE];
      switch(dtType) {
        case V.OPERATION:
          setOperation(evt.target[K._.DATA_OPER],pvt.type===V.OPERATION?args.value:[new ArithNode(args),new ArithNode()]);
          break;
        case V.IDENTIFIER:
          setIdentifier(evt.target[K._.DT_VAL]);
          break;
        case V.CONSTANT:
          setConstant(promptForConstant());
          break;
        case V.EXPRESSION:
          setExpression(new K.BooleanNode(),new ArithNode(args),new ArithNode());
          break;
        case V.NEGATIVE:
          negativate();
          break;
        default:
          evt.target[K._.DT_VAL].replaceParent();
          clearToolbar();
          break;
      }
      _this.getDOM().dispatchEvent(new CustomEvent("selected",{bubbles:true,cancelable:true,detail:{}}));
    }
    
    function negativate() {
      if (pvt.type===V.NEGATIVE) {
        pvt.childNodes[0].replaceParent();
      }else if(getParent() instanceof ArithNode && getParent().type===V.NEGATIVE){
        replaceParent();
      }else{
        init(args={type:V.NEGATIVE, operation:K.ArithOper.NEGATIVE.name, value:[new ArithNode(args)]});
      }
    }
    function setExpression(condition, value1, value2) {
      init(args={condition:condition, value:[value1,value2], type:V.EXPRESSION});
    }
    function setConstant(constantValue) {
      init(args={type:V.CONSTANT,value:[formatValue(constantValue)]});
    }
    function setIdentifier(varName) {
      init(args={type:V.IDENTIFIER,value:[[K._.IDENT_STR,"[",varName,"]"].join("")]});
    }
    function setOperation(oper,values) {
      init(args={operation:oper.name,type:V.OPERATION,value:values.slice(0,2)});
    }

    function clickOverrideParentEvent(evt) {
      var parent = getParent();
      if (parent instanceof K.Node) {
        if (parent instanceof _this.getClass()) {
          replaceParent();
        }
      } else {
        alert("parent is not a Node type");
      }
      clearToolbar();
    }
    
    function getVariableSubMenu() {
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.NUMBER);
      var items = [];
      for(var i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:toolbarClickItem, data:{type:V.IDENTIFIER,value:variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE], items:items, text:lbl.VAR});
    }
    
    function initToolbar() {
      clearToolbar();
      var arithOp = K.ArithOper;
      var itms = [
        {text:lbl.NEGATIVE, click:toolbarClickItem, data:{type:V.NEGATIVE,operation:arithOp.NEGATIVE}},
        {text:lbl.PLUS, click:toolbarClickItem, data:{type:V.OPERATION,operation:arithOp.PLUS}},
        {text:lbl.MINUS, click:toolbarClickItem, data:{type:V.OPERATION,operation:arithOp.MINUS}},
        {text:lbl.MULT, click:toolbarClickItem, data:{type:V.OPERATION,operation:arithOp.MULT}},
        {text:lbl.DIV, click:toolbarClickItem, data:{type:V.OPERATION,operation:arithOp.DIV}},
        {text:"-", classes:[]},
        {text:lbl.CONSTANT, click:toolbarClickItem, data:{type:V.CONSTANT}},
        {text:lbl.EXPRESSION, click:toolbarClickItem, data:{type:V.EXPRESSION}},
        {text:"-", classes:[]},
      ];
      itms.push(getVariableSubMenu());
      
      if (getParent() instanceof _this.getClass()) {
        itms.push({text:"-", classes:[]});
        itms.push({parent:toolbar, text:lbl.OVERRIDE, click:toolbarClickItem, data:{value:_this}});
      }
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR,K._.VALUE],items:itms});
      _this.getDOM()[K._.DATA_TBR] = pvt.toolbar;
    }
    
    function clearToolbar() {
      if (pvt.toolbar) {
        pvt.toolbar.clear();
        delete pvt.toolbar;
      }
    }

    function init(param) {
      clear();
      pvt.type=param.type||V.CONSTANT;
      switch(pvt.type) {
        case V.OPERATION:
          pvt.operation = K.ArithOper.getValueOf(param.operation);
          pvt.childNodes.push(param.value[0]);
          pvt.childNodes.push(param.value[1]);
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.renderDOM = renderOperationDOM;
          break;
        case V.NEGATIVE:
          pvt.operation = K.ArithOper.getValueOf(param.operation);
          pvt.childNodes.push(param.value[0]);
          pvt.childNodes[0].parent = _this;
          pvt.renderDOM = renderNegativeDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(param.value[0].slice(11,param.value[0].length-1));
          pvt.renderDOM = renderValueDOM;
          break;
        case V.CONSTANT:
          pvt.childNodes.push(Number.parseFloat(new RegExp(V.FLOAT_PATT).exec((param.value||[])[0]||0)[0]));
          pvt.renderDOM = renderValueDOM;
          break;
        case V.EXPRESSION:
          if (param.condition.getClass() !== K.BooleanNode){
            param.condition = new K.BooleanNode();
          }
          pvt.childNodes.push(param.condition);
          pvt.childNodes.push(param.value[0]);
          pvt.childNodes.push(param.value[1]);
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.childNodes[2].parent = _this;
          pvt.renderDOM = renderExpressionDOM;
          break;
        default:
          console.error("Unsupported type",param);
          throw 0;
      }
      var dom = _this.getDOM();
      dom.classList.add(V.NAME);
      dom[K._.DT_CLASS]=_this;
      
      pvt.renderDOM();
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
      clear:{
        get:function(){return clear;}
      },
      getDOM:{
        get:function(){return _super.getDOM;}
      },
      toString : {
        get:function(){return toString;}
      },
      getStack:{
        get:function(){return getStack;}
      },
      initToolbar:{
        get:function(){return initToolbar;}
      },
      replaceParent:{
        get:function(){return replaceParent;}
      },
      replaceChild:{
        get:function(){return replaceChild;}
      },
      attachInput:{
        get:function(){return _super.attachInput;}
      }
    });
    init(args);
  }

  ArithNode.prototype = new K.Node();
  ArithNode.prototype.getClass=function getClass() {
    return ArithNode;
  };
  ArithNode.prototype.valueOf=function valueOf(){
    return K._.TYPE_NBR;
  };
  
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
  
  function toString(){
    return this.name;
  }
  
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
    },
    toString:{
      get:function(){return toString;}
    }
  });
  
  Object.defineProperties(K,{
    ArithNode:{
      get:function(){return ArithNode;}
    }
  });
  
})(window._parser);