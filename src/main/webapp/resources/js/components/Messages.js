namespace("infox.Messages",function Messages(args) {
	var timeout = args.timeout || 1;
	var existsMessages = args.existsMessages || false;
	var existsGlobalMessages = args.existsGlobalMessages || false;
	var Messages = Messages || {
		showDialog:$_showDialog,
		hideDialog:$_hideDialog,
		init:$_init
	};
	
	function $_showDialog() {
		$(".d-msg").removeClass("hidden");
	}
	
	function $_hideDialog() {
		$(".d-msg").addClass("hidden");
	}
	
	function $_init() {
		$(".d-msg-h-close").click($_showDialog);
		if (existsGlobalMessages 
				&& existsMessages
				&& $(".d-msg-c").text().trim()!== "") {
			$_showDialog();
			if ($('.rf-msgs-err').html() == null) {
				setTimeout($_hideDialog, timeout);
			}
		}
	}
	return Messages;
});