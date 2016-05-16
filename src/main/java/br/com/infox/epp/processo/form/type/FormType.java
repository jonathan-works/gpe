package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.variable.value.TypedValue;

public interface FormType {

    String getName();
    
    TypedValue convertToFormValue(Object value);

    TypedValue convertToModelValue(TypedValue propertyValue);

}
