namespace("infox.Modal",function Modal(args){
	var instances = namespace.infox.Modal.instances = namespace.infox.Modal.instances || {};
	var $modalId = args.id;
	var result = instances[args.id] || {
		show:function() {
			RichFaces.$($modalId).show();
		},
		hide:function() {
			RichFaces.$($modalId).hide();
		}
	};
	return result;
});