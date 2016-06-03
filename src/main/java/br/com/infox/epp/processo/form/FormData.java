package br.com.infox.epp.processo.form;

import java.util.List;
import java.util.Map;

import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.seam.exception.BusinessException;

public interface FormData {
    
    String getFormKey();
    
    Processo getProcesso();

    List<FormField> getFormFields();
    
    Map<String, FormType> getFormTypes();
    
    Object getVariable(String name);
    
    void setVariable(String name, Object value);
    
    void update();
    
    Map<String, Object> getVariables();
    
    ExpressionResolverChain getExpressionResolver();
    
    void validate() throws BusinessException;
    
}
