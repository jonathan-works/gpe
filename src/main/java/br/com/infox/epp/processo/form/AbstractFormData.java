package br.com.infox.epp.processo.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;

import org.jbpm.context.def.VariableAccess;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.type.FormTypes;
import lombok.Getter;

public abstract class AbstractFormData implements FormData {
    
    protected Holder<Processo> processo;
    @Getter
    protected String formKey;
    @Getter
    protected Map<String, FormType> formTypes = new HashMap<>();
    @Getter
    protected List<FormField> formFields = new ArrayList<FormField>();
    @Getter
    protected List<FormField> formFieldsReadOnly = new ArrayList<FormField>();
    
    public AbstractFormData(String formKey, Holder<Processo> processo) {
        this.formKey = formKey;
        this.processo = processo;
    }
    
    protected abstract void createFormFields(List<VariableAccess> variableAccesses);

    protected void createFormField(VariableAccess variableAccess) {
        String variableName = variableAccess.getVariableName();
        String mappedName = variableAccess.isWritable() ? variableAccess.getMappedName() : variableAccess.getVariableName();
        FormField formField = new FormField();
        FormType formType = createFormType(variableAccess.getType());
        formField.setType(formType);
        formField.setId(variableName);
        formField.setLabel(variableAccess.getLabel());
        formField.setValue(formType.convertToFormValue(getVariable(mappedName)));
        formField.setProperties(createProperties(variableAccess));
        formType.performValue(formField, this);
        if (variableAccess.isWritable()) {
            getFormFields().add(formField);
        } else {
            getFormFieldsReadOnly().add(formField);
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
    public boolean isInvalid() {
        boolean valid = false;
        for (FormField formField : getFormFields()) {
            valid = valid | formField.getType().isInvalid(formField, this);
        }
        return valid;
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
    
    @Override
    public Processo getProcesso() {
        return processo.value;
    }

}
