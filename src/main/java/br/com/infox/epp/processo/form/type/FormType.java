package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.seam.exception.BusinessException;

public interface FormType {
    
    String getName();
    
    String getPath();
    
    ValueType getValueType();
    
    Object convertToFormValue(Object value);
    
    void performValue(FormField formField, FormData formData);
    
    void performUpdate(FormField formField, FormData formData);
    
    boolean validate(FormField formField, FormData formData);
    
    boolean isPersistable();
    
}
