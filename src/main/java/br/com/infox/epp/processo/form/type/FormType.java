package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;

public interface FormType {

    String getName();
    
    ValueType getValueType();
    
    TypedValue convertToFormValue(Object value);
    
    void performValue(FormField formField, FormData formData);
    
    void performUpdate(FormField formField, FormData formData);
    
    boolean isPersistable();
    
}
