(function(K) {
  function Enum(label, ordinal, name) {
    function getLabel() {
      return label;
    }
    
    function getOrdinal() {
      return ordinal;
    }
    
    function getName() {
      return name;
    }
    
    function valueOf() {
      return ordinal;
    }
    
    if (this !== window) {
      Object.defineProperties(this, {
        label:{
          get:getLabel
        },
        ordinal:{
          get:getOrdinal
        },
        name:{
          get:getName
        },
        valueOf:{
          get:function() {
            return valueOf;
          }
        }
      });
    }
  }
  
  Object.defineProperties(K,{
    Enum:{
      get:function() {
        return Enum;
      }
    }
  });
  
})(window._parser = window._parser || {});