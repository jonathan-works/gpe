(function(K){
  if (K.BooleanNode !== undefined){
    console.log("BooleanNode already loaded");
    return;
  }
  
  var V={
    get NAME(){return "BooleanNode";},
    get TRUE(){return "True";},
    get FALSE(){return "False";},
    get CONSTANT(){return 0x1;},
    get OPERATION(){return 0x2;},
    get IDENTIFIER(){return 0x4;},
    get NOT(){return 0x8;},
    get EXPRESSION(){return 0x10;},
    get OVERRIDE(){return 0x20;},
    get TYPE_EXCEP(){return "Type Exception";}
  };
  
  var lbl={
    get negate   (){return [V.NAME,"negate"].join(".");},
    get and      (){return [V.NAME,"and"].join(".");},
    get or       (){return [V.NAME,"or"].join(".");},
    get eq       (){return [V.NAME,"eq"].join(".");},
    get neq      (){return [V.NAME,"neq"].join(".");},
    get gte      (){return [V.NAME,"gte"].join(".");},
    get gt       (){return [V.NAME,"gt"].join(".");},
    get lte      (){return [V.NAME,"lte"].join(".");},
    get lt       (){return [V.NAME,"lt"].join(".");},
    get var      (){return [V.NAME,"var"].join(".");},
    get TRUE     (){return [V.NAME,"TRUE"].join(".");},
    get FALSE    (){return [V.NAME,"FALSE"].join(".");},
    get ARIT     (){return [V.NAME,"ARIT"].join(".");},
    get STR_COMP (){return [V.NAME,"STR_COMP"].join(".");},
    get EXPR     (){return [V.NAME,"EXPR"].join(".");},
    get COMPARE  (){return [V.NAME,"COMPARATION"].join(".");},
    get OVERRIDE (){return [V.NAME,"OVERRIDE"].join(".");}
  };
  
  function getFunctionObject(obj){
    var result={};
    for(var key in obj){
      Object.defineProperty(result, key, Object.getOwnPropertyDescriptor(obj,key));
    }
    return result;
  }
  
  function replaceParentFunction(parent,child){
    for(var key in parent){
      Object.defineProperty(parent,key,Object.getOwnPropertyDescriptor(child,key));
    }
    return parent;
  }
  
  function BooleanNode(args){
    var _this=K.checkInit(this);
    var pvt={
      childNodes:[]
    };
    var _super=new K.Node({parent:(args=args||{}).parent});
    var uber=getFunctionObject(_super);
    
    function getStack(){
      var result=[];
      var appendArray=function(itm){
        result.push(itm);
      };
      switch(pvt.type){
        case V.CONSTANT:
          result=[pvt.childNodes[0].slice(0,1).toUpperCase()+pvt.childNodes[0].slice(1,pvt.childNodes[0].length)];
          break;
        case V.OPERATION:
          result.push(pvt.operation.name);
          pvt.childNodes[0].getStack().forEach(appendArray);
          pvt.childNodes[1].getStack().forEach(appendArray);
          break;
        case V.IDENTIFIER:
          result=[[K._.IDENT_STR,"[",pvt.childNodes[0],"]"].join("")];
          break;
        case V.NOT:
          result.push(pvt.operation.name);
          pvt.childNodes[0].getStack().forEach(appendArray);
          break;
      }
      return result;
    }
    
    function toString(){
      return "";
    }
    
    function clear(){
      var itm;
      while(pvt.childNodes.length>0){
        itm=pvt.childNodes.pop();
        if(itm instanceof Node){
          itm.clear();
        }
      }
      uber.clear();
    }
    
    function getType(){
      return pvt.type;
    }
    
    function getOperation(){
      return pvt.operation;
    }
    
    function getValues(){
      return pvt.childNodes.slice(0, pvt.childNodes.length);
    }
    
    function getParent(){
      return uber.parent;
    }
    function setParent(itm){
      args.parent=uber.parent=itm;
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
    
    function updateParent(node){
      if(node instanceof K.Node){
        node.parent=_this;
      }
    }
    
    function genericClickEvent(evt){
      var result="";
      var _=K.BooleanOper;
      switch(evt.target[K._.DT_TYPE]){
        case V.OPERATION:
          setOperation(evt.target[K._.DATA_OPER], retrieveOperationValues(evt.target[K._.DATA_OPER],evt.target[K._.DT_VAL]));
          break;
        case V.IDENTIFIER:
          setIdentifier([K._.IDENT_STR,"[",evt.target[K._.DT_VAL],"]"].join(""));
          break;
        case V.CONSTANT:
          setConstant(evt.target[K._.DT_VAL]);
          break;
        case V.NOT:
          negate();
          break;
        default:
          evt.target[K._.DT_VAL].replaceParent();
          clearToolbar();
          break;
      }
      _this.getDOM().dispatchEvent(new CustomEvent("selected",{bubbles:true,cancelable:true,detail:{}}));
    }
    
    function retrieveOperationValues(oper,childrentype) {
      var _=K.BooleanOper;
      switch(oper){
        case _.AND:
        case _.OR:
          if(pvt.type!==V.OPERATION||(pvt.operation!==_.AND&&pvt.operation!==_.OR)){
            return [new BooleanNode(args),new BooleanNode({value:[oper===_.AND?V.TRUE:V.FALSE]})];
          }
          break;
        case _.GT:
        case _.GTE:
        case _.LT:
        case _.LTE:
          if(pvt.type!==V.OPERATION||!(pvt.childNodes[0] instanceof K.ArithNode&&pvt.childNodes[1] instanceof K.ArithNode)){
            return [new K.ArithNode(),new K.ArithNode()];
          }
          break;
        case _.EQ:
        case _.NEQ:
          if((childrentype||K._.TYPE_NBR)===K._.TYPE_NBR){
            if(pvt.type!==V.OPERATION||!(pvt.childNodes[0] instanceof K.ArithNode&&pvt.childNodes[1] instanceof K.ArithNode)){
              return [new K.ArithNode(),new K.ArithNode()];
            }
          }else{
            if (pvt.type!==V.OPERATION||!(pvt.childNodes[0] instanceof K.StringNode&&pvt.childNodes[1] instanceof K.StringNode)) {
              return [new K.StringNode(),new K.StringNode()];
            }
          }
          break;
      }
      return pvt.childNodes;
    }
    
    function setConstant(_value){
      init(args={value:[_value]});
    }
    
    function setIdentifier(_value){
      init(args={type:V.IDENTIFIER,value:[_value]});
    }
    
    function setOperation(oper,values){
      init(args={operation:oper.name, type:V.OPERATION, value:values.slice(0,2)});
    }
    
    function negate(){
      if(pvt.type===V.CONSTANT){
        var value=pvt.childNodes[0];
        if(value===V.TRUE){
          value=V.FALSE;
        }else if(value===V.FALSE){
          value=V.TRUE;
        }
        setConstant(value);
      }else if(getParent() instanceof BooleanNode && getParent().type===V.NOT){
        replaceParent();
      }else if(pvt.type===V.NOT){
        pvt.childNodes[0].replaceParent();
      }else{
        init(args={operation:K.BooleanOper.NOT.name,type:V.NOT,value:[new BooleanNode(args)]});
      }
    }
    
    function clearToolbar(){
      if(pvt.toolbar){
        pvt.toolbar.clear();
        delete pvt.toolbar;
      }
    }
    
    function getVariableSubMenu(){
      var _=K.Node;
      var variables=_.getVariables(_.VariableType.BOOLEAN);
      var items=[];
      for(var i=0, l=variables.length;i<l;i++){
        items.push({text:variables[i], click:genericClickEvent, data:{type:V.IDENTIFIER,value:variables[i]}});
      }
      return new K.Toolbar({classes:[K._.TEXT_TYPE], items:items, text:K.getMessage(lbl.var)});
    }
    
    function initToolbar(){
      clearToolbar();
      var _=K.BooleanOper;
      var boolOp=K.BooleanOper;
      var itms=[
        {text:K.getMessage(lbl.TRUE), click:genericClickEvent, data:{type:V.CONSTANT,value:V.TRUE}},
        {text:K.getMessage(lbl.FALSE), click:genericClickEvent, data:{type:V.CONSTANT,value:V.FALSE}},
        {text:"-", classes:[]},
        {text:K.getMessage(lbl.negate), click:genericClickEvent, data:{type:V.NOT}},
        {text:K.getMessage(lbl.and), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.AND}},
        {text:K.getMessage(lbl.or), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.OR}},
        {text:"-", classes:[]},
        new K.Toolbar({classes:[K._.TEXT_TYPE],text:K.getMessage(lbl.COMPARE),items:[
          new K.Toolbar({classes:[K._.TEXT_TYPE],text:K.getMessage(lbl.ARIT),items:[
            {text:K.getMessage(lbl.eq), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.EQ}},
            {text:K.getMessage(lbl.neq), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.NEQ}},
            {text:K.getMessage(lbl.gte), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.GTE}},
            {text:K.getMessage(lbl.gt), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.GT}},
            {text:K.getMessage(lbl.lte), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.LTE}},
            {text:K.getMessage(lbl.lt), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.LT}}
          ]}),
          new K.Toolbar({classes:[K._.TEXT_TYPE],text:K.getMessage(lbl.STR_COMP),items:[
            {text:K.getMessage(lbl.eq), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.EQ,value:K._.TYPE_STR}},
            {text:K.getMessage(lbl.neq), click:genericClickEvent, data:{type:V.OPERATION,operation:boolOp.NEQ,value:K._.TYPE_STR}}
          ]}),
        ]}),
        {text:"-", classes:[]}
      ];
      itms.push(getVariableSubMenu());
      
      if(getParent() instanceof _this.getClass()){
        itms.push({text:"-", classes:[]});
        itms.push({parent:toolbar, text:K.getMessage(lbl.OVERRIDE), click:genericClickEvent, data:{value:_this}});
      }
      _this.getDOM()[K._.DATA_TBR]=pvt.toolbar=new K.Toolbar({parent:_this.getDOM(), classes:[K._.TOOLBAR,K._.VALUE],items:itms});
    }
    
    function renderOperationDOM(){
      var dom=_this.getDOM();
      dom.classList.add(pvt.operation.name);
      
      K.createDOM({text:"(", classes:[K._.TEXT], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[0]);
      K.createDOM({text:pvt.operation.label, classes:[K._.TEXT, K._.OPER], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[1]);
      K.createDOM({text:")", classes:[K._.TEXT] , parent:dom, hasToolbar:true});
    }
    
    function renderNegationDOM(){
      var dom=_this.getDOM();
      dom.classList.add(pvt.operation.name);
      K.createDOM({text:pvt.operation.label, classes:[K._.TEXT, K._.OPER], parent:dom, hasToolbar:true});
      updateParent(pvt.childNodes[0]);
    }
    
    function renderValueDOM(){
      var dom=_this.getDOM();
      dom.classList.add(K._.VALUE);
      
      var _text;
      if(pvt.type===V.IDENTIFIER){
        _text=["[",pvt.childNodes[0],"]"].join("");
      }else{
        if(pvt.childNodes[0]===V.TRUE){
            _text=K.getMessage(lbl.TRUE);
          }else if(pvt.childNodes[0]===V.FALSE){
            _text=K.getMessage(lbl.FALSE);
          }
      }
      K.createDOM({text:_text, classes:[V.NAME,K._.TEXT, K._.VALUE], parent:dom, hasToolbar:true});
    }
    
    function init(args){
      clear();
      pvt.type=args.type||V.CONSTANT;
      args.value=args.value||[];
      switch(pvt.type){
        case V.OPERATION:
          pvt.operation=K.BooleanOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes.push(args.value[1]);
          pvt.childNodes[0].parent=_this;
          pvt.childNodes[1].parent=_this;
          pvt.renderDOM=renderOperationDOM;
          break;
        case V.NOT:
          pvt.operation=K.BooleanOper.getValueOf(args.operation);
          pvt.childNodes.push(args.value[0]);
          pvt.childNodes[0].parent=_this;
          pvt.renderDOM=renderNegationDOM;
          break;
        case V.CONSTANT:
          pvt.childNodes.push(args.value[0]||V.TRUE);
          pvt.renderDOM=renderValueDOM;
          break;
        case V.IDENTIFIER:
          pvt.childNodes.push(args.value[0].slice(11,args.value[0].length-1));
          pvt.renderDOM=renderValueDOM;
          break;
        default:
          console.error(V.TYPE_EXCEP,args);
          throw 0;
      }
      var dom=_this.getDOM();
      dom.classList.add(V.NAME);
      dom[K._.DT_CLASS]=_this;
      
      pvt.renderDOM();
    }
  
    Object.defineProperties(_this,{
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
        value:getStack
      },
      clear:{
        value:clear
      },
      getDOM:{
        value:uber.getDOM
      },
      toString:{
        value:toString
      },
      initToolbar:{
        value:initToolbar
      },
      clearToolbar:{
        value:clearToolbar
      },
      replaceParent:{
        value:replaceParent
      },
      replaceChild:{
        value:replaceChild
      },
      attachInput:{
        value:uber.attachInput
      }
    });
    init(args);
    _super=replaceParentFunction(_super,_this);
  }
  
  BooleanNode.prototype=new K.Node();
  Object.defineProperties(BooleanNode.prototype,{
    valueOf:{
      value:function valueOf(){
        return K._.TYPE_BOOL;
      }
    },
    getClass:{
      value:function getClass(){
        return BooleanNode;
      }
    }
  });
  
  function isBooleanNode(str){
    var _=K.Node;
    return K.BooleanOper.isBooleanOper(str)||(K._.REGX_IDENT.test(str)&&_.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0)||(str===V.TRUE||str===V.FALSE);
  }
  
  function getBooleanNodeType(str){
    var type;
    var _=K.Node;
    if(K._.REGX_IDENT.test(str)&&_.getVariables(_.VariableType.BOOLEAN).indexOf(str.slice(11,str.length-1))>=0){
      type=V.IDENTIFIER;
    }else if(str===V.TRUE||str===V.FALSE){
      type=V.CONSTANT;
    }else if(K.BooleanOper.isBooleanOper(str)){
      type=K.BooleanOper.getValueOf(str);
      if(type===K.BooleanOper.NOT){
        type=V.NOT;
      }else{
        type=V.OPERATION;
      }
    }else{
      type=0x0;
    }
    return type;
  }

  function toString(){
    return this.name;
  }
  
  /*    CONSTANTES DA CLASSE    */
  Object.defineProperties(BooleanNode,{
    CONSTANT:{
      value:V.CONSTANT
    },
    OPERATION:{
      value:V.OPERATION
    },
    IDENTIFIER:{
      value:V.IDENTIFIER
    },
    NOT:{
      value:V.NOT
    },
    EXPRESSION:{
      value:V.EXPRESSION
    },
    isBooleanNode:{
      value:isBooleanNode
    },
    getBooleanNodeType:{
      value:getBooleanNodeType
    },
    toString:{
      value:toString
    }
  });
  
  Object.defineProperties(K,{
    BooleanNode:{
      value:BooleanNode
    }
  });
  return BooleanNode;
})(window._parser=window._parser||{});