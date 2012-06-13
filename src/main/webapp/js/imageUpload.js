var img;
function uploadHandler(e, path) {
	src = path + "/" + e.memo.entry.fileName;
	img="<img src=\"" + src + "\" />";
}

function insert() {
	ed = tinymce.EditorManager.activeEditor;
	setTimeout("ed.execCommand('mceInsertContent', false,'" +  img + "')",500);
}

function configureDragDrop() {
	tinymce.dom.Event.add(tinymce.EditorManager.activeEditor.getDoc(), 'dragdrop', function(e) {
		ed = tinymce.EditorManager.activeEditor;
		texto = ed.getContent().replace(/img style="max-width: 50px;"/gi, 'img');
		texto = texto.replace(/title="Arraste para o documento"/gi, '');
		ed.setContent(texto)
	});
	tinyMCE.activeEditor.onClick.add(function(ed, e) { 
		texto = ed.getContent().replace('max-width: 50px;', '');
		ed.setContent(texto)
	});
}

