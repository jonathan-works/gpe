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
		if ($(".d-msg").attr('infox-dialog')) {
			return;
		}
		clearTimeout(timer);
		timer=false;
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
	
	function $_dialog(msg, modal, onhide, enableTimeout) {
		clearTimeout(timer);
		$(".d-msg").attr('infox-dialog', true);
		var modalDiv;
		if (modal) {
			modalDiv = $("<div></div>");
			modalDiv.insertBefore(".d-msg");
			modalDiv.addClass("d-msg-modal");
		}
		$(".d-msg-c").text(msg);
		$_showDialog();
		var hide = function () {
			$_hideDialog();
			$(".d-msg").attr('infox-dialog', false);
			if (onhide) {
				onhide();
			}
			if (modalDiv) {
				modalDiv.remove();
			}
		};
		$(".d-msg-h-close").off('click');
		$(".d-msg-h-close").click(hide);
		if (enableTimeout === undefined || enableTimeout) {
			timer = setTimeout(hide, timeout);
		}
	}
	return Messages;
});