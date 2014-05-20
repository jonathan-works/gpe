(function(K) {
  StringNode.prototype = new K.Node();
  
  var V = {
    get STRING()"String",
    get NAME()"StringNode",
    get LN_BRK_CLS()"breakLine",
    get SPAN()"span",
    get DIV()"div",
    get TEXT()"Text",
    get OPER()"Operator",
    get VALUE()"Value",
    get IF()K.getMessage(this.NAME+".if"),
    get THEN()K.getMessage(this.NAME+".then"),
    get ELSE()K.getMessage(this.NAME+".else"),
    get CONSTANT()0x1,
    get OPERATION()0x2,
    get IDENTIFIER()0x4,
    get EXPRESSION()0x8
  };
  
  var lbl = {
    
  };
  
  function StringNode(args) {
    var _this = K.checkInit(this);
    var _super = new K.Node({parent:args.parent});
    var pvt = {
      type:args.type,
      childNodes:[]
    };
    
    function getStack() {
      var result = [];
      
      function addToResult(itm){
        result.push(itm);
      }
      
      switch(pvt.type) {
        case V.CONSTANT:
          result = [[V.STRING,"[",pvt.childNodes[0],"]"].join("")];
          break;
        case V.OPERATION:
          result.push(pvt.operation);
          pvt.childNodes[0].getStack().forEach(addToResult);
          pvt.childNodes[1].getStack().forEach(addToResult);
          break;
        case V.IDENTIFIER:
          result = [[K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("")];
          break;
        case V.EXPRESSION:
          result.push(K._.CHOICE);
          pvt.childNodes[0].getStack().forEach(addToResult);
          pvt.childNodes[1].getStack().forEach(addToResult);
          pvt.childNodes[2].getStack().forEach(addToResult);
          break;
      }
      return result;
    }
    
    function toString() {
      var result = "";
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
    }
    
    function clear() {
      _super.clear();
      pvt.childNodes=[];
    }
    
    function getType(){
      return pvt.type;
    }
    
    Object.defineProperties(_this, {
      parent:{
        get:getParent,
        set:setParent
      },
      type:{
        get:getType
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
      valueOf:{
        get:function(){return valueOf;}
      },
      getNodeType:{
        get:function(){return getNodeType;}
      },
      clear:{
        get:function(){return clear;}
      }
    });
        
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }
    
    function clickOverrideParentEvent(evt) {
      
    }
    
    function renderOperationDOM() {
      var dom = _this.getDOM();
      pvt.childNodes[0].renderDOM();
      dom.appendChild(K.createDOM({text:"+", classes:[V.TEXT,V.OPER], hasToolbar:true}));
      pvt.childNodes[1].renderDOM();
      dom.classList.add(pvt.operation);
    }
    
    function renderValueDOM() {
      var dom = _this.getDOM();
      var _text;
      if (pvt.type===V.IDENTIFIER) {
        _text=["[",pvt.childNodes[0],"]"].join("");
      } else {
        _text=pvt.childNodes[0];
      }
      dom.appendChild(K.createDOM({text:_text, hasToolbar:true}));
      dom.classList.add(V.VALUE);
    }
    
    function renderExpressionDOM() {
      var dom = _this.getDOM();
      dom.appendChild(K.createDOM({type:V.DIV, text:V.IF, classes:[V.TEXT], hasToolbar:true}));
      pvt.childNodes[0].renderDOM();
      dom.appendChild(K.createDOM({type:V.DIV, text:V.THEN, classes:[V.TEXT], hasToolbar:true}));
      pvt.childNodes[1].renderDOM();
      dom.appendChild(K.createDOM({type:V.DIV, text:V.ELSE, classes:[V.TEXT], hasToolbar:true}));
      pvt.childNodes[2].renderDOM();
      
      dom.classList.add(K._.EXPRESSION);
    }
    
    function initValueToolbar() {
      var itms = [
        {text:"Plus", click:genericClickEvent, data:{type:V.OPERATION,operation:"Plus"}},
        {text:"-", classes:[]}
      ];
      itms.push(getVariableSubMenu());
      itms.push({text:"Constant", click:genericClickEvent, data:{type:V.CONSTANT}});
      
      itms.push({text:"-", classes:[]});
      itms.push({text:"Expression", click:genericClickEvent, data:{type:V.EXPRESSION}});
      itms.push({text:"-", classes:[]});
      if (getParent() instanceof StringNode) {
        itms.push({parent:toolbar, text:"override", click:clickOverrideParentEvent});
      }
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR,K._.VALUE],items:itms});
    }
    
    function clearToolbar() {
      if (pvt.toolbar) {
        pvt.toolbar.clear();
        delete pvt.toolbar;
      }
    }

    function getVariableSubMenu() {
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.STRING);
      var items = [];
      for(var i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:genericClickEvent, data:{type:V.IDENTIFIER,"var-name":variables[i]}});
      }
      variables = _.getVariables(_.VariableType.NUMBER);
      for(i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:genericClickEvent, data:{type:V.IDENTIFIER,"var-name":variables[i]}});
      }
      variables = _.getVariables(_.VariableType.BOOLEAN);
      for(i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:genericClickEvent, data:{type:V.IDENTIFIER,"var-name":variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE], items:items, text:"Variáveis"});
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
          setConstant(prompt("Digite um valor"));
          break;
        case V.EXPRESSION:
          setExpression();
          break;
        default:
          break;
      }
    }
    
    function initOperationToolbar() {
      
    }
    
    function initExpressionToolbar() {
      
    }
    
    function initToolbar() {
      switch(pvt.type) {
        case V.OPERATION:
          initOperationToolbar();
          break;
        case V.IDENTIFIER:
        case V.CONSTANT:
          initValueToolbar();
          break;
        case V.EXPRESSION:
          initExpressionToolbar();
          break;
      }
    }
    
    function init(args) {
      var dom = _this.getDOM();
      dom.classList.add(V.NAME);
      pvt.type=args.type;
      switch(pvt.type) {
        case V.OPERATION:
          pvt.operation = args.operation;
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.renderDOM = renderOperationDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(args.value.slice(11,args.value.length-1));
          pvt.renderDOM = renderValueDOM;
          break;
        case V.CONSTANT:
          pvt.childNodes.push(args.value.slice(7,args.value.length-1));
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
          console.error("Invalid StringNode type");
          throw 0;
      }
    }
    init(args);
  }
  
  function isStringNode(str) {
    var _ = K.Node;
    return (/^String\['.*'\]|Plus|Choice$/).test(str)
            || (K._.REGX_IDENT.test(str) && _.getVariables().indexOf(str.slice(11,str.length-1))>=0);
  }
  
  function getStringNodeType(str) {
    var type;
    var _ = K.Node;
    if ((/^Plus$/).test(str)) {
      type = V.OPERATION;
    } else if ((/^Choice$/).test(str)) {
      type = V.EXPRESSION;
    } else if ((/^String\['.*'\]$/).test(str)) {//K._.REGX_IDENT.test(str)
      type = V.CONSTANT;
    } else if (K._.REGX_IDENT.test(str)&&_.getVariables().indexOf(str.slice(11,str.length-1))>=0) {
      type = V.IDENTIFIER;
    } else {
      type = 0x0;
    }
    return type;
  }
  
  function valueOf() {
    return 0x1;
  }
  
  Object.defineProperties(StringNode, {
    CONSTANT:{get:function(){return V.CONSTANT;}},
    OPERATION:{get:function(){return V.OPERATION;}},
    IDENTIFIER:{get:function() {return V.IDENTIFIER;}},
    EXPRESSION:{get:function(){return V.EXPRESSION;}},
    
    getStringNodeType:{
      get:function(){return getStringNodeType;}
    },
    isStringNode:{
      get:function(){return isStringNode;}
    }
  });

  Object.defineProperties(K,{
    StringNode:{
      get:function() {
        return StringNode;
      }
    }
  });
  
})(window._parser);