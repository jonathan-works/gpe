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
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.service.VariavelInicioProcessoService;

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
        createFormFields(variableAccesses, getProcessDefinition());
    }

    @Override
    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }
    
    public Map<String, Object> getVariables() {
        Map<String, Object> variables = new HashMap<>();
        for (FormField formField : getFormFields()) {
            TypedValue typedValue = formField.getTypedValue();
            if (formField.getType().isPersistable() && typedValue.getValue() != null) {
                variables.put(formField.getId(), typedValue.getType().convertToModelValue(typedValue).getValue());
            }
        }
        return variables;
    }
    
    @Override
    public void update() {
        for (FormField formField : getFormFields()) {
            if (formField.getType().isPersistable()) {
                formField.getType().performUpdate(formField, this);
                setVariable(formField.getId(), formField.getTypedValue());
            }
        }
    }

    @Override
    public Object getVariable(String name) {
        return getVariavelService().getVariavel(processo, name); 
    }

    @Override
    public void setVariable(String name, TypedValue value) {
        getVariavelService().setVariavel(processo, name, value);
    }

    public VariavelInicioProcessoService getVariavelService() {
        return BeanManager.INSTANCE.getReference(VariavelInicioProcessoService.class);
    }

    @Override
    public ExpressionResolverChain getExpressionResolver() {
        return expressionResolver;
    }

}
