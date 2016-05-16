package br.com.infox.epp.processo.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.EditorFormType;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.type.StringFormType;
import br.com.infox.ibpm.util.JbpmUtil;

public class StartFormDataImpl extends AbstractFormData implements StartFormData {
    
    protected ProcessDefinition processDefinition;
    
    public StartFormDataImpl(Processo processo, ProcessDefinition processDefinition) {
        super(processo);
        this.processDefinition = processDefinition;
        createFormFields();
    }

    private void createFormFields() {
        List<VariableAccess> variableAccesses = getProcessDefinition().getTaskMgmtDefinition().getStartTask().getTaskController().getVariableAccesses();
        for (VariableAccess variableAccess : variableAccesses) {
            String type = variableAccess.getMappedName().split(":")[0];
            String variableName = variableAccess.getVariableName();
            String label = JbpmUtil.instance().getMessages().get(getProcessDefinition().getName() + ":" + variableName);
            FormField formField = new FormField();
            FormType formType = createFormType(type);
            formField.setType(formType);
            formField.setId(variableName);
            formField.setLabel(label);
            formField.setValue(formType.convertToFormValue(getVariable(variableName)));
            formField.setProperties(createProperties(variableAccess));
            getFormFields().add(formField);
        }
    }
    
    private FormType createFormType(String type) {
        FormType formType = null;
        if (EditorFormType.TYPE_NAME.equalsIgnoreCase(type)) {
            formType = getFormTypes().get(EditorFormType.TYPE_NAME);
            if (formType == null) {
                formType = new EditorFormType(this);
                getFormTypes().put(formType.getName(), formType);
            }
        } else if (StringFormType.TYPE_NAME.equalsIgnoreCase(type)) {
            formType = getFormTypes().get(StringFormType.TYPE_NAME);
            if (formType == null) {
                formType = new StringFormType();
                getFormTypes().put(formType.getName(), formType);
            }
        }
        return formType;
    }
    
    private Map<String, String> createProperties(VariableAccess variableAccess) {
        Map<String, String> properties = new HashMap<>();
        if (variableAccess.isRequired()) {
            properties.put("required", "true");
        }
        return properties;
    }
    
    @Override
    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    @Override
    public Object getVariable(String name) {
        return null;
    }

    @Override
    public void putVariable(String name, Object value) {
        
    }

    @Override
    public Object evaluate(String expression) {
        return null;
    }

}
