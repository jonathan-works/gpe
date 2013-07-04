namespace("infox.util",{
	applyCSS:function(args) {
		setTimeout(function(){
			$(args.selector).css(args.style);
		},args.delay || 1);
	},
	openPopUp:function (id, url, width, height) {
		var featPopUp = ["width="		,width,
			             "height="		,height,
			             "resizable=YES",
			             "scrollbars=YES",
			             "status=NO",
			             "location=NO"];
		var popUp = window.open(url, id, featPopUp.join(","));	
		popUp.moveTo(0, 0);
	},showLoading:function() {
		RichFaces.$('modalStatus').show();	
	},hideLoading:function() {
		RichFaces.$('modalStatus').hide();
	},refreshOpener:function() {
		try {
			opener.refreshPage();
		} catch (e) {
			console.error(e);
		}
	}
});
namespace("infox.util",function(util) {
	window.showLoading = util.showLoading;
	window.hideLoading = util.hideLoading;
	window.refreshOpener = util.refreshOpener;
	window.openPopUp = util.openPopUp;
	
	if (!$.fn.clearForm) {
		$.fn.clearForm = function() {
			return this.each(function() {
				var type = this.type; 
				var tag = this.tagName.toLowerCase();
				if (tag == 'form')
					return $(':input',this).clearForm();
				if (type == 'text' || type == 'password' || tag == 'textarea')
					this.value = '';
				else if (type == 'checkbox' || type == 'radio')
					this.checked = false;
				else if (tag == 'select')
					this.selectedIndex = 0;
			});
		}	
	}
});