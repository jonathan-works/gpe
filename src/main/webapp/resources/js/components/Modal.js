namespace("infox.Modal",function Modal(args){
	if (typeof args === "undefined") {
		throw"function infox.Modal(args:object) expects an object as parameter";
	}
	if (!args.id) {
		throw"function infox.Modal(args:object) args object requires at least an id property";
	}
	var instances = instances || {};
	var $modalId = args.id;
	var result = instances[$modalId] || {
		get instances() {
			return instances;
		},
		show:function() {
			RichFaces.$($modalId).show();
		},
		hide:function() {
			RichFaces.$($modalId).hide();
		}
	};
	return result;
});