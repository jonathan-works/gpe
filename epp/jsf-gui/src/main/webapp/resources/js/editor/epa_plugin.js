

(function() {
	tinymce.create('tinymce.plugins.EPA', {
		init : function(ed, url) {
			var t = this;
			t.editor = ed;

			ed.addCommand('mceVariavel', function(ui, str) {
				ed.execCommand('mceInsertContent', false, str);
				setTimeout("tinyMCE.activeEditor.controlManager.get('epaInsert').selectByIndex(-1)",1);
			});

		},

		createControl : function(n, cm) {
			switch (n) {
				case "epaInsert":
					c = this.editor.controlManager.createListBox('epaInsert', {title : label, cmd : 'mceVariavel'});
					for (v in contents) {
						c.add(v, contents[v]);
					}
					return c;
			}
			return null;
		}

	});

	// Register plugin
	tinymce.PluginManager.add('epa', tinymce.plugins.EPA);
})();