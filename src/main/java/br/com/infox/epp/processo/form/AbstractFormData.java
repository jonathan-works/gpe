package br.com.infox.epp.processo.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.type.FormTypes;
import br.com.infox.ibpm.util.JbpmUtil;

public abstract class AbstractFormData implements FormData {
    
    protected String formKey;
    protected Processo processo;
    protected Map<String, FormType> formTypes = new HashMap<>();
    protected List<FormField> formFields = new ArrayList<FormField>();
    
    public AbstractFormData(String formKey, Processo processo) {
        this.formKey = formKey;
        this.processo = processo;
    }
    
    protected void createFormFields(List<VariableAccess> variableAccesses, ProcessDefinition processDefinition) {
        for (VariableAccess variableAccess : variableAccesses) {
            String type = variableAccess.getMappedName().split(":")[0];
            String variableName = variableAccess.getVariableName();
            String label = JbpmUtil.instance().getMessages().get(processDefinition.getName() + ":" + variableName);
            FormField formField = new FormField();
            FormType formType = createFormType(type);
            formField.setType(formType);
            formField.setId(variableName);
            formField.setLabel(label);
            formField.setTypedValue(formType.convertToFormValue(getVariable(variableName)));
            formField.setProperties(createProperties(variableAccess));
            formType.performValue(formField, this);
            getFormFields().add(formField);
        }
    }
    
    protected FormType createFormType(String type) {
        FormType formType = getFormTypes().get(type);
        if (formType == null) {
            formType = FormTypes.valueOf(type).create();
            getFormTypes().put(type, formType);
        }
        return formType;
    }
    
    protected Map<String, String> createProperties(VariableAccess variableAccess) {
        Map<String, String> properties = new HashMap<>();
        if (variableAccess.isRequired()) {
            properties.put("required", "true");
        }
        if (!variableAccess.isWritable()) {
            properties.put("readonly", "true");
        }
        String[] tokens = variableAccess.getMappedName().split(":");
        if (tokens.length > 2) {
            properties.put("extendedProperties", tokens[2]);
        }
        return properties;
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
