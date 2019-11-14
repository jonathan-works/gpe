CKEDITOR.dialog.add('showProtectedDialog', function(editor) {

    return {
        title : CKEDITOR.plugins.showprotected.l10n(editor, 'showProtectedDialog.title'),
        minWidth : 300,
        minHeight : 60,
        onOk : function() {
            var newSourceValue = this.getContentElement('info', 'txtProtectedSource').getValue();
            newSourceValue=CKEDITOR.plugins.showprotected.encapsulateToken(newSourceValue);
            var encodedSourceValue = CKEDITOR.plugins.showprotected.encodeProtectedSource(newSourceValue);
            this._.selectedElement.setAttribute('data-cke-realelement', encodedSourceValue);
            this._.selectedElement.setText(newSourceValue.slice(2, newSourceValue.length - 1));
            this._.selectedElement.setAttribute('title', newSourceValue);
        },

        onHide : function() {
            delete this._.selectedElement;
        },

        onShow : function() {
            this._.selectedElement = editor.getSelection().getSelectedElement();
            var decodedSourceValue = CKEDITOR.plugins.showprotected.decodeProtectedSource(this._.selectedElement
                    .getAttribute('data-cke-realelement'));
            decodedSourceValue=CKEDITOR.plugins.showprotected.unencapsulateToken(decodedSourceValue);
            this.setValueOf('info', 'txtProtectedSource', decodedSourceValue);
        },
        contents : [ {
            id : 'info',
            label : CKEDITOR.plugins.showprotected.l10n(editor, 'showProtectedDialog.contents.label'),
            accessKey : 'I',
            elements : [ {
                type : 'text',
                id : 'txtProtectedSource',
                label : CKEDITOR.plugins.showprotected.l10n(editor,
                        'showProtectedDialog.contents.elements.txtProtectedSource.label'),
                required : true,
                validate : function() {
                    if (!this.getValue()) {
                        alert(CKEDITOR.plugins.showprotected.l10n(editor,
                                'showProtectedDialog.contents.elements.txtProtectedSource.emptyAlert'));
                        return false;
                    }
                    return true;
                }
            } ]
        } ]
    };
});