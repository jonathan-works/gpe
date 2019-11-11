namespace("infox.PickList",function PickList(id, inputId) {
	if (typeof id !== 'string') {
		throw "ID["+id+"] type not supported";
	}
	if (typeof inputId !== 'string') {
		throw "InputID["+inputId+"] type not supported";
	}
	jQuery(inputId).keyup(function(){
		var list = id;
		var s = jQuery(this).val();
		jQuery(list + ' .rf-pick-opt').show();
		if (s.trim() != "") {
			jQuery(list + ' .rf-pick-opt:not(:Contains("' + s + '"))').hide();
		}
	});
});