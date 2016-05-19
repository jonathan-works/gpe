package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.epp.processo.documento.entity.Documento;

public class UploadValueImpl extends FileTypedValue {

    public UploadValueImpl(Documento documento) {
        super(documento, ValueType.UPLOAD);
    }
    
}
