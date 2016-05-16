package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValueImpl;
import br.com.infox.epp.processo.form.variable.value.TypedValue;

public class StringFormType implements FormType {
    
    public final static String TYPE_NAME = "string";

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public TypedValue convertToFormValue(Object object) {
        if (object instanceof String) {
            return new PrimitiveTypedValueImpl.StringValue((String) object);
        }
        return null;
    }

    @Override
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        return propertyValue;
    }

}
