/*
 *  "showprotected" CKEditor plugin
 *  
 *  Created by Matthew Lieder (https://github.com/IGx89)
 *  
 *  Licensed under the MIT, GPL, LGPL and MPL licenses
 *  
 *  Icon courtesy of famfamfam: http://www.famfamfam.com/lab/icons/mini/
 */

// TODO: configuration settings
// TODO: show the actual text inline, not just an icon?
// TODO: improve copy/paste behavior (tooltip is wrong after paste)

CKEDITOR.plugins.add( 'showprotected', {
	requires: 'dialog,fakeobjects',
	onLoad: function() {
                // Styles with contents direction awareness.
                function cssWithDir( template , dir ) {
                        return template.replace( /%1/g, dir == 'rtl' ? 'right' : 'left' ).replace( /%2/g, 'cke_contents_' + dir );
                }
                
		var template = '.%2 span.cke_protected' +
	        '{' +
	          'cursor:pointer;' +
	          '-webkit-user-select: none;' +
	          '-moz-user-select: none;' +
	          '-ms-user-select: none;' +
	          'user-select: none;' +
	          'border: 1px dotted #00f;'
	        '}';
		
		CKEDITOR.addCss( cssWithDir( template, 'ltr' ) + cssWithDir( template, 'rtl' ) );
	},

	init: function( editor ) {
		CKEDITOR.dialog.add( 'showProtectedDialog', this.path + 'dialogs/protected.js' );
		
		editor.on( 'doubleclick', function( evt ) {
			var element = evt.data.element;

			if ( element.is( 'span' ) && element.hasClass( 'cke_protected' ) ) {
			    editor.getSelection().fake(element);
				evt.data.dialog = 'showProtectedDialog';
			}
		} );
	},

	afterInit: function( editor ) {
		// Register a filter to displaying placeholders after mode change.

		var dataProcessor = editor.dataProcessor,
			dataFilter = dataProcessor && dataProcessor.dataFilter;

		if ( dataFilter ) {
			dataFilter.addRules( {
				comment: function( commentText, commentElement ) {
					if(commentText.indexOf(CKEDITOR.plugins.showprotected.protectedSourceMarker) == 0) {
						commentElement.attributes = [];
						var fakeElement = editor.createFakeParserElement( commentElement, 'cke_protected', 'protected' );
						fakeElement = CKEDITOR.plugins.showprotected.createFakeParserElement.apply(editor, [ commentElement, 'cke_protected', 'protected' ]);
						var cleanedCommentText = CKEDITOR.plugins.showprotected.decodeProtectedSource( commentText );
						fakeElement.attributes.title = cleanedCommentText;
						
						fakeElement.add(new CKEDITOR.htmlParser.text( cleanedCommentText.slice(2, cleanedCommentText.length-1) ));
						return fakeElement;
					}
					
					return null;
				}
			} );
		}
	}
} );

/**
 * Set of showprotected plugin's helpers.
 *
 * @class
 * @singleton
 */
CKEDITOR.plugins.showprotected = {
		
	protectedSourceMarker: '{cke_protected}',
		
	decodeProtectedSource: function( protectedSource ) {
		if(protectedSource.indexOf('%3C!--') == 0) {
			return decodeURIComponent(protectedSource).replace( /<!--\{cke_protected\}([\s\S]+?)-->/g, function( match, data ) {
                            return decodeURIComponent( data );
			});
		} else {
			return decodeURIComponent(protectedSource.substr(CKEDITOR.plugins.showprotected.protectedSourceMarker.length));
		}
	},
	
	encodeProtectedSource: function( protectedSource ) {
		return encodeURIComponent('<!--' + CKEDITOR.plugins.showprotected.protectedSourceMarker) +
        	encodeURIComponent( protectedSource ).replace( /--/g, '%2D%2D' ) +
        	encodeURIComponent('-->');
	},

	createFakeParserElement : function(realElement, className, realElementType) {
            var lang = this.lang.fakeobjects, label = lang[realElementType] || lang.unknown, html;
    
            var writer = new CKEDITOR.htmlParser.basicWriter();
            realElement.writeHtml(writer);
            html = writer.getHtml();
    
            var attributes = {
                'class' : className,
                'data-cke-realelement' : encodeURIComponent(html),
                'data-cke-real-node-type' : realElement.type,
                align : realElement.attributes.align || ''
            };
    
            if (!CKEDITOR.env.hc)
                attributes.src = CKEDITOR.tools.transparentImageData;
    
            if (realElementType)
                attributes['data-cke-real-element-type'] = realElementType;
    
            return new CKEDITOR.htmlParser.element('span', attributes);
        }
	
};