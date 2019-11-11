function setZIndex(id) {
	document.getElementById(id).style.zIndex = document.getElementById(id).offsetHeight > 30 ? 1 : 0;
}