package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.seam.exception.BusinessException;

public interface FormType {
    
    String getName();
    
    ValueType getValueType();
    
    TypedValue convertToFormValue(Object value);
    
    void performValue(FormField formField, FormData formData);
    
    void performUpdate(FormField formField, FormData formData);
    
    void validate(FormField formField, FormData formData) throws BusinessException;
    
    boolean isPersistable();
    
}
