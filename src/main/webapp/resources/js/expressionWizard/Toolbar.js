(function (K) {
  if (K.Toolbar !== undefined){
    console.log("Toolbar already loaded");
    return;
  }
  var V = {
    UL:"ul",
    LI:"li",
    ACTION:"Action"
  };
  
  function Toolbar(args) {
    var _this = K.checkInit(this);
    var pvt = {
      classes:[K._.TOOLBAR],
      visible:false,
      data:{},
      items:[]
    };
    
    function initClasses(classes) {
      if (typeof classes !== K._.UNDEF) {
        for(var i=0,l=classes.length;i<l;i++) {
          pvt.classes.push(classes[i]);
        }
      }
    }
    
    function initItems(items) {
      if (typeof items !== K._.UNDEF) {
        for(var i=0,l=items.length;i<l;i++) {
          pvt.items.push(items[i]);
        }
      }
    }
    
    function mouseLeave(evt) {
      var parent = evt.target.parentNode;
      if (typeof parent[K._.DATA_TBR] !== K._.UNDEF) {
        parent[K._.DATA_TBR].clear();
      }
      parent.classList.remove(K._.CSS_SEL_ND);
    }
    
    function draw(x, y) {
      if (pvt.visible) {
        clear();
      }
      if (typeof pvt.parent !== K._.UNDEF) {
        pvt.dom = K.createDOM({type:V.UL, classes:pvt.classes, parent:pvt.parent, data:pvt.data, mouseLeave:mouseLeave});
        if (typeof x !== K._.UNDEF) {
          pvt.dom.style.left = x+'px';
        }
        if (typeof y !== K._.UNDEF) {
          pvt.dom.style.top = y+'px';
        }
        pvt.visible = true;
        appendMenuItems();
      }
    }
    
    function appendMenuItems() {
      var itm;
      var newItm;
      for(var i=0,l=pvt.items.length;i<l;i++) {
        itm = pvt.items[i];
        newItm = new K.createDOM({type:itm.type||V.LI, classes:itm.classes||[V.ACTION], click:itm.click, text:itm.text||"", parent:pvt.dom, data:itm.data||{}});
        newItm.classList.add(K._.TOOLBAR_ITM);
        if (itm instanceof Toolbar) {
          itm.parent = newItm;
          itm.draw();
        } else {
          itm.dom = newItm;
        }
      }
    }
    
    function isVisible() {
      return pvt.visible;
    }
    
    function clear() {
      if (pvt.visible) {
        if (pvt.dom) {
          if (pvt.dom.parentNode) {
            pvt.dom.parentNode.classList.remove(K._.CSS_SEL_ND);
          }
          pvt.dom.remove();
          delete pvt.dom;
        }
        pvt.visible = false;
      }
    }
    
    function setParent(parent) {
      pvt.parent = parent;
      if (parent instanceof Element) {
        if (pvt.visible) {
          parent.appendChild(pvt.dom);
        }
      } else if (parent instanceof Toolbar && parent.visible) {
        draw();
      }
    }
    function getParent() {
      return pvt.parent;
    }
    
    function getText() {
      return pvt.text;
    }
    function setText(itm) {
      pvt.text=itm;
    }
    
    /* public methods and properties*/
    Object.defineProperties(_this,{
      parent:{
        get:getParent,set:setParent
      },
      draw:{
        value:draw
      },
      clear:{
        value:clear
      },
      visible:{
        get:isVisible
      },
      text:{
        get:getText,set:setText
      }
    });
    initClasses(args.classes);
    initItems(args.items);
    pvt.type=V.UL;
    setParent(args.parent);
    pvt.data=args.data;
    pvt.mouseLeave=args.mouseLeave;
    if (typeof args.isImage !== K._.UNDEF && args.isImage && typeof args.src !== K._.UNDEF) {
      initImage(args.src);
    }
    pvt.text=args.text;
  }
  
  /* public static methods */
  Object.defineProperties(Toolbar,{
    
  });
  
  Object.defineProperties(K,{
    Toolbar:{
      get:function() {
        return Toolbar;
      }
    }
  });
  
})(window._parser = window._parser || {});