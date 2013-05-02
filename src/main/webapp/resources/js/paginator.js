jQuery(document).bind('keydown', 'home', function(){
		Event.fire(document.getElementById("documento:docScroller_table"), 'rich:datascroller:onscroll', {'page': 'first'})
	 });

jQuery(document).bind('keydown', 'left', function(){
		Event.fire(document.getElementById("documento:docScroller_table"), 'rich:datascroller:onscroll', {'page': 'fastrewind'})
	 });

jQuery(document).bind('keydown', 'right', function(){
		Event.fire(document.getElementById("documento:docScroller_table"), 'rich:datascroller:onscroll', {'page': 'fastforward'})
	 });

jQuery(document).bind('keydown', 'end', function(){
		Event.fire(document.getElementById("documento:docScroller_table"), 'rich:datascroller:onscroll', {'page': 'last'})
	 });
	 
jQuery(document).ready(init);

jQuery(document).ready(pageFocus);
	 
function pageFocus(){
  jQuery("#areaPagina").focus();
}			 

function init() {
  var sheets = document.styleSheets;
  for (var i = 0; i < sheets.length; i++) {
	 if (sheets[i].href.indexOf("paginator.css") != -1) {
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
  
}