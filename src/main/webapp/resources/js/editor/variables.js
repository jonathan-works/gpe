var Variables = {
	init : function( editor ) {
		console.log("init",editor);
		editor.addCommand("teste",{
			exec:function(editor) {
				console.debug("executou teste");
			}
		});
		editor.ui.addButton("variables.btn",{
			label:"Botão de teste",
			command:"teste"
		});
	}
};