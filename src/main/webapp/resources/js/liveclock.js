(function() {
	var $digital = new Date();
	function $updateClock() {
		var $ = window.jQuery || function() {this.html=function(){}};
		$digital.setTime(Date.now());
		$(".liveclock").html($digital.toLocaleTimeString())
	}
	window.clockTimer = window.clockTimer || setInterval($updateClock, 1000);
})()