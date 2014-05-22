(function(K){
  var V={
    get NAME()"BooleanNode",
    get TRUE()"True",
    get FALSE()"False",
    get CONSTANT()0x1,
    get OPERATION()0x2,
    get IDENTIFIER()0x4,
    get NOT()0x8,
    get EXPRESSION()0x10,
    get OVERRIDE()0x20,
    get TYPE_EXCEP()"Type Exception"
  };
  
  var lbl={
    get negate   ()[V.NAME,"negate"].join("."),
    get and      ()[V.NAME,"and"].join("."),
    get or       ()[V.NAME,"or"].join("."),
    get eq       ()[V.NAME,"eq"].join("."),
    get neq      ()[V.NAME,"neq"].join("."),
    get gte      ()[V.NAME,"gte"].join("."),
    get gt       ()[V.NAME,"gt"].join("."),
    get lte      ()[V.NAME,"lte"].join("."),
    get lt       ()[V.NAME,"lt"].join("."),
    get var      ()[V.NAME,"var"].join("."),
    get TRUE     ()[V.NAME,"TRUE"].join("."),
    get FALSE    ()[V.NAME,"FALSE"].join("."),
    get ARIT     ()[V.NAME,"ARIT"].join("."),
    get STR_COMP ()[V.NAME,"STR_COMP"].join("."),
    get EXPR     ()[V.NAME,"EXPR"].join("."),
    get COMPARE  ()[V.NAME,"COMPARATION"],
    get OVERRIDE ()[V.NAME,"OVERRIDE"].join(".")
  };
  
    
  function valueOf(){
    return K._.TYPE_BOOL;
  }
  
  function BooleanNode(args){
    var _this=K.checkInit(this);
    var _super=new K.Node({parent:(args=args||{}).parent});
    
    var pvt={
      childNodes : []
    };
    
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
      var result="";
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
    
    function getType(){
      return pvt.type;
    }
    
    function getOperation(){
      return pvt.operation;
    }
    
    function getValues(){
      return pvt.childNodes.slice(0, pvt.childNodes.length);
    }
    
    function getNodeType(){
      return V.NAME;
    }
    
    function getParent(){
      return _super.parent;
    }
    
    function setParent(itm){
      args.parent=_super.parent=itm;
      clearToolbar();
      initToolbar();
    }
    
    function replaceParent(){
      var _parent=getParent();
      var _gParent;
      if(_parent instanceof BooleanNode){
        _gParent=_parent.parent;
        if(_gParent instanceof K.Node){
          _gParent.replaceChild(_parent,_this);
        }else if(_gParent instanceof Element){
          _gParent.replaceChild(_this.getDOM(),_parent.getDOM());
        }
      }
      clearToolbar();
      initToolbar();
    }
    
    function replaceChild(_old,_new){
      var pos=pvt.childNodes.indexOf(_old);
      if(pos<0){
        console.error("");
        throw 0;
      }
      if(!_new instanceof BooleanNode){
        console.error("");
        throw 0;
      }
      _new.parent=_this;
      pvt.childNodes[pos]=_new;
      args.value=pvt.childNodes;
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
        case V.OVERRIDE:
          evt.target[K._.DT_VAL].replaceParent();
          break;
        default:
          break;
      }
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
      }else if(getParent().type===V.NOT){
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
      
      if(getParent() instanceof BooleanNode){
        itms.push({parent:toolbar, text:K.getMessage(lbl.OVERRIDE), click:genericClickEvent, data:{type:V.OVERRIDE,value:_this}});
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
          throw V.TYPE_EXCEP;
      }
      var dom=_this.getDOM();
      dom.classList.add(V.NAME);
      dom[K._.DT_CLASS]=_this;
      
      pvt.renderDOM();
      initToolbar();
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
    init(args);
  }
  
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
  
  BooleanNode.prototype=new K.Node();
  /*    CONSTANTES DA CLASSE    */
  Object.defineProperties(BooleanNode,{
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