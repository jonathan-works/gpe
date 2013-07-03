// função contains sem casesensitive
window.addEventListener("load", function() {
	jQuery.expr[':'].Contains = function(a,i,m){
		var index = jQuery(a).text().toUpperCase().indexOf(m[3].toUpperCase());
		return index>=0;
	}; 
});