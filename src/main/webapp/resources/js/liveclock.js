(function() {
	var $digital = new Date();
	function $updateClock() {
		if (window.jQuery) {
			$digital.setTime(Date.now());
			jQuery(".liveclock").html($digital.toLocaleTimeString());
		}
	}
	namespace("infox.util.clockTimer",setInterval($updateClock, 1000));
})()