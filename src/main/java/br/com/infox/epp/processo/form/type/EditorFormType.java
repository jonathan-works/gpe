package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.variable.value.EditorValueImpl;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValueImpl;
import br.com.infox.epp.processo.form.variable.value.TypedValue;

public class EditorFormType extends FileFormType {

    public final static String TYPE_NAME = "editor";

    public EditorFormType(FormData formData) {
        super(formData);
    }
    
    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public TypedValue convertToFormValue(Object value) {
        if (value == null) {
            Documento documento = new Documento();
            documento.setProcesso(formData.getProcesso());
            documento.setAnexo(false);
            documento.setDocumentoBin(new DocumentoBin());
            return new EditorValueImpl(documento);
        } else {
            if (value instanceof Integer) {
                Documento documento = getDocumentoManager().find((Integer) value);
                return new EditorValueImpl(documento);
            }
        }
        return null;
    }

    @Override
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        Object value = propertyValue.getValue();
        if (value instanceof Documento) {
            return new PrimitiveTypedValueImpl.IntegerValue(((Documento) value).getId());
        }
        throw new IllegalArgumentException("Impossible convert " + propertyValue);
    }
    
}
