(function(container) {
  
  function newLine() {
    var outputList = document.getElementsByClassName("output");
    var output;
    for(var i=0,l=outputList.length;i<l;i++) {
      output = outputList[i];
      output.appendChild(document.createElement("div"));
    }
  }

  function getCurrentLine(output) {
    var divs = output.getElementsByTagName("div");
    var line = divs.length-1;
    if (line < 0) {
      newLine();
      line = 0;
    }
    return divs[line];
  }

  function print() {
    var outputList = document.getElementsByClassName("output");
    var output;
    for(var j=0,n=outputList.length;j<n;j++) {
      output = outputList[j];
      var div = getCurrentLine(output);
      var span;
      for(var i=0,l=arguments.length;i<l;i++) {
        span = document.createElement("span");
        var text = ""+arguments[i];
        var trimmedText = text.trimLeft();
        span.style.padding = ["0 0 0 ",(text.length - trimmedText.length)*10, "px"].join("");
        span.textContent = trimmedText;
        div.appendChild(span);
      }
    }
  }

  function println() {
    for(var i=0,l=arguments.length;i<l;i++) {
      print(arguments[i]);
    }
    newLine();
  }

  function clearOutput() {
    var outputList = document.getElementsByClassName("output");
    var output;
    for(var i=0,l=outputList.length;i<l;i++) {
      output = outputList[i];
      while(output.childElementCount>0) {
        output.removeChild(output.children[0]);
      }
      newLine();
    }
  }

  function putSpaces(depth) {
    for(var i=0; i<depth; i++) {
      print("  ");
    }
  }

  function internal_dump(variable, depth) {
    for(var key in variable) {
      
      var itemType = typeof variable[key];
      putSpaces(depth);
      print(key,":");
      if (itemType === "object") {
        newLine();
        internal_dump(variable[key], depth+1);
      } else if (itemType === "function") {
        println(variable[key].name);
      } else {
        println(variable[key]);
      }
    }
  }

  function var_dump() {
    for(var i=0,l=arguments.length;i<l;i++) {
      internal_dump(arguments[i],0);
    }
  }
  Object.defineProperties(container, {
    println:{
      get:function() {
        return println;
      }
    },
    print:{
      get:function() {
        return print;
      }
    },
    newLine:{
      get:function() {
        return newLine;
      }
    },
    clearOutput:{
      get:function() {
        return clearOutput;
      }
    },
    var_dump:{
      get:function() {
        return var_dump;
      }
    }
  });
})(window);