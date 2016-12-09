
CKEDITOR.dialog.add( 'showProtectedDialog', function( editor ) {

	return {
		title: 'Edit Protected Source',
		minWidth: 300,
		minHeight: 60,
		onOk: function() {
			var newSourceValue = this.getContentElement( 'info', 'txtProtectedSource' ).getValue();
			
			var encodedSourceValue = CKEDITOR.plugins.showprotected.encodeProtectedSource( newSourceValue );
			this._.selectedElement.setAttribute('data-cke-realelement', encodedSourceValue);
			this._.selectedElement.setText(newSourceValue.slice(2, newSourceValue.length-1));
			this._.selectedElement.setAttribute('title', newSourceValue);
		},

		onHide: function() {
			delete this._.selectedElement;
		},

		onShow: function() {
			this._.selectedElement = editor.getSelection().getSelectedElement();
			var decodedSourceValue = CKEDITOR.plugins.showprotected.decodeProtectedSource( this._.selectedElement.getAttribute('data-cke-realelement') );

			this.setValueOf( 'info', 'txtProtectedSource', decodedSourceValue );
		},
		contents: [
			{
			id: 'info',
			label: 'Edit Protected Source',
			accessKey: 'I',
			elements: [
				{
				type: 'text',
				id: 'txtProtectedSource',
				label: 'Value',
				required: true,
				validate: function() {
					if ( !this.getValue() ) {
						alert( 'The value cannot be empty' );
						return false;
					}
					return true;
				}
			}
			]
		}
		]
	};
} );