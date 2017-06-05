window.Infox = window.Infox || {};

if ( !Infox.ajax ) {
	
	Infox.ajax = {
			
			RICHFACES_AJAX : RichFaces.ajax,
			PRIMEFACES_AJAX :  PrimeFaces.ajax.Request.send,
			
			Queue : {

				requests : new Array(),

				push : function(request) {
					
					if (request.async) {
						Infox.ajax.Request.send(request);
					} else {
						this.requests.push(request);

						if (this.requests.length === 1) {
							Infox.ajax.Request.send(request);
						}
					}
				},

				poll : function() {
					if (this.isEmpty()) {
						return null;
					}

					var processed = this.requests.shift(), next = this.peek();

					if (next) {
						console.log('Next ajax called');
						Infox.ajax.Request.send(next);
					}

					return processed;
				},

				peek : function() {
					if (this.isEmpty()) {
						return null;
					}

					return this.requests[0];
				},

				isEmpty : function() {
					return this.requests.length === 0;
				},

				abortAll : function() {

					this.requests = new Array();
				}
			},

			Request : {

				send : function(cfg) {
					
					console.log('Initiating ajax request.');
					
					if (cfg.ajaxOwner === 'PrimeFaces') {
						delete cfg['ajaxOwner'];
						if (cfg.oncomplete) {
							var oncomplete = cfg.oncomplete;
							cfg.oncomplete = function(xhr,status,args) {
								oncomplete(xhr,status,args);
								Infox.ajax.Queue.poll();
							}
						} else {
							cfg.oncomplete = function(xhr,status,args) {
								Infox.ajax.Queue.poll();
							}
						}
						Infox.ajax.PRIMEFACES_AJAX(cfg);
					} else {
						delete cfg['ajaxOwner'];
						var sourceElement = cfg.sourceElement;
						var sourceEvent = cfg.sourceEvent;
						delete cfg['sourceElement'];
						delete cfg['sourceEvent'];
						Infox.ajax.RICHFACES_AJAX(sourceElement, sourceEvent, cfg);
					}
					
				}
			
			}
		};

		$(window).unload(function() {
		    Infox.ajax.Queue.abortAll();
		});
}

Object.assign(
		PrimeFaces.ajax.Request,
		{
			send: function(cfg) {
				cfg.ajaxOwner = 'PrimeFaces';
				console.log("add to Infox Queue");
				Infox.ajax.Queue.push(cfg);
			}
			
		}
);

Object.assign(
		RichFaces,
		{
			ajax: function(source, event, options) {
				options.ajaxOwner = 'RichFaces';
				options.sourceElement = source;
				options.sourceEvent = event;
				console.log("add to Infox Queue");
				Infox.ajax.Queue.push(options);
			}
		}
);
