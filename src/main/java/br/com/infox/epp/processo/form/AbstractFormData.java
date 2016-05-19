package br.com.infox.epp.processo.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;

public abstract class AbstractFormData implements FormData {
    
    protected String formKey;
    protected Processo processo;
    protected Map<String, FormType> formTypes = new HashMap<>();
    protected List<FormField> formFields = new ArrayList<FormField>();
    
    public AbstractFormData(String formKey, Processo processo) {
        this.formKey = formKey;
        this.processo = processo;
    }

    public String getFormKey() {
      return formKey;
    }

    public void setFormKey(String formKey) {
      this.formKey = formKey;
    }

    public List<FormField> getFormFields() {
      return formFields;
    }
    
    public void setFormFields(List<FormField> formFields) {
      this.formFields = formFields;
    }
    
    public Map<String, FormType> getFormTypes() {
        return formTypes;
    }

    public void setFormTypes(Map<String, FormType> formTypes) {
        this.formTypes = formTypes;
    }

    @Override
    public Processo getProcesso() {
        return processo;
    }
    
}
