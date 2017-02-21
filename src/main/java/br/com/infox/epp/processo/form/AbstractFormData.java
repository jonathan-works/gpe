package br.com.infox.epp.processo.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.context.def.VariableAccess;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.type.FormTypes;
import br.com.infox.seam.exception.BusinessException;

public abstract class AbstractFormData implements FormData {
    
    protected String formKey;
    protected Processo processo;
    protected Map<String, FormType> formTypes = new HashMap<>();
    protected List<FormField> formFields = new ArrayList<FormField>();
    
    public AbstractFormData(String formKey, Processo processo) {
        this.formKey = formKey;
        this.processo = processo;
    }
    
    protected abstract void createFormFields(List<VariableAccess> variableAccesses);
    

    protected void createFormField(VariableAccess variableAccess) {
        String variableName = variableAccess.getVariableName();
        FormField formField = new FormField();
        FormType formType = createFormType(variableAccess.getType());
        formField.setType(formType);
        formField.setId(variableName);
        formField.setLabel(variableAccess.getLabel());
        formField.setValue(formType.convertToFormValue(getVariable(variableName)));
        formField.setProperties(createProperties(variableAccess));
        formType.performValue(formField, this);
        getFormFields().add(formField);
    }
    
    protected FormType createFormType(String type) {
        FormType formType = getFormTypes().get(type);
        if (formType == null) {
            formType = FormTypes.valueOf(type).create();
            getFormTypes().put(type, formType);
        }
        return formType;
    }
    
    protected Map<String, Object> createProperties(VariableAccess variableAccess) {
        Map<String, Object> properties = new HashMap<>();
        if (variableAccess.isRequired()) {
            properties.put("required", "true");
        }
        if (!variableAccess.isWritable()) {
            properties.put("readonly", "true");
        }
        if (variableAccess.getConfiguration() != null && !variableAccess.getConfiguration().isEmpty()) {
        	properties.put("configuration", variableAccess.getConfiguration());
        }
        return properties;
    }
    
    @Override
    public boolean validate() {
        boolean validacao = false;
        for (FormField formField : getFormFields()) {
            validacao = validacao | formField.getType().validate(formField, this);
        }
        return validacao;
    }
    
    protected VariableAccess getTaskPage(List<VariableAccess> variableAccesses) {
        for (VariableAccess variableAccess : variableAccesses) {
            String type = variableAccess.getMappedName().split(":")[0];
            if ("TASK_PAGE".equalsIgnoreCase(type)) {
                return variableAccess;
            }
        }
        return null;
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
