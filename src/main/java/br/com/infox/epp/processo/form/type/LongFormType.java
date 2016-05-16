package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.variable.value.TypedValue;

public class LongFormType implements FormType {
    
    public final static String TYPE_NAME = "long";

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public TypedValue convertToFormValue(Object object) {
        return null;
    }

    @Override
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        return propertyValue;
    }

}
