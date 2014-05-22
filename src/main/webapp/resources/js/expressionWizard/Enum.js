(function(K) {
  function Enum(name) {
    if (this === window) {
      throw K.CONSTR_EXCEP;
    }
    
    Object.defineProperties(this, {
      name:{
        get:function getName() {
          return name;
        }
      }
    });
  }
  
  function valueOf() {
    throw K.IMPL_REQ_EXCEP;
  }
  
  function toString() {
    throw K.IMPL_REQ_EXCEP;
  }
  
  Enum.prototype = {
    get ordinal() {
      throw K.IMPL_REQ_EXCEP;
    },
    get name() {
      throw K.IMPL_REQ_EXCEP;
    },
    get valueOf(){return valueOf;},
    get toString(){return toString;}
  };
  
  Object.defineProperties(K,{
    Enum:{
      get:function() {
        return Enum;
      }
    }
  });
  
})(window._parser = window._parser || {});