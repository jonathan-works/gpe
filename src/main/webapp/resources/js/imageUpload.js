namespace("infox.imageUpload",{
	click:function(evt, editorId) {
		if (editorId) {
			infox.editor[editorId].insertImage(evt.src);
		}
	}
});