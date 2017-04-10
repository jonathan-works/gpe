(function() {
	// Registra apenas Gecko e Webkit pois até o momento os prefixos para Edge e Blink são iguais aos do Webkit
	// Mapeia os nomes dos métodos e properties W3C para suas versões prefixadas
	// A property fullscreen não está mapeada pois é obsoleta
	var apis = {
		gecko: {
			fullscreenEnabled: "mozFullScreenEnabled",
			fullscreenElement: "mozFullScreenElement",
			onfullscreenchange: "onmozfullscreenchange",
			onfullscreenerror: "onmozfullscreenerror",
			exitFullscreen: "mozCancelFullScreen",
			requestFullscreen: "mozRequestFullScreen"
		},
		webkit: {
			fullscreenEnabled: "webkitFullscreenEnabled",
			fullscreenElement: "webkitFullscreenElement",
			onfullscreenchange: "onwebkitfullscreenchange",
			onfullscreenerror: "onwebkitfullscreenerror",
			exitFullscreen: "webkitExitFullscreen",
			requestFullscreen: "webkitRequestFullscreen"
		}
	};
	
	if (typeof(document.fullscreen) !== 'undefined') {
		return;
	}
	
	var api;
	if (typeof(document[apis['gecko']['fullscreenEnabled']]) !== 'undefined') {
		api = apis['gecko'];
	} else if (typeof(document[apis['webkit']['fullscreenEnabled']]) !== 'undefined') {
		api = apis['webkit'];
	} else {
		console.error('API Fullscreen não suportada');
		return;
	}
	
	Element.prototype.requestFullscreen = function() {
		return this[api['requestFullscreen']]();
	};
	
	Document.prototype.exitFullscreen = function() {
		return this[api['exitFullscreen']]();
	};
	
	Object.defineProperty(document, 'fullscreenEnabled', {
		get: function() {
			return this[api['fullscreenEnabled']];
		}
	});
	
	Object.defineProperty(document, 'fullscreenElement', {
		get: function() {
			return this[api['fullscreenElement']];
		}
	});
	
	Object.defineProperty(document, 'onfullscreenchange', {
		get: function() {
			return this[api['onfullscreenchange']];
		},
		set: function(eventFunction) {
			this[api['onfullscreenchange']] = eventFunction;
		}
	});
	
	Object.defineProperty(document, 'onfullscreenerror', {
		get: function() {
			return this[api['onfullscreenerror']];
		},
		set: function(eventFunction) {
			this[api['onfullscreenerror']] = eventFunction;
		}
	});
})();