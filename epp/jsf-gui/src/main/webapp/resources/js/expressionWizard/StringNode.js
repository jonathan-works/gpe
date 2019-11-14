(function(K){
  if (K.StringNode !== undefined){
    console.log("StringNode already loaded");
    return;
  }
  var V={
    get STRING(){return "String";},
    get NAME(){return "StringNode";},
    get DIV(){return "div";},
    get TEXT(){return "Text";},
    get OPER(){return "Operator";},
    get VALUE(){return "Value";},
    get CONSTANT(){return 0x1;},
    get OPERATION(){return 0x2;},
    get IDENTIFIER(){return 0x4;},
    get EXPRESSION(){return 0x8;},
    get STR_OPER(){return "Plus";},
    get REGX_STR(){return (/^String\['(.*)'\]$/);}
  };

  var lbl={
    get IF(){return [V.NAME,"if"].join(".");},
    get THEN(){return [V.NAME,"then"].join(".");},
    get ELSE(){return [V.NAME,"else"].join(".");},
    get PLUS_OP(){return ["StringOper","plus"].join(".");},
    get PROMPT(){return [V.NAME,"prompt"].join(".");},
    get BEFORE(){return [V.NAME,"addPrefix"].join(".");},
    get AFTER(){return [V.NAME,"addSufix"].join(".");},
    get BOOL(){return [V.NAME,"booleanValue"].join(".");},
    get STR(){return [V.NAME,"stringValue"].join(".");},
    get NBR(){return [V.NAME,"numberValue"].join(".");},
    get CONST(){return [V.NAME,"constant"].join(".");},
    get EXPR(){return [V.NAME,"expression"].join(".");},
    get OVERRIDE(){return [V.NAME,"override"].join(".");},
  };
  
  function StringNode(args){
    var _this=K.checkInit(this);
    var _super=new K.Node({parent:(args=args||{}).parent});
    var pvt={
      type:args.type,
      childNodes:[]
    };
    
    function getStack(){
      var result=[];
      
      function addToResult(itm){
        result.push(itm);
      }
      
      switch(pvt.type){
        case V.CONSTANT:
          result=[[V.STRING,"['",pvt.childNodes[0],"']"].join("")];
          break;
        case V.OPERATION:
          result.push(pvt.operation);
          pvt.childNodes[0].getStack().forEach(addToResult);
          pvt.childNodes[1].getStack().forEach(addToResult);
          break;
        case V.IDENTIFIER:
          result=[[K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("")];
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
    
    function getValues(){
      var arr=pvt.childNodes;
      return arr.slice(0,arr.length);
    }
    
    function toString(){
      var result="";
      return result;
    }
    
    function getParent(){
      return _super.parent;
    }
    
    function setParent(itm){
      args.parent=_super.parent=itm;
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
        console.error("replace Child BooleanNode");
        throw 0;
      }
      if(!_new instanceof _this.getClass()){
        console.error("replace Child BooleanNode");
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
    
    function updateParent(node){
      if (node instanceof K.Node){
        node.parent=_this;
      }
    }
    
    function renderOperationDOM(){
      var dom=_this.getDOM();
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({text:K.getMessage(lbl.PLUS_OP),classes:[V.TEXT,V.OPER],hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
      dom.classList.add(pvt.operation);
    }
    
    function renderValueDOM(){
      var dom=_this.getDOM();
      var _text;
      if (pvt.type===V.IDENTIFIER){
        _text=["[",pvt.childNodes[0],"]"].join("");
      } else {
        _text=pvt.childNodes[0];
        if(_text.trim()===""){
          _text="\u2000";
        }
      }
      dom.appendChild(K.createDOM({text:_text,hasToolbar:true}));
      dom.classList.add(V.VALUE);
    }
    
    function renderExpressionDOM(){
      var dom=_this.getDOM();
      dom.appendChild(K.createDOM({type:V.DIV,text:K.getMessage(lbl.IF),classes:[V.TEXT],hasToolbar:true}));
      updateParent(pvt.childNodes[0]);
      dom.appendChild(K.createDOM({type:V.DIV,text:K.getMessage(lbl.THEN),classes:[V.TEXT],hasToolbar:true}));
      updateParent(pvt.childNodes[1]);
      dom.appendChild(K.createDOM({type:V.DIV,text:K.getMessage(lbl.ELSE),classes:[V.TEXT],hasToolbar:true}));
      updateParent(pvt.childNodes[2]);
      
      dom.classList.add(K._.EXPRESSION);
    }
    
    function toolbarItemClick(evt){
      var val;
      var dtType=evt.target[K._.DT_TYPE];
      switch(dtType){
        case V.OPERATION:
          setOperation({name:V.STR_OPER},resolveOperationValues(evt.target[K._.DT_VAL]));
          break;
        case V.IDENTIFIER:
          init(args={type:dtType,value:[[K._.IDENT_STR,"[",evt.target[K._.DT_VAL],"]"].join("")]});
          break;
        case V.CONSTANT:
          val=prompt(K.getMessage(lbl.PROMPT));
          if (val !== null){
            init(args={type:dtType,value:[[V.STRING,"['",val,"']"].join("")]});
          }
          break;
        case V.EXPRESSION:
          setExpression(new K.BooleanNode(),new StringNode(args),new StringNode());
          break;
        default:
          replaceParent();
          break;
      }
      _this.getDOM().dispatchEvent(new CustomEvent("selected",{bubbles:true,cancelable:true,detail:{}}));
    }
    
    function resolveOperValue(flag){
      var result;
      if((flag&K._.TYPE_BOOL)===K._.TYPE_BOOL){
        result=new K.BooleanNode();
      }else if((flag&K._.TYPE_STR)===K._.TYPE_STR){
        result=new StringNode();
      }else if((flag&K._.TYPE_NBR)===K._.TYPE_NBR){
        result=new K.ArithNode();
      }else{
        console.error("err");
        throw 0;
      }
      return result;
    }
    
    function resolveOperationValues(flag){
      var values=[];
      if ((flag&V.EXPRESSION)===V.EXPRESSION){
        values.push(new StringNode(args));
        values.push(resolveOperValue(flag));
      }else{
        values.push(resolveOperValue(flag));
        values.push(new StringNode(args));
      }
      return values;
    }
    
    function setExpression(condition,value1,value2){
      init(args={type:V.EXPRESSION,condition:condition,value:[value1,value2]});
    }
  
    function setOperation(oper,values){
      init(args={operation:oper.name,type:V.OPERATION,value:values.slice(0,2)});
    }
    
    function getVariableSubMenu(){
      var _=K.Node;
      var variables=_.getVariables(_.VariableType.STRING);
      var items=[];
      for(var i=0,l=variables.length;i<l;i++){
        items.push({text:variables[i],click:toolbarItemClick,data:{type:V.IDENTIFIER,value:variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE],items:items,text:K.getMessage("StringNode.var")});
    }
      
    function initToolbar(){
      var itms=[
        new K.Toolbar({text:K.getMessage(lbl.BEFORE),classes:[K._.TEXT_TYPE],items:[
          {text:K.getMessage(lbl.BOOL),click:toolbarItemClick,data:{type:V.OPERATION,value:K._.TYPE_BOOL}},
          {text:K.getMessage(lbl.STR),click:toolbarItemClick,data:{type:V.OPERATION,value:K._.TYPE_STR}},
          {text:K.getMessage(lbl.NBR),click:toolbarItemClick,data:{type:V.OPERATION,value:K._.TYPE_NBR}}
        ]}),
        new K.Toolbar({text:K.getMessage(lbl.AFTER),classes:[K._.TEXT_TYPE],items:[
          {text:K.getMessage(lbl.BOOL),click:toolbarItemClick,data:{type:V.OPERATION,value:K._.TYPE_BOOL|V.EXPRESSION}},
          {text:K.getMessage(lbl.STR),click:toolbarItemClick,data:{type:V.OPERATION,value:K._.TYPE_STR|V.EXPRESSION}},
          {text:K.getMessage(lbl.NBR),click:toolbarItemClick,data:{type:V.OPERATION,value:K._.TYPE_NBR|V.EXPRESSION}}
        ]}),
        {type:"hr",classes:[]},
        getVariableSubMenu(),
        {text:K.getMessage(lbl.CONST),click:toolbarItemClick,data:{type:V.CONSTANT}},
        {type:"hr",classes:[]},
        {text:K.getMessage(lbl.EXPR),click:toolbarItemClick,data:{type:V.EXPRESSION}},
      ];
      
      if (getParent() instanceof _this.getClass()){
        itms.push({type:"hr",classes:[]});
        itms.push({parent:toolbar,text:K.getMessage(lbl.OVERRIDE),click:toolbarItemClick});
      }
      
      pvt.toolbar=new K.Toolbar({parent:_this.getDOM(),classes:[K._.TOOLBAR,K._.VALUE],items:itms});
      _this.getDOM()[K._.DATA_TBR]=pvt.toolbar;
    }
    
    function clearToolbar(){
      if (pvt.toolbar){
        pvt.toolbar.clear();
        delete pvt.toolbar;
      }
    }

    function init(param){
      clear();
      pvt.type=param.type||V.CONSTANT;
      switch(pvt.type){
        case V.OPERATION:
          pvt.operation=param.operation;
          pvt.childNodes.push(param.value[0]);
          pvt.childNodes.push(param.value[1]);
          pvt.childNodes[0].parent=_this;
          pvt.childNodes[1].parent=_this;
          pvt.renderDOM=renderOperationDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(K._.REGX_IDENT.exec(param.value[0])[1]);
          pvt.renderDOM=renderValueDOM;
          break;
        case V.CONSTANT:
          param.value=param.value||[];
          param.value[0]=param.value[0]||[V.STRING,"['']"].join("");
          console.log(param.value);
          pvt.childNodes.push(V.REGX_STR.exec(param.value[0])[1]);
          pvt.renderDOM=renderValueDOM;
          break;
        case V.EXPRESSION:
          if (param.condition.getClass() !== K.BooleanNode){
            param.condition = new K.BooleanNode();
          }
          pvt.childNodes.push(param.condition);
          pvt.childNodes.push(param.value[0]);
          pvt.childNodes.push(param.value[1]);
          pvt.childNodes[0].parent=_this;
          pvt.childNodes[1].parent=_this;
          pvt.childNodes[2].parent=_this;
          pvt.renderDOM=renderExpressionDOM;
          break;
        default:
          console.error("Invalid StringNode type");
          throw 0;
      }
      var dom=_this.getDOM();
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
        value:_super.getDOM,
        enumerable:true
      },
      toString:{
        value:toString,
        enumerable:true
      },
      getStack:{
        value:getStack,
        enumerable:true
      },
      clear:{
        value:clear,
        enumerable:true
      },
      initToolbar:{
        value:initToolbar,
        enumerable:true
      },
      clearToolbar:{
        value:clearToolbar,
        enumerable:true
      },
      replaceParent:{
        value:replaceParent,
        enumerable:true
      },
      replaceChild:{
        value:replaceChild,
        enumerable:true
      },
      attachInput:{
        value:_super.attachInput,
        enumerable:true
      }
    });
    init(args);
  }
  
  StringNode.prototype=new K.Node();
  Object.defineProperties(StringNode.prototype,{
    getClass:{
      value:function getClass(){
        return StringNode;
      }
    },
    valueOf:{
      value:function valueOf(){
        return K._.TYPE_STR;
      }
    }
  });
  
  function isIdentifier(str){
    var res=K._.REGX_IDENT.exec(str);
    return res!==null && K.Node.getVariables().indexOf(res[1])>=0;
  }
  
  function isStringNode(str){
    var _=K.Node;
    return (/^String\['.*'\]|Plus|Choice$/).test(str)
            || isIdentifier(str);
  }
  
  function getStringNodeType(str){
    var type;
    var _=K.Node;
    if ((/^Plus$/).test(str)){
      type=V.OPERATION;
    } else if ((/^Choice$/).test(str)){
      type=V.EXPRESSION;
    } else if ((/^String\['.*'\]$/).test(str)){
      type=V.CONSTANT;
    } else if (isIdentifier(str)){
      type=V.IDENTIFIER;
    } else {
      type=0x0;
    }
    return type;
  }
  
  Object.defineProperties(StringNode,{
    CONSTANT:{get:function(){return V.CONSTANT;}},
    OPERATION:{get:function(){return V.OPERATION;}},
    IDENTIFIER:{get:function(){return V.IDENTIFIER;}},
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
      get:function(){
        return StringNode;
      }
    }
  });
  
})(window._parser);