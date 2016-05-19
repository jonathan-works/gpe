package br.com.infox.epp.processo.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.type.FormTypes;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.service.VariavelInicioProcessoService;
import br.com.infox.ibpm.util.JbpmUtil;

public class StartFormDataImpl extends AbstractFormData implements StartFormData {
    
    protected ProcessDefinition processDefinition;
    protected ExpressionResolverChain expressionResolver;
    
    public StartFormDataImpl(Processo processo, ProcessDefinition processDefinition) {
        super("startForm", processo);
        this.processDefinition = processDefinition;
        this.expressionResolver = new ExpressionResolverChain(new SeamExpressionResolver());
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
            formField.setTypedValue(formType.convertToFormValue(getVariable(variableName)));
            formField.setProperties(createProperties(variableAccess));
            formType.performValue(formField, this);
            getFormFields().add(formField);
        }
    }
    
    private FormType createFormType(String type) {
        FormType formType = getFormTypes().get(type);
        if (formType == null) {
            formType = FormTypes.valueOf(type).create();
            getFormTypes().put(type, formType);
        }
        return formType;
    }
    
    private Map<String, String> createProperties(VariableAccess variableAccess) {
        Map<String, String> properties = new HashMap<>();
        if (variableAccess.isRequired()) {
            properties.put("required", "true");
        }
        if (!variableAccess.isWritable()) {
            properties.put("readonly", "true");
        }
        return properties;
    }
    
    @Override
    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }
    
    public Map<String, Object> getVariables() {
        Map<String, Object> variables = new HashMap<>();
        for (FormField formField : getFormFields()) {
            TypedValue typedValue = formField.getTypedValue();
            variables.put(formField.getId(), typedValue.getType().convertToModelValue(typedValue));
        }
        return variables;
    }
    
    @Override
    public void update() {
        for (FormField formField : getFormFields()) {
            formField.getType().performUpdate(formField, this);
            setVariable(formField.getId(), formField.getTypedValue());
        }
    }

    @Override
    public Object getVariable(String name) {
        return getVariavelService().getVariavel(processo, name); 
    }

    @Override
    public void setVariable(String name, Object value) {
        getVariavelService().setVariavel(processo, name, (TypedValue) value);
    }

    public VariavelInicioProcessoService getVariavelService() {
        return BeanManager.INSTANCE.getReference(VariavelInicioProcessoService.class);
    }

    @Override
    public ExpressionResolverChain getExpressionResolver() {
        return expressionResolver;
    }

}
