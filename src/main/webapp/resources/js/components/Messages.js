namespace("infox.Messages",function Messages(args) {
	var timeout = args.timeout || 1;
	var existsMessages = args.existsMessages || false;
	var existsGlobalMessages = args.existsGlobalMessages || false;
	var timer = timer || false;
	var Messages = Messages || {
		showDialog:$_showDialog,
		hideDialog:$_hideDialog,
		init:$_init,
		dialog:$_dialog,
		get isHidden() {
			return $(".d-msg.hidden").size()>0; 
		}
	};
	
	function $_showDialog() {
		$(".d-msg").removeClass("hidden");
	}
	
	function $_hideDialog() {
		clearTimeout(timer);
		timer=false;
		$(".d-msg-c").text("");
		$(".d-msg").addClass("hidden");
	}
	
	function $_init() {
		$(".d-msg-h-close").click($_hideDialog);
		if (existsGlobalMessages 
				&& existsMessages
				&& $(".d-msg-c").text().trim()!== "") {
			$_showDialog();
			if ($('.rf-msgs-err').html() == null) {
				timer = setTimeout($_hideDialog, timeout);
			}
		}
	}
	
	function $_dialog(msg) {
		clearTimeout(timer);
		$(".d-msg-c").text(msg);
		$_showDialog();
		timer = setTimeout($_hideDialog, timeout);
			$(".d-msg").attr('infox-dialog', false);
	}
	return Messages;
});