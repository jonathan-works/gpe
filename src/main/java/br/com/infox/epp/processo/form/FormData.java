package br.com.infox.epp.processo.form;

import java.util.List;
import java.util.Map;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;

public interface FormData {
    
    String getFormKey();
    
    Processo getProcesso();

    List<FormField> getFormFields();
    
    Map<String, FormType> getFormTypes();
    
    Object getVariable(String name);
    
    void putVariable(String name, Object value);
    
    Object evaluate(String expression);
    
}
