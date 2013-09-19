namespace("infox",{
	applyCSS:function applyCSS(args) {
		setTimeout(function(){
			$(args.selector).css(args.style);
		},args.delay || 1);
	},
	openPopUp:function openPopUp(id, url, width, height) {
		var featPopUp = ["width=",width,
			             ",height=",height,
			             ",resizable=YES",
			             ",scrollbars=YES",
			             ",status=NO",
			             ",location=NO"];
		var popUp = window.open(url, id, featPopUp.join(""));	
		popUp.moveTo(0, 0);
	},showLoading:function showLoading() {
		RichFaces.$('modalStatus').show();	
	},hideLoading:function hideLoading() {
		RichFaces.$('modalStatus').hide();
	},refreshOpener:function refreshOpener() {
		try {
			opener.refreshPage();
		} catch (e) {
			console.error(e);
		}
	},redirect:function redirect(o) {
		var destination = o.url;
		var iter = 0;
		if (o.params) {
			destination += "?";
			for(var name in o.params) {
				if (iter++ > 0) {
					destination += "&";
				}
				destination += name + "=" + o.params[name];
			}
		}
		window.location = destination;
	},escapeId:function escapeId(id) {
		if (typeof id !== "string") {
			throw "Invalid argument type";
		}
		return id.split(":").join("\\:");
	},imageSrcToBase64:function imageSrcToBase64(src) {
		var img = document.createElement("img");
		img.src = src;
		var canvas = document.createElement("canvas");
		canvas.width = img.width;
		canvas.height = img.height;
		var ctx = canvas.getContext("2d");
		ctx.drawImage(img,0,0);
		return canvas.toDataURL();
	}
});

namespace("infox",function(util) {
	window.showLoading = util.showLoading;
	window.hideLoading = util.hideLoading;
	window.refreshOpener = util.refreshOpener;
	window.openPopUp = util.openPopUp;
	window.infox = util;
	
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