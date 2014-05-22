(function(K) {
  var V = {
    get STRING(){return "String";},
    get NAME(){return "StringNode";},
    get LN_BRK_CLS(){return "breakLine";},
    get SPAN(){return "span";},
    get DIV(){return "div";},
    get TEXT(){return "Text";},
    get OPER(){return "Operator";},
    get VALUE(){return "Value";},
    get IF(){return K.getMessage(this.NAME+".if");},
    get THEN(){return K.getMessage(this.NAME+".then");},
    get ELSE(){return K.getMessage(this.NAME+".else");},
    get CONSTANT(){return 0x1;},
    get OPERATION(){return 0x2;},
    get IDENTIFIER(){return 0x4;},
    get EXPRESSION(){return 0x8;}
  };
  
  var lbl = {
    
  };
  
  function StringNode(args) {
    var _this = K.checkInit(this);
    var _super = new K.Node({parent:(args=args||{}).parent});
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
    
    function getParent() {
      return _super.parent;
    }
    
    function setParent(itm) {
      args.parent = _super.parent = itm;
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
        }
      }
    }
    
    function replaceChild(_old,_new){
      var pos=pvt.childNodes.indexOf(_old);
      if(pos<0){
        console.error("");
        throw 0;
      }
      if(!_new instanceof _this.getClass()){
        console.error("");
        throw 0;
      }
      _new.parent=_this;
      pvt.childNodes[pos]=_new;
      args.value=pvt.childNodes.slice(length-2,length);
      _this.getDOM().replaceChild(_new.getDOM(),_old.getDOM());
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
    
    function getType(){
      return pvt.type;
    }
    
    function updateParent(node) {
      if (node instanceof K.Node) {
        node.parent = _this;
      }
    }
    
    function renderOperationDOM() {
      var dom = _this.getDOM();
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({text:"+", classes:[V.TEXT,V.OPER], hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
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
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({type:V.DIV, text:V.THEN, classes:[V.TEXT], hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({type:V.DIV, text:V.ELSE, classes:[V.TEXT], hasToolbar:true}));
      updateParent(pvt.childNodes[2]);
      
      dom.classList.add(K._.EXPRESSION);
    }
    
    function toolbarItemClick(evt) {
      var result = "";
      var dtType = evt.target[K._.DT_TYPE];
      switch(dtType) {
        case V.OPERATION:
          //setOperation(evt.target[K._.DATA_OPER],pvt.type===V.OPERATION?args.value:[new ArithNode(args),new ArithNode()]);
          alert("Operation");
          break;
        case V.IDENTIFIER:
          init(args={type:dtType,value:[[K._.IDENT_STR,"[",evt.target[K._.DT_VAL],"]"].join("")]});
          break;
        case V.CONSTANT:
          init(args={type:dtType,value:[[V.STRING,"['",prompt("Digite um valor"),"']"].join("")]});
          break;
        case V.EXPRESSION:
          init(args={condition:new K.BooleanNode(), value:[new StringNode(args),new StringNode()], type:V.EXPRESSION});
          break;
        default:
          replaceParent();
          break;
      }
    }
    
    function setOperation(oper,values) {
      init(args={operation:oper.name,type:V.OPERATION,value:values.slice(0,2)});
    }
    
    function getVariableSubMenu() {
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.STRING);
      var items = [];
      for(var i=0, l=variables.length;i<l;i++) {
        items.push({text:variables[i], click:toolbarItemClick, data:{type:V.IDENTIFIER,value:variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE], items:items, text:K.getMessage("StringNode.var")});
    }
    
    function initToolbar() {
      var itms = [
        {text:"Plus", click:toolbarItemClick, data:{type:V.OPERATION,operation:"Plus"}},
        {text:"-", classes:[]},
        getVariableSubMenu(),
        {text:"Constant", click:toolbarItemClick, data:{type:V.CONSTANT}},
        {text:"-", classes:[]},
        {text:"Expression", click:toolbarItemClick, data:{type:V.EXPRESSION}},
      ];
      
      if (getParent() instanceof _this.getClass()) {
        itms.push({text:"-", classes:[]});
        itms.push({parent:toolbar, text:"override", click:toolbarItemClick});
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
          pvt.operation = param.operation;
          pvt.childNodes.push(param.value[0]);
          pvt.childNodes.push(param.value[1]);
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.renderDOM = renderOperationDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(param.value[0].slice(11,param.value[0].length-1));
          pvt.renderDOM = renderValueDOM;
          break;
        case V.CONSTANT:
          param.value[0]=(param.value=param.value||[])[0]||[V.STRING,"['']"].join("");
          pvt.childNodes.push(param.value[0].slice(7,param.value[0].length-1));
          pvt.renderDOM = renderValueDOM;
          break;
        case V.EXPRESSION:
          pvt.childNodes.push(param.condition);
          pvt.childNodes.push(param.value[0]);
          pvt.childNodes.push(param.value[1]);
          pvt.childNodes[0].parent = _this;
          pvt.childNodes[1].parent = _this;
          pvt.childNodes[2].parent = _this;
          pvt.renderDOM = renderExpressionDOM;
          break;
        default:
          console.error("Invalid StringNode type");
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
      clear:{
        get:function(){return clear;}
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
        
    init(args);
  }
  
  StringNode.prototype = new K.Node();
  StringNode.prototype.getClass=function getClass() {
    return StringNode;
  };
  StringNode.prototype.valueOf=function valueOf() {
    return 0x1;
  };
  
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