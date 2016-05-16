package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValueImpl;
import br.com.infox.epp.processo.form.variable.value.TypedValue;

public class UploadFormType extends FileFormType {
    
    public final static String TYPE_NAME = "upload";
    
    public UploadFormType(FormData formData) {
        super(formData);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public TypedValue convertToFormValue(Object value) {
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
