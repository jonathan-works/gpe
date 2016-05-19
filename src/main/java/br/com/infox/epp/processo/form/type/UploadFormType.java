package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;

public class UploadFormType extends FileFormType {
    
    public UploadFormType() {
        super("upload", ValueType.UPLOAD);
    }

    @Override
    public TypedValue convertToFormValue(Object value) {
        if (value instanceof String) {
            value = Integer.valueOf((String) value);
        }
        if (value instanceof Integer) {
//            Documento documento = getDocumentoManager().find((Integer) value);
//            return new UploadValueImpl(documento);
        }
        return null;
    }
}
