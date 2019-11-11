/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
(function() {
  if(window.namespace) {
    throw"Object window.namespace is already declared";
  }
  var $library = {};
  var $loaded = false;
  var $execQueue = [];

  function loadScript(_path, _callback) {
    if(typeof _path !== "string") {
      throw"";
    }
    if(typeof _callback !== "function") {
      throw"";
    }
    $invoke([], function loadScript() {
      var script = document.createElement("script");
      script.addEventListener("load", _callback);
      script.src = _path;
      script.type = "text/javascript";
      document.head.appendChild(script);
    }, []);
  }
  
  function $getObjectFromPath(path) {
    var res = {
      path:"",
      object:{},
      get next() {
        return this.object[this.path];
      },
      set next(obj) {
        this.object[this.path] = obj;
      }
    };
    var $namespace = $library;
    for(var i=0,l=path.length;i<l;i++) {
      res.path = path[i];
      res.object = $namespace;
      if (i<l-1) {
        $namespace = res.next = res.next || {};
      }
    }
    return res;
  }
  
  function $create(_path, _object, _options) {
    var $nm = $getObjectFromPath(_path.split("."));
    _options = _options || {};
    _options.mergeType = _options.mergeType || "merge";
    
    if($nm.next !== _object) {
      if (_options.mergeType === "override") {
        $nm.next = _object;
      } else if (_options.mergeType === "merge") {
        $nm.next = $merge($nm.next, _object);
      } else {
        $nm.next = $nm.next || _object;
      }
    }
    $nm = $nm.next;
    if(typeof $nm === "function") {
      $nm.toString = function toString() {
        return _path;
      };
    }
    return $nm;
  }
  function $flushQueue() {
    var exec=$execQueue.shift();
    if (exec!==undefined){
      try {
        exec.func.apply(exec.func, $merge(exec.args, $loadDependencies(exec.dependencies)));
      }catch(e) {
        console.error(e,exec);
      }
    }
    if($execQueue.length > 0) {
      setTimeout($flushQueue, 1);
    }else {
      $loaded = true;
    }
  }
  function $mergeWeight(object) {
    var type = typeof object;
    if(type === "function") {
      return 4;
    }else {
      if(type === "object" && [].constructor === object.constructor) {
        return 2;
      }else {
        return 1;
      }
    }
  }
  function $processMerge(obj1, obj2) {
    for(var index in obj2) {
      if(obj2.hasOwnProperty(index) && !obj1.hasOwnProperty(index)) {
        obj1[index] = obj2[index];
      }
    }
    return obj1;
  }
  function $merge(obj1, obj2) {
    var w1 = $mergeWeight(obj1);
    var w2 = $mergeWeight(obj2);
    return w1 > w2 ? $processMerge(obj1, obj2) : $processMerge(obj2, obj1);
  }
  function $extend(_object, _path) {
  var nm = $getObjectFromPath(_path.split("."));
    if(!nm.next) {
      throw"Parent object not found in path " + _path;
    }
    _object.uber = nm.next;
    return _object;
  }
  function $loadDependencies(dependencies) {
    var loaded = [];
    for(var i = 0, arrlength = dependencies.length;i < arrlength;i++) {
      if(typeof dependencies[i] !== "string") {
        throw"Dependencies array contains an invalid type, must contain strings only. Contains " + typeof dependencies[i];
      }
      loaded.push($getObjectFromPath(dependencies[i].split(".")).next);
    }
    return loaded;
  }
  function $invoke(dependencies, callback, params) {
    if(typeof dependencies !== "object" || dependencies.constructor !== params.constructor) {
      throw"Dependencies argument is of the wrong type, must be an array. Is " + typeof dependencies;
    }
    if(typeof callback !== "function") {
      throw"Callback argument is of the wrong type, must be a function. Is " + typeof callback;
    }
    if($loaded) {
      callback.apply(callback, $merge(params, $loadDependencies(dependencies)));
    }else {
      $execQueue.push({func:callback, args:params, dependencies:dependencies});
    }
  }
  function invoke(dependencies, callback) {
    $invoke(dependencies, callback, []);
  }
  function namespace(path, obj, args) {
    var params = [];
    if(typeof path !== "string") {
      throw"Path argument is of the wrong type, must be string. Is " + typeof path;
    }
    if(typeof obj !== "function" && typeof obj !== "object") {
      throw"Object argument is of the wrong type, must be object or function. Is " + typeof obj;
    }
    if(args) {
      if(args.callback) {
        $invoke(args.required||[], function populate() {
          for(var i = 0, l = arguments.length;i < l;i++) {
            params.push(arguments[i]);
          }
        }, []);
        params.push(obj);
        $invoke([], $create, [path, obj, args]);
        $invoke([], args.callback, params);
        return 0;
      }
    }
    $invoke([], $create, [path, obj, args]);
    return 0;
  }
  function toString(){
    return this.name;
  }
  Object.defineProperty(loadScript,"toString",{value:toString});
  Object.defineProperty(namespace,"toString",{value:toString});
  Object.defineProperty(invoke,"toString",{value:toString});
  Object.defineProperties(window,{
    namespace:{
      value:namespace
    },
    invoke:{
      value:invoke
    },
    loadScript:{
      value:loadScript
    }
  });
  window.addEventListener("load", $flushQueue);
})();