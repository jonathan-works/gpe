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
  
  Enum.prototype = {
    get ordinal() {
      throw K.IMPL_REQ_EXCEP;
    },
    valueOf:function valueOf() {
      throw K.IMPL_REQ_EXCEP;
    },
    get name() {
      throw K.IMPL_REQ_EXCEP;
    }
  };
  
  Object.defineProperties(K,{
    Enum:{
      get:function() {
        return Enum;
      }
    }
  });
  
})(window._parser = window._parser || {});