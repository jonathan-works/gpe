function defineObject(path, object, basePath) {
  var packages = path.split(".");
  var _namespace = basePath || window;
  for(var i=0; i<packages.length; i++) {
    var item = packages[i];
    if (i==packages.length-1) {
      _namespace = _namespace[item] = _namespace[item] || object;
    } else {
      _namespace = _namespace[item] = _namespace[item] || {};
    }
  }
  return _namespace;
}

function existsObject(path, basePath) {
	var packages = path.split(".");
	var _namespace = basePath || window;
	var result = true;
	for(var i=0; i<packages.length; i++) {
		var item = packages[i];
		if (!_namespace[item]) {
			return false;
		}
		_namespace = _namespace[item];
	}
	return true;
}

namespace("infox.util",{
	applyCSS:function(args) {
		setTimeout(function(){
			$(args.selector).css(args.style);
		},args.delay || 1);
	}
});

defineObject("br.com.infox.applyCSSAfterTimeout", function(args) {
	if (args.id) {
		var $id = args.id;
		var $style = args.style || {};
		var $timeout = args.timeout || 5000;
		$($id).each(function() {
			setTimeout(function() {
				$(this).css($style);
			},$timeout);
		});
	}
});

function showLoading() {	
	if ($('#status')) {
		$('#status').hide();
	}
	RichFaces.$('modalStatus').show();
}

function hideLoading() {
	RichFaces.$('modalStatus').hide();
	if ($('#status')) {
		$('#status').show();
	}
}

function refreshOpener() {
	try {
	   opener.refreshPage();
	} catch (e) {}
}

if (!$.fn.clearForm) {
	$.fn.clearForm = function() {
		return this.each(function() {
			var type = this.type; 
			var tag = this.tagName.toLowerCase();
			if (tag == 'form')
				return $(':input',this).clearForm();
			if (type == 'text' || type == 'password' || tag == 'textarea')
				this.value = '';
			else if (type == 'checkbox' || type == 'radio')
				this.checked = false;
			else if (tag == 'select')
				this.selectedIndex = 0;
		});
	}	
}

// Padronização das telas de popUps
function openPopUp(id, url, width, height) {
	if (!width) width = 800;
	if (!height) height = 600;
	var featurePopUp = "width="+width + ", height="+height+", resizable=YES, scrollbars=YES, status=NO, location=NO";
	var popUp = window.open(url, id, featurePopUp);	
	popUp.moveTo(0, 0);
}