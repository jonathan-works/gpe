function setZIndex(id) {
	$(id).style.zIndex = $(id).offsetHeight > 30 ? 1 : 0;
}