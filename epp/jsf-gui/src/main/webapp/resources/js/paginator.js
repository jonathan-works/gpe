function mapKeys() {
	jQuery(document).keydown(function paginator(event) {
		console.log(event);
		if (event.keyCode===36) {
			RichFaces.$('documento:docScroller').first();
		}
		if (event.keyCode===37) {
			RichFaces.$('documento:docScroller').previous();
		}
		if (event.keyCode===39) {
			RichFaces.$('documento:docScroller').next();
		}
		if (event.keyCode===35) {
			RichFaces.$('documento:docScroller').last();
		}
	});
}
	 
jQuery(document).ready(init);

function pageFocus(){
  jQuery("#areaPagina").focus();
}			 

function init() {
  var sheets = document.styleSheets;
  for (var i = 0; i < sheets.length; i++) {
	 if (sheets[i] != null && sheets[i].href != null && sheets[i].href.indexOf("paginator.css") != -1) {
	   if (sheets[i].cssRules) {
	     rules = sheets[i].cssRules;
	    } else {
	      isIE = true;
	      rules = document.styleSheets[i].rules;
	    }
	    break;
	  }
  }

	ttab = $('documento:documentosGrid').offsetTop
	tsc = $('documento:docScroller').offsetTop + $('documento:docScroller').offsetHeight
	h= tsc - ttab;

  // ====== ATENCAO: COLOQUE O NOME DA CLASSE SEMPRE EM MINUSCULAS =============
  for (var i = 0; i< rules.length; i++) {
	rule = rules[i].selectorText.toLowerCase();
    if (rule == '#areapagina') {
    	rules[i].style.height = h - 45 + "px";
    }
    if (rule == '#pagina') {
    	rules[i].style.minHeight = h - 65 + "px";
    }
  }
  
  mapKeys();
  pageFocus(); 
}