PrimeFaces.widget.AutoComplete = PrimeFaces.widget.AutoComplete.extend({
	setupForceSelection : function() {
		this.currentItems = [ this.input.val() ];
		var a = this;
		this.input.blur(function() {
			var d = $(this).val(), c = false;
			if (PrimeFaces.isIE(8)) {
				a.itemClick = true
			}
			for (var b = 0; b < a.currentItems.length; b++) {
				if (a.currentItems[b] === d) {
					c = true;
					break;
				}
			}
			console.log(a.items);
			console.log(a.currentItems);
			if (!c) {
				if (a.cfg.multiple) {
					a.input.val("")
				} else {
					a.input.val("");
					a.hinput.val("")
				}
			} else if (!a.cfg.multiple) {
				for (var i = 0; i < a.items.length; i++) {
					if (a.hinput.val() === a.items[i].getAttribute('data-item-value')) {
						return;
					}
				}
				a.input.val("");
				a.hinput.val("");
			}
		});
	}
});