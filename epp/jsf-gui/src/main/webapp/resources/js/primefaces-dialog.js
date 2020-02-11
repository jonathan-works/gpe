//ENCAPSULANDO PARA NÃO VAZAR VARIÁVEIS NO CONTEXTO GLOBAL
(function(){
var InfoxPrimefacesDialogHandlerMixin={
		openDialog: function(cfg) {
            var rootWindow = this.findRootWindow(),
            dialogId = cfg.sourceComponentId + '_dlg';

            if(rootWindow.document.getElementById(dialogId)) {
                return;
            }

            var dialogWidgetVar = cfg.sourceComponentId.replace(/:/g, '_') + '_dlgwidget',
            dialogDOM = $('<div id="' + dialogId + '" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container ui-overlay-hidden"' +
                    ' data-pfdlgcid="' + cfg.pfdlgcid + '" data-widgetvar="' + dialogWidgetVar + '"></div>')
                    .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span class="ui-dialog-title"></span></div>');

            var titlebar = dialogDOM.children('.ui-dialog-titlebar');
            if(cfg.options.closable !== false) {
                titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a>');
            }

            if(cfg.options.minimizable) {
                titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-minimize ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-minus"></span></a>');
            }

            if(cfg.options.maximizable) {
                titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-maximize ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-extlink"></span></a>');
            }

            dialogDOM.append('<div class="ui-dialog-content ui-widget-content ui-df-content">' +
                    '<iframe style="border:0 none" frameborder="0"/>' +
                    '</div>');

            dialogDOM.appendTo(rootWindow.document.body);
						this.cfg=cfg;
						this.jq=dialogDOM;
						this.titlebar=titlebar;
						this.content=dialogDOM.children('.ui-dialog-content');
						this.footer=dialogDOM.children('.ui-dialog-footer');

            var dialogFrame = dialogDOM.find('iframe'),
            symbol = cfg.url.indexOf('?') === -1 ? '?' : '&',
            frameURL = cfg.url.indexOf('pfdlgcid') === -1 ? cfg.url + symbol + 'pfdlgcid=' + cfg.pfdlgcid: cfg.url,
            frameWidth = cfg.options.contentWidth||640;

            dialogFrame.width(frameWidth);

						var _that=this;
            dialogFrame.on('load', function() {
                var $frame = $(this),
                headerElement = $frame.contents().find('title'),
                isCustomHeader = false;

                if (cfg.options.headerText) {
                	headerElement = $('<title>' + cfg.options.headerText + '</title>');
                }
                else {
                	if(cfg.options.headerElement) {
                        var customHeaderId = PrimeFaces.escapeClientId(cfg.options.headerElement),
                        customHeaderElement = dialogFrame.contents().find(customHeaderId);

                        if(customHeaderElement.length) {
                            headerElement = customHeaderElement;
                            isCustomHeader = true;
                        }
                    }
                }

                if(!$frame.data('initialized')) {
                    PrimeFaces.cw.call(rootWindow.PrimeFaces, 'DynamicDialog', dialogWidgetVar, {
                        id: dialogId,
                        position: cfg.options.position || 'center',
                        sourceComponentId: cfg.sourceComponentId,
                        sourceWidgetVar: cfg.sourceWidgetVar,
                        onHide: function() {
                            var $dialogWidget = this,
                            dialogFrame = this.content.children('iframe'),
                            sourceWidgetVar = $dialogWidget.cfg.sourceWidgetVar;

                            if(dialogFrame.get(0).contentWindow.PrimeFaces) {
                                this.destroyIntervalId = setInterval(function() {
                                    if(dialogFrame.get(0).contentWindow.PrimeFaces.ajax.Queue.isEmpty()) {
                                        clearInterval($dialogWidget.destroyIntervalId);
                                        dialogFrame.attr('src','about:blank');
                                        $dialogWidget.jq.remove();
                                    }
                                }, 10);
                            }
                            else {
                                dialogFrame.attr('src','about:blank');
                                $dialogWidget.jq.remove();
                            }

                            //Call dialog return
                            PrimeFaces.dialog.DialogHandler._closeDialog(cfg);

                            rootWindow.PF[dialogWidgetVar] = undefined;
                        },
                        modal: cfg.options.modal,
                        resizable: cfg.options.resizable,
                        hasIframe: true,
                        draggable: cfg.options.draggable,
                        width: cfg.options.width,
                        height: cfg.options.height,
                        minimizable: cfg.options.minimizable,
                        maximizable: cfg.options.maximizable,
                        headerElement: cfg.options.headerElement,
                        onShow: function () {
                        	if (cfg.options.onShow) {
                        		window.eval(cfg.options.onShow);
                        	}
													_that.calcCorrectSize();
                        }
                    });
                }

                var title = rootWindow.PF(dialogWidgetVar).titlebar.children('span.ui-dialog-title');
                if(headerElement.length > 0) {
                    if(isCustomHeader) {
                        title.append(headerElement);
                        headerElement.show();
                    }
                    else {
                        title.text(headerElement.text());
                    }

                    dialogFrame.attr('title', title.text());
                }

                dialogFrame.data('initialized', true);

                rootWindow.PF(dialogWidgetVar).show();

                //adjust height
                var frameHeight = null;
                if(cfg.options.contentHeight)
                    frameHeight = cfg.options.contentHeight;
                else
                    frameHeight = $frame.get(0).contentWindow.document.body.scrollHeight + (PrimeFaces.env.browser.webkit ? 5 : 25);

                $frame.css('height', frameHeight);
            })
            .attr('src', frameURL);
        },

        _closeDialog: function(cfg) {
        	var rootWindow = this.findRootWindow(),
            dlgs = $(rootWindow.document.body).children('div.ui-dialog[data-pfdlgcid="' + cfg.pfdlgcid +'"]').not('[data-queuedforremoval]'),
            dlgsLength = dlgs.length,
            dlg = dlgs.eq(dlgsLength - 1),
            parentDlg = dlgsLength > 1 ? dlgs.eq(dlgsLength - 2) : null,
            dlgWidget = rootWindow.PF(dlg.data('widgetvar')),
            sourceWidgetVar = dlgWidget.cfg.sourceWidgetVar,
            sourceComponentId = dlgWidget.cfg.sourceComponentId,
            dialogReturnBehavior = null,
            windowContext = null;

            dlg.attr('data-queuedforremoval', true);

            if(parentDlg) {
                var parentDlgFrame = parentDlg.find('> .ui-dialog-content > iframe').get(0),
                windowContext = parentDlgFrame.contentWindow||parentDlgFrame;
                sourceWidget = windowContext.PF(sourceWidgetVar);
            }
            else {
                windowContext = rootWindow;
            }

            if(sourceWidgetVar) {
                var sourceWidget = windowContext.PF(sourceWidgetVar);
                dialogReturnBehavior = sourceWidget.cfg.behaviors ? sourceWidget.cfg.behaviors['dialogReturn']: null;
            }
            else if(sourceComponentId) {
                var dialogReturnBehaviorStr = $(windowContext.document.getElementById(sourceComponentId)).data('dialogreturn');
                if(dialogReturnBehaviorStr) {
                    dialogReturnBehavior = windowContext.eval('(function(ext){this.' + dialogReturnBehaviorStr + '})');
                }
            }

            if(dialogReturnBehavior) {
                var ext = {
                        params: [
                            {name: sourceComponentId + '_pfdlgcid', value: cfg.pfdlgcid}
                        ]
                    };

                dialogReturnBehavior.call(windowContext, ext);
            }

            //dlgWidget.hide(); --> Removed for not loop
        }
	};

var InfoxPrimefacesDynamicDialogMixin={

	    initPosition: function() {
	        var c = this;
	        this.jq.css({
	            left: 0,
	            top: 0
	        });
	        if (/(center|left|top|right|bottom)/.test(this.cfg.position)) {
	            this.cfg.position = this.cfg.position.replace(",", " ");
	            this.jq.position({
	                my: "center",
	                at: this.cfg.position,
	                collision: "fit",
	                of: window,
	                using: function(h) {
	                    var e = h.left < 0 ? 0 : h.left
	                      , f = h.top < 0 ? 0 : h.top
	                      , g = $(window).scrollTop();
	                    if (c.cfg.absolutePositioned) {
	                        f += g;
	                        c.lastScrollTop = g
	                    }
	                    $(this).css({
	                        left: e,
	                        top: f
	                    })
	                }
	            })
	        } else {
	            var b = this.cfg.position.split(",")
	              , a = $.trim(b[0])
	              , d = $.trim(b[1]);
	            this.jq.css({
	                left: a,
	                top: d
	            })
	        }
	        this.positionInitialized = true
	    }
	};

var InfoxPrimefacesDialogMixin={
	initSize: function() {
		this.jq.css({
    		width: this.cfg.width,
        height: this.cfg.height
    });
		if (this.cfg.height === 'auto') {
				this.content.height('')
		}
		if (this.cfg.width === 'auto') {
				this.content.width('')
		}
        if (this.cfg.fitViewport) {
            this.fitViewport();
            this.calcCorrectSize();
        }
				if (!this.cfg._$isOnShowOverrided) {
					var prevShow=this.cfg.onShow||function(){}
					this.cfg.onShow=function overrideOnShow(){
							this.updateSizePosition();
							prevShow.apply(this, arguments);
					}
					this.cfg._$isOnShowOverrided=true;
				}

        if (this.cfg.width === "auto" && PrimeFaces.isIE(7)) {
            this.jq.width(this.content.outerWidth());
        }
    },

	calcCorrectSize:function(){
		var windowHeight=jQuery(window).height();
		var innerHeight=this.jq.innerHeight()
		if (this.jq.height() > windowHeight){
			this.jq.height(windowHeight * 0.8)
		}
		this.content.innerHeight(innerHeight-this.titlebar.innerHeight()-this.footer.innerHeight());
	},
    updateSizePosition:function(){
				this.initSize();
				this.calcCorrectSize();
        this.initPosition();
    }
};

PrimeFaces.widget.Dialog = PrimeFaces.widget.Dialog.extend(Object.assign({},InfoxPrimefacesDialogMixin));

PrimeFaces.widget.DynamicDialog = PrimeFaces.widget.DynamicDialog.extend(Object.assign({},InfoxPrimefacesDynamicDialogMixin,InfoxPrimefacesDialogMixin));

Object.assign(PrimeFaces.dialog.DialogHandler,InfoxPrimefacesDialogHandlerMixin,InfoxPrimefacesDialogMixin);

})()
