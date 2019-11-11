window.Infox = window.Infox || {};

if ( !Infox.ajax ) {
	
	Infox.ajax = {
			
			PRIMEFACES_AJAX :  PrimeFaces.ajax.Request.send,
			PRIMEFACES_POLL : PrimeFaces.ajax.Queue.poll,
			JSF_AJAX : jsf.ajax.request,
			
			Queue : {

				requests : [],

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
					this.requests = [];
				}
			},

			Request : {

				send : function(cfg) {
					if (cfg.ajaxOwner === 'PrimeFaces') {
						delete cfg['ajaxOwner'];
						Infox.ajax.PRIMEFACES_AJAX(cfg);
					} else {
						delete cfg['ajaxOwner'];
						var sourceElement = cfg.sourceElement;
						var sourceEvent = cfg.sourceEvent;
						delete cfg['sourceElement'];
						delete cfg['sourceEvent'];
						Infox.ajax.JSF_AJAX(sourceElement, sourceEvent, cfg);
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
				Infox.ajax.Queue.push(cfg);
			},
			
			// Função chamada pela função PrimeFaces.ab (requisição ajax dos componentes)
			// Necessário sobrescrever para chamar diretamente a função send, ao invés de tentar colocar na queue do Prime (Queue.offer)
			handle: function(a, b) {
				a.ext = b;
                if (PrimeFaces.settings.earlyPostParamEvaluation) {
                    a.earlyPostParams = PrimeFaces.ajax.Request.collectEarlyPostParams(a);
                }
                PrimeFaces.ajax.Request.send(a);
			}
			
		}
);

Object.assign(
		PrimeFaces.ajax.Queue,
		{
			// Sobrescreve o poll ao invés de adicionar oncomplete para também funcionar com
			// cancelamento da requisição ao retornar false no onstart
			poll: function() {
				Infox.ajax.PRIMEFACES_POLL.call(PrimeFaces.ajax.Queue);
				Infox.ajax.Queue.poll();
			}
		}
);

Object.assign(
		jsf.ajax,
		{
			// É necessário apenas sobrescrever esta função para o JSF e o RichFaces
			// pois o RichFaces se integra com o JSF
			request: function(source, event, options) {
				options.sourceElement = source;
				options.sourceEvent = event;
				Infox.ajax.Queue.push(options);
			}
		}
);

// Para JSF e RichFaces
jsf.ajax.addOnEvent(function(data) {
	if (data.type === 'event' && data.status === 'success') {
		Infox.ajax.Queue.poll();
	}
})
