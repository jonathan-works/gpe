(function() {
	var $digital = new Date();
	function $updateClock() {
		var $;
		try {
			$ = window.jQuery;
			$digital.setTime(Date.now());
			$(".liveclock").html($digital.toLocaleTimeString())
		} catch(e) {
			console.error(e);
		}
	}
	window.clockTimer = window.clockTimer || setInterval($updateClock, 1000);
})()