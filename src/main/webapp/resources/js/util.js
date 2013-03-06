function showLoading() {	
	if ($('#status')) {
		$('#status').hide();
	}
	RichFaces.$('modalStatus').show();
}

function hideLoading() {
	RichFaces.$('modalStatus').hide();
	if ($('#status')) {
		$('#status').show();
	}
}

function refreshOpener() {
	try {
	   opener.refreshPage();
	} catch (e) {}
}

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

// Padronização das telas de popUps
function openPopUp(id, url, width, height) {
	if (!width) width = 800;
	if (!height) height = 600;
	var featurePopUp = "width="+width + ", height="+height+", resizable=YES, scrollbars=YES, status=NO, location=NO";
	var popUp = window.open(url, id, featurePopUp);	
	popUp.moveTo(0, 0);
}

// Corrige as imagens *.png no IE
function correctPNG(){
   var browserType = navigator.appName;  
   if (browserType == "Microsoft Internet Explorer"){  
	   for(var i = 0; i < document.images.length; i++) {
	      var img = document.images[i];
	      var imgName = img.src.toUpperCase();
	      if (imgName.substring(imgName.length-3, imgName.length) == "PNG") {
	         var imgID = (img.id) ? "id='" + img.id + "' " : "";
	         var imgClass = (img.className) ? "class='" + img.className + "' " : "";
	         var imgTitle = (img.title) ? "title='" + img.title + "' " : "title='" + img.alt + "' ";
	         var imgStyle = "display:inline-block;" + img.style.cssText;
	         if (img.align == "left") {
	        	 imgStyle = "float:left;" + imgStyle;
	         }
	         if (img.align == "right") {
	        	 imgStyle = "float:right;" + imgStyle;
	         }
	         if (img.parentElement.href) {
	        	 imgStyle = "cursor:hand;" + imgStyle;
	         }	         
	         var strNewHTML = "<span " + imgID + imgClass + imgTitle
	         	+ " style=\"" + "width:" + img.width + "px; height:" + img.height + "px;" + imgStyle + ";"
	         	+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
	         	+ "(src=\'" + img.src + "\', sizingMethod='scale');\"></span>";
	         img.outerHTML = strNewHTML;
	         i = i - 1;
	      }
	   }
   }
}
try{
	window.attachEvent("onload", correctPNG);
} catch(e) {}