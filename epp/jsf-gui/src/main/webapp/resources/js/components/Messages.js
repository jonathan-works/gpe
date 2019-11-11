namespace("infox.Messages", function Messages(args) {
    args = args || {}
	var timeout = args.timeout || -1;
	var existsMessages = args.existsMessages || false;
	var existsGlobalMessages = args.existsGlobalMessages || false;
	var timer = timer || false;

	var Messages = Messages || {
		showDialog:$_showDialog,
		hideDialog:$_hideDialog,
		init:$_init,
		dialog:$_textDialog,
		htmlDialog:$_htmlDialog,
		get isHidden() {
			return $(".d-msg.hidden").size()>0;
		}
	};

	function showNotification(message){
	  Notification.requestPermission(function(permission){
	    if ("granted"===permission){
	      var notification = new Notification(args.title||"",{
	        body:message,
	        tag:"#MSG001",
	        icon:args.icon || ""
	      })
	    }
	  });
	}

	function $_showDialog() {
	    if ("Notification" in window){
	      showNotification($(".d-msg-c").text().trim());
	    }
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
			if ($('.rf-msgs-err').html() == null && timeout !== -1) {
				timer = setTimeout($_hideDialog, timeout);
			}
		}
	}

	function $_dialog(msg, html) {
		clearTimeout(timer);
		if (html) {
			$(".d-msg-c").html(msg);
		} else {
			$(".d-msg-c").text(msg);
		}
		$_showDialog();
		if (timeout !== -1) {
			timer = setTimeout($_hideDialog, timeout);
		}
		$(".d-msg").attr('infox-dialog', false);
	}
	
	function $_textDialog(msg) {
		$_dialog(msg, false);
	}
	
	function $_htmlDialog(msg) {
		$_dialog(msg, true);
	}
	
	return Messages;
});