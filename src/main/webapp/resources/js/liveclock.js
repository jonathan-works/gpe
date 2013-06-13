(function() {
	var $digital = new Date();
	function $updateClock() {
		if (window.jQuery) {
			$digital.setTime(Date.now());
			jQuery(".liveclock").html($digital.toLocaleTimeString());
		}
	}
	window.clockTimer = window.clockTimer || setInterval($updateClock, 1000);
})()