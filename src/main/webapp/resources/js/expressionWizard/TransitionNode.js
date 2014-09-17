(function(K) {
  if (K.TransitionNode !== undefined){
    console.log("TransitionNode already loaded");
    return;
  }
  var V = {
    get STRING(){return "String";},
    get NAME(){return "TransitionNode";},
    get DIV(){return "div";},
    get TEXT(){return "Text";},
    get VALUE(){return "Value";},
    get CONSTANT(){return 0x1;},
    get EXPRESSION(){return 0x8;}
  };

  var lbl = {
    get IF(){return [V.NAME,"if"].join(".");},
    get THEN(){return [V.NAME,"then"].join(".");},
    get ELSE(){return [V.NAME,"else"].join(".");},
    get EXPR(){return [V.NAME,"expression"].join(".");},
    get OVERRIDE(){return [V.NAME,"override"].join(".");},
  };
  
  function TransitionNode(args) {
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
        case V.EXPRESSION:
          result.push(K._.CHOICE);
          pvt.childNodes[0].getStack().forEach(addToResult);
          pvt.childNodes[1].getStack().forEach(addToResult);
          pvt.childNodes[2].getStack().forEach(addToResult);
          break;
        default:
          console.error("Not expected",pvt);
          throw 0;
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
          _this.parent=_gParent;
          _this.attachInput(_parent.getDOM()[K._.DT_INPT]);
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
        if(itm instanceof K.Node){
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
    
    function renderValueDOM() {
      var dom = _this.getDOM();
      var _text=["[",pvt.childNodes[0],"]"].join("");
      dom.appendChild(K.createDOM({text:_text,hasToolbar:true}));
      dom.classList.add(V.VALUE);
    }
    
    function renderExpressionDOM() {
      var dom = _this.getDOM();
      dom.appendChild(K.createDOM({type:V.DIV,text:K.getMessage(lbl.IF),classes:[V.TEXT],hasToolbar:true}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({type:V.DIV,text:K.getMessage(lbl.THEN),classes:[V.TEXT],hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({type:V.DIV,text:K.getMessage(lbl.ELSE),classes:[V.TEXT],hasToolbar:true}));
      updateParent(pvt.childNodes[2]);
      
      dom.classList.add(K._.EXPRESSION);
    }
    
    function toolbarItemClick(evt) {
      var dtType = evt.target[K._.DT_TYPE];
      switch(dtType) {
        case V.CONSTANT:
          init(args={type:dtType,value:[[V.STRING,"['",evt.target[K._.DT_VAL],"']"].join("")]});
          break;
        case V.EXPRESSION://",_.getVariables()[0],"
          setExpression(new K.BooleanNode(),new TransitionNode(args),new TransitionNode({value:[[V.STRING,"['",K.Node.getVariables()[0],"']"].join("")]}));
          break;
        default:
          replaceParent();
          break;
      }
      _this.getDOM().dispatchEvent(new CustomEvent("selected",{bubbles:true,cancelable:true,detail:{}}));
    }
    
    function setExpression(condition,value1,value2){
      init(args={type:V.EXPRESSION,condition:condition,value:[value1,value2]});
    }
  
    function getVariableSubMenu() {
      var _ = K.Node;
      var variables = _.getVariables(_.VariableType.TRANSITION);
      var items = [];
      for(var i=0,l=variables.length;i<l;i++) {
        items.push({text:variables[i],click:toolbarItemClick,data:{type:V.CONSTANT,value:variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE],items:items,text:K.getMessage("TransitionNode.var")});
    }
      
    function initToolbar() {
      var itms = [
        getVariableSubMenu(),
        {text:"-",classes:[]},
        {text:K.getMessage(lbl.EXPR),click:toolbarItemClick,data:{type:V.EXPRESSION}},
      ];
      
      if (getParent() instanceof _this.getClass()) {
        itms.push({text:"-",classes:[]});
        itms.push({parent:toolbar,text:K.getMessage(lbl.OVERRIDE),click:toolbarItemClick});
      }
      
      pvt.toolbar = new K.Toolbar({parent:_this.getDOM(),classes:[K._.TOOLBAR,K._.VALUE],items:itms});
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
        case V.CONSTANT:
          param.value=param.value||[];
          param.value[0]=param.value[0]||[V.STRING,"['",K.Node.getVariables()[0],"']"].join("");
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
          console.error("Invalid TransitionNode type");
          throw 0;
      }
      var dom = _this.getDOM();
      dom.classList.add(V.NAME);
      dom[K._.DT_CLASS]=_this;
      pvt.renderDOM();
    }
    
    Object.defineProperties(_this,{
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
      },
      attachInput:{
        get:function(){return _super.attachInput;}
      }
    });
    init(args);
  }
  
  TransitionNode.prototype = new K.Node();
  TransitionNode.prototype.getClass=function getClass() {
    return TransitionNode;
  };
  TransitionNode.prototype.valueOf=function valueOf() {
    return 0x1;
  };
  
  function isChoice(str){
    return (/^Choice$/).test(str);
  }
  
  function isStringConst(str){
    var _ = K.Node;
    return (/^String\['.*'\]$/).test(str) && _.getVariables(_.VariableType.TRANSITION).indexOf(str.slice(8,str.length-2))>=0;
  }
  
  function isTransitionNode(str) {
    var _ = K.Node;
    return isChoice(str) || (isStringConst(str));
  }
  
  function getTransitionNodeType(str) {
    var type;
    var _ = K.Node;
    if (isChoice(str)) {
      type = V.EXPRESSION;
    } else if (isStringConst(str)) {
      type = V.CONSTANT;
    } else {
      type = 0x0;
    }
    return type;
  }

  function createBooleanNode(current, cache){
    var BNode=K.BooleanNode;
    var _type=BNode.getBooleanNodeType(current);
    var result;
    switch(_type){
      case BNode.CONSTANT:
      case BNode.IDENTIFIER:
        result = new BNode({value:[current], type:_type});
        break;
      case BNode.NOT:
        result = new BNode({operation:current, value:[cache.pop()], type:_type});
        break;
      case BNode.OPERATION:
        result = new BNode({operation:current, value:[cache.pop(), cache.pop()], type:_type});
        break;
      default:
        console.error("BooleanNode type not supported");
        throw 0;
    }
    return result;
  }
  
  function createArithmeticNode(current, cache){
    var ANode=K.ArithNode;
    var result;
    var _type = ANode.getArithNodeType(current);
    switch(_type){
      case ANode.CONSTANT:
      case ANode.IDENTIFIER:
        result = new ANode({type:_type, value:[current]});
        break;
      case ANode.NEGATIVE:
        result = new ANode({operation:current, value:[cache.pop()], type:_type});
        break;
      case ANode.EXPRESSION:
        result = new ANode({condition:cache.pop(), value:[cache.pop(),cache.pop()], type:_type});
        break;
      case ANode.OPERATION:
        result = new ANode({operation:current, value:[cache.pop(),cache.pop()], type:_type});
        break;
      default:
        console.error("ArithNode type not supported");
        throw 0;
    }
    return result;
  }

  function calculateValueTypes(obj){
    var type = 0x0;
    if (obj.value[0] instanceof K.Node && obj.value[1] instanceof K.Node){
      type = obj.value[0] | obj.value[1];
    }
    return type;
  }
  
  function getCorrectExpression(obj){
    var result;
    switch(calculateValueTypes(obj)){
      case V.TYPE_BOOL|V.TYPE_BOOL:
        obj.type = K.BooleanNode.EXPRESSION;
        result = new K.BooleanNode(obj);
        break;
      case V.TYPE_NBR|V.TYPE_NBR:
        obj.type = K.ArithNode.EXPRESSION;
        result = new K.ArithNode(obj);
        break;
      default:
        obj.type = K.TransitionNode.EXPRESSION;
        result = new K.TransitionNode(obj);
        break;
    }
    
    return result;
  }
  
  function createTransitionNode(current, cache){
    var _type = K.TransitionNode.getTransitionNodeType(current);
    var result;
    switch(_type){
      case K.TransitionNode.CONSTANT:
        result = new K.TransitionNode({type:_type, value:[current]});
        break;
      default:
        console.error("TransitionNode type not supported");
        throw 0;
    }
    return result;
  }

  function createStringNode(current, cache) {
    var SNode = K.StringNode;
    var _type = SNode.getStringNodeType(current);
    var result;
    switch(_type) {
      case SNode.CONSTANT:
      case SNode.IDENTIFIER:
        result = new SNode({type:_type, value:[current]});
        break;
      default:
        console.error("StringNode type not supported");
        throw 0;
    }
    return result;
  }
  
  function generateTree(stack, dom, input){
    var cache = [];
    var current;
    var result;
    while(stack.length > 0){
      current = stack.shift();
      if (current==="Choice"){
         result = getCorrectExpression({condition:cache.pop(),value:[cache.pop(),cache.pop()]});
      } else if (K.BooleanNode.isBooleanNode(current)){
        result = createBooleanNode(current, cache);
      } else if (K.ArithNode.isArithNode(current)){
        result = createArithmeticNode(current, cache);
      } else if (K.TransitionNode.isTransitionNode(current)){
        result = createTransitionNode(current, cache);
      } else if (K.StringNode.isStringNode(current)) {
        result = createStringNode(current, cache);
      } else if (K._.REGX_IDENT.test(current)){
        // é variável
        console.error("Identifier not expected", current);
        throw 0;
      } else {
        console.error("Parse exception, token not found", current);
        throw 0;
      }
      cache.push(result);
      var stck = result.getStack()[0];
      if (stck!=current){
        console.error(current, result.getStack()[0]);
        throw 0;
      }
    }
    cache[0].parent=dom;
    if (cache.length !== 1){
      console.error("Parse exception. More than one root was found");
      throw 0;
    }
    cache[0].attachInput(input);
    return cache.pop();
  }
  
  Object.defineProperties(TransitionNode,{
    CONSTANT:{get:function(){return V.CONSTANT;}},
    EXPRESSION:{get:function(){return V.EXPRESSION;}},
    generateTree:{
      get:function(){return generateTree;}
    },
    getTransitionNodeType:{
      get:function(){return getTransitionNodeType;}
    },
    isTransitionNode:{
      get:function(){return isTransitionNode;}
    }
  });

  Object.defineProperties(K,{
    TransitionNode:{
      get:function() {
        return TransitionNode;
      }
    }
  });
  
})(window._parser);