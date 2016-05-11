String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

namespace("infox",{
	
	messageModalStatus: "",
	
	applyCSS:function applyCSS(args) {
		setTimeout(function(){
			$(args.selector).css(args.style);
		},args.delay || 1);
	},openPopUp:function openPopUp(id, url, width, height, top, left) {
		var featPopUp = ["width=",width || outerWidth,
			             ",height=",height || outerHeight,
			             ",top=",top || screen.top || screenY || screenTop || 0,
			             ",left=",left || screen.left || screenX || screenLeft || 0,
			             ",resizable=YES",
			             ",scrollbars=YES",
			             ",status=NO",
			             ",location=NO"];
		var popUp = window.open(url || 'about:blank', id, featPopUp.join(""));	
	},abrirPopUp:function abrirPopUp(id, url,args) {
		var options = (args || {}).options || {};
		var props = [];
		for(var it in options) {
			props.push(it+"="+options[it]);
		}
		window.open(url || "about:blank", id || "Janela", props.join(","))
			.moveTo(0, 0);
	},showPopup:function maximizePopup(id) {
	    var popup = RichFaces.$(id);
	    popup.show();
	    this.maximizePopup(popup);
	},maximizePopup:function maximizePopup(obj) {
	    obj.setSize(obj.__calculateWindowWidth()*0.95, obj.__calculateWindowHeight()*0.95);
	    obj.setTop((obj.__calculateWindowHeight()-obj.height())/2);
	    obj.setLeft((obj.__calculateWindowWidth()-obj.width())/2);
	},showLoading:function showLoading(message) {
		if(message)
			this.messageModalStatus = message;
		else
			this.messageModalStatus = "";
		
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
}, {
	callback:function (Infox) {
		window.showLoading = Infox.showLoading;
		window.hideLoading = Infox.hideLoading;
		window.refreshOpener = Infox.refreshOpener;
		window.openPopUp = Infox.openPopUp;
		window.infox = Infox;
		
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
			};	
		}
	}
});