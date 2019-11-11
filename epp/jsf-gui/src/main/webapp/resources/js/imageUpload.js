namespace("infox.imageUpload",{
	click:function(evt, editorId, useBase64) {
		if (editorId) {
			if (useBase64 === 'true') {
				infox.editor[editorId].insertImageBase64(evt.src);
			} else {
				infox.editor[editorId].insertImage(evt.src);
			}
		}
	}
});